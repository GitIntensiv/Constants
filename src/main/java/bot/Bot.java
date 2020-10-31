package bot;

import base.H2;
import dictionary.AlertMessage;
import dictionary.Button;
import lombok.extern.slf4j.Slf4j;
import model.Constant;
import model.Participant;
import model.Store;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import util.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dictionary.Button.*;

@Slf4j
public class Bot extends TelegramLongPollingBot {
    private final HashMap<Integer, Store> accessAllowed;

    public Bot() {
        accessAllowed = new HashMap<>();
        accessAllowed.put(1072735921, new Store());
        accessAllowed.put(935940625, new Store());
        accessAllowed.put(144931884, new Store());
    }

    @Override
    public void onUpdateReceived(Update update) {
        Runnable task = () -> {
            Message msg = update.getMessage();
            if (msg != null && !update.hasCallbackQuery()) {
                if (accessAllowed.containsKey(msg.getFrom().getId())) {
                    Store store = accessAllowed.get(msg.getFrom().getId());
                    String text = msg.getText();
                    if (text != null && text.equals("/start")) {
                        accessAllowed.put(msg.getFrom().getId(), new Store());
                        sendStartMessage(msg.getChatId());
                    }
                    if (text != null && text.equals(RESET_DATA.getText())) {
                        H2.getInstance().deleteAllByConstantId(store.getCurrentConstantId());
                        accessAllowed.put(msg.getFrom().getId(), new Store());
                        sendStartMessage(msg.getChatId());
                        return;
                    }
                    if (text != null && text.equals(CREATE_NEW_CONSTANT.getText())) {
                        store.setAddChannels(true);
                        createNewConstant(msg.getChatId(), store);
                    }
                    if (store.isAddChannels() && msg.getForwardFromChat() != null) {
                        addNewChannel(msg, store);
                    }
                    if (text != null && text.equals(STOP_IN.getText())) {
                        store.setAddChannels(false);
                        store.setAddText(true);
                        sendTextMessage(msg.getChatId());
                        return;
                    }
                    if (store.isAddText() && text != null) {
                        store.setAddText(false);
                        store.setAddStartTime(true);
                        sendStartTimeMessage(msg, store);
                        return;
                    }
                    if (store.isAddStartTime() && text != null) {
                        store.setAddStartTime(false);
                        store.setAddEndTime(true);
                        sendEndTimeMessage(msg, store);
                        return;
                    }
                    if (store.isAddEndTime() && text != null) {
                        store.setAddEndTime(false);
                        store.setAddCountWinners(true);
                        sendWinnersMessage(msg, store);
                        return;
                    }
                    if (store.isAddCountWinners() && text != null) {
                        store.setAddCountWinners(false);
                        sendSuccessfulMessage(msg, store);
                        return;
                    }
                    if (text != null && text.equals(GET_BACK_MENU.getText())) {
                        accessAllowed.put(msg.getFrom().getId(), new Store());
                        sendStartMessage(msg.getChatId());
                    }
                    if (text != null && text.equals(SEE_ALL_CONSTANTS.getText())) {
                        getAllConstant(msg.getChatId());
                    }
                    if (text != null && text.equals(DELETE_CONSTANT.getText())) {
                        store.setDeleteConstant(true);
                        sendDeleteMessage(msg.getChatId());
                        return;
                    }
                    if (store.isDeleteConstant() && text != null) {
                        store.setDeleteConstant(false);
                        sendSuccessfulDeleteMessage(msg);
                    }
                } else {
                    sendCancelMessage(msg.getChatId());
                    return;
                }
            }
            CallbackQuery query = update.getCallbackQuery();
            if (query != null) {
                if (query.getData().equals("/participate")) {
                    Integer userId = query.getFrom().getId();
                    Long chatId = query.getMessage().getChatId();
                    long constantId = H2.getInstance().getConstantByTelegramChannel(chatId);
                    AnswerCallbackQuery answer = new AnswerCallbackQuery();
                    answer.setCallbackQueryId(query.getId());
                    List<Long> channelsForSubscription = H2.getInstance().getChannelsForConstantById(constantId);

                    for (Long id : channelsForSubscription) {
                        if (!checkSignOnChanel(answer, userId, id)) {
                            return;
                        }
                    }

                    if (H2.getInstance().isExitUsername(userId, constantId)) {
                        answer.setText(AlertMessage.YET_PARTICIPATE.getText());
                        sendBaseAnswer(answer);
                    } else {
                        answer.setText(AlertMessage.START_PARTICIPATE.getText());
                        sendBaseAnswer(answer);
                        User user = query.getFrom();
                        if (user != null) {
                            Participant participant = new Participant();
                            participant.setName(user.getFirstName());
                            participant.setTelegramId(userId);
                            participant.setUsername("https://t.me/" + user.getUserName());
                            participant.setConstantId(constantId);
                            H2.getInstance().insertParticipant(participant);
                            sendEditMessage(constantId);
                        }
                    }
                }
            }
        };
        task.run();
    }

    @Override
    public String getBotUsername() {
        return "ContestBot";
    }

    @Override
    public String getBotToken() {
        return "1353766782:AAFkd4Pgc9yv8QgcD9J45qMkwJhxWNI-5oE";
    }

