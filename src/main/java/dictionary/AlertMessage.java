package dictionary;

public enum AlertMessage {
    NOT_SIGNED("Проверьте, подписаны ли вы на все каналы"),
    YET_PARTICIPATE("Вы уже участвуете в розыгрыше"),
    START_PARTICIPATE("Вы присоединились к участию в розыгрыше");

    private final String text;

    AlertMessage(String str) {
        this.text = str;
    }

    public String getText() {
        return text;
    }
}
