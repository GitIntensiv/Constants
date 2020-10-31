package dictionary;

public enum Button {
    CREATE_NEW_CONSTANT("Создать новый розыгрыш"),
    SEE_ALL_CONSTANTS("Просмотреть все розыгрыши"),
    STOP_IN("Далее"),
    GET_BACK_MENU("Вернуться в главное меню"),
    DELETE_CONSTANT("Удалить конкурс"),
    RESET_DATA("Сбросить и вернутся в главное меню")
    ;

    private String text;

    Button(String str) {
        this.text = str;
    }

    public String getText() {
        return text;
    }
}