    private void sendStartMessage(Long chatId) {
        SendMessage msg = new SendMessage().setChatId(chatId);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(CREATE_NEW_CONSTANT.getText());
        row.add(Button.SEE_ALL_CONSTANTS.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setText(dictionary.Message.CHOICE_START_ACTIVITY.getText());
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void createNewConstant(Long chatId, Store store) {
        store.setCurrentConstantId(H2.getInstance().insertNewConstant());
        SendMessage msg = new SendMessage().setChatId(chatId);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(RESET_DATA.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setReplyMarkup(keyboard);
        msg.setText(dictionary.Message.REPLAY_MESSAGE.getText());
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void addNewChannel(Message message, Store store) {
        Long channelId = message.getForwardFromChat().getId();
        H2.getInstance().insertNewChannel(channelId, store.getCurrentConstantId());
        SendMessage msg = new SendMessage().setChatId(message.getChatId());
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(STOP_IN.getText());
        row.add(RESET_DATA.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setText(dictionary.Message.CONTINUE_ADDING.getText());
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendTextMessage(Long chatId) {
        SendMessage msg = new SendMessage().setChatId(chatId);
        msg.setText(dictionary.Message.GET_TEXT.getText());
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(RESET_DATA.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendStartTimeMessage(Message message, Store store) {
        H2.getInstance().updateConstantText(store.getCurrentConstantId(), message.getText());
        SendMessage msg = new SendMessage().setChatId(message.getChatId());
        msg.setText(dictionary.Message.GET_START_TIME.getText());
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendEndTimeMessage(Message message, Store store) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime start = LocalDateTime.parse(message.getText(), formatter);
        H2.getInstance().updateConstantStart(store.getCurrentConstantId(), start);
        SendMessage msg = new SendMessage().setChatId(message.getChatId());
        msg.setText(dictionary.Message.GET_END_TIME.getText());
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendWinnersMessage(Message message, Store store) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime end = LocalDateTime.parse(message.getText(), formatter);
        H2.getInstance().updateConstantEnd(store.getCurrentConstantId(), end);
        SendMessage msg = new SendMessage().setChatId(message.getChatId());
        msg.setText(dictionary.Message.GET_COUNT_WINNERS.getText());
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendSuccessfulMessage(Message message, Store store) {
        int count = Integer.parseInt(message.getText());
        H2.getInstance().updateConstantWinners(store.getCurrentConstantId(), count);
        SendMessage msg = new SendMessage().setChatId(message.getChatId());
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(GET_BACK_MENU.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setText(dictionary.Message.CONSTANT_CREATED.getText());
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendSuccessfulDeleteMessage(Message message) {
        H2.getInstance().deleteAllByConstantId(Integer.parseInt(message.getText()));
        SendMessage msg = new SendMessage().setChatId(message.getChatId());
        msg.setText(dictionary.Message.SUCCESSFUL_DELETE.getText());
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(GET_BACK_MENU.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendCancelMessage(Long chatId) {
        SendMessage msg = new SendMessage().setChatId(chatId);
        msg.setText(dictionary.Message.CANCEL_MESSAGE.getText());
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendDeleteMessage(Long chatId) {
        SendMessage msg = new SendMessage().setChatId(chatId);
        msg.setText(dictionary.Message.INPUT_ID_FOR_DELETING.getText());
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        msg.setReplyMarkup(remove);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendStartMessage(Constant constant, List<Long> channels) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setText(constant.getText())
                .disableWebPagePreview();

        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().setText(dictionary.Message.MESSAGE_ON_BUTTON.getText())
                .setCallbackData("/participate"));
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        replyKeyboard.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboard);

        for (Long id : channels) {
            sendMessage.setChatId(id);
            try {
                Message sendFirstMessage = sendApiMethod(sendMessage);
                H2.getInstance().updateChannelMessageId(id, sendFirstMessage.getMessageId());
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void sendEndMessage(Constant constant, HashMap<Long, Integer> channels) {
        EditMessageText new_message = new EditMessageText()
                .setText(Util.createTextWithWinners(constant))
                .enableMarkdown(true)
                .disableWebPagePreview();

        for (Map.Entry<Long, Integer> pair : channels.entrySet()) {
            new_message.setChatId(pair.getKey());
            new_message.setMessageId(pair.getValue());
            try {
                execute(new_message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void getAllConstant(Long chatId) {
        SendMessage msg = new SendMessage().setChatId(chatId).enableMarkdown(true);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(GET_BACK_MENU.getText());
        row.add(DELETE_CONSTANT.getText());
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        msg.setText(Util.printConstants(H2.getInstance().getAllConstant()));
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private boolean checkSignOnChanel(AnswerCallbackQuery answer, Integer userId, Long channelId) {
        if (!checkUserIsSigned(userId, channelId)) {
            answer.setText(AlertMessage.NOT_SIGNED.getText());
            sendBaseAnswer(answer);
            return false;
        }
        return true;
    }

    private boolean checkUserIsSigned(Integer userId, long chatId) {
        String status = "";
        GetChatMember chatMember = new GetChatMember();
        chatMember.setChatId(chatId);
        chatMember.setUserId(userId);
        try {
            status = execute(chatMember).getStatus();
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return status.equals("member") || status.equals("creator");
    }

    private void sendBaseAnswer(AnswerCallbackQuery answer) {
        try {
            answerCallbackQuery(answer);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendEditMessage(Long constantId) {
        Constant constant = H2.getInstance().getConstantById(constantId);
        HashMap<Long, Integer> channelsForEdit = H2.getInstance()
                .getChannelsAndMessagesForConstantById(constantId);
        EditMessageText new_message = new EditMessageText()
                .setText(constant.getText())
                .enableMarkdown(true)
                .disableWebPagePreview();

        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton()
                .setText(dictionary.Message.MESSAGE_ON_BUTTON.getText() + " "
                        + H2.getInstance().getAllParticipantsByConstantId(constant.getId()).size())
                .setCallbackData("/participate"));
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        replyKeyboard.setKeyboard(rows);
        new_message.setReplyMarkup(replyKeyboard);

        for (Map.Entry<Long, Integer> pair : channelsForEdit.entrySet()) {
            new_message.setChatId(pair.getKey());
            new_message.setMessageId(pair.getValue());
            try {
                execute(new_message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }
}
