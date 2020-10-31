import bot.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import scheduler.PublisherPost;

import java.util.Timer;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        Timer time = new Timer();
        PublisherPost st = new PublisherPost();
        time.schedule(st, 0, 10_000);
        try {
            botsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            log.error(e.getMessage());
        }
    }
}
