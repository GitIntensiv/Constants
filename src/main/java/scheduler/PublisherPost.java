package scheduler;

import base.H2;
import bot.Bot;
import dictionary.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import model.Constant;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class PublisherPost extends TimerTask {

    private final Bot bot;

    public PublisherPost() {
        bot = new Bot();
    }

    @Override
    public void run() {
        try {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now().withNano(0).withSecond(0));
            int idStart = H2.getInstance().getExistConstantIdForNow(now,
                    BaseRequest.GET_CONSTANT_BY_TIME_FOR_START.getText());
            if (idStart != 0) {
                Constant constant = H2.getInstance().getConstantById(idStart);
                List<Long> channelsForStart = H2.getInstance().getChannelsForConstantById(idStart);
                bot.sendStartMessage(constant, channelsForStart);
                H2.getInstance().updateConstantStarted(idStart);
            }
            int idFinished = H2.getInstance().getExistConstantIdForNow(now,
                    BaseRequest.GET_CONSTANT_BY_TIME_FOR_FINISH.getText());
            if (idFinished != 0) {
                Constant constant = H2.getInstance().getConstantById(idFinished);
                HashMap<Long, Integer> channelsForFinish = H2.getInstance()
                        .getChannelsAndMessagesForConstantById(idFinished);
                bot.sendEndMessage(constant, channelsForFinish);
                H2.getInstance().updateConstantFinished(idFinished);
                H2.getInstance().deleteAllByConstantId(idFinished);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
