package dictionary;

public enum BaseRequest {
    CREATE_CONSTANT("create table CONSTANT\n" +
            "(\n" +
            "id int auto_increment,\n" +
            "text text,\n" +
            "start datetime,\n" +
            "end datetime,\n" +
            "winners int, \n" +
            "started bool default false, \n" +
            "finished bool default false, \n" +
            ");\n" +
            "\n" +
            "create unique index CONSTANT_ID_UINDEX\n" +
            "on CONSTANT (id);\n" +
            "\n" +
            "alter table CONSTANT\n" +
            "add constraint CONSTANT_PK\n" +
            "primary key (id);\n" +
            "\n"),
    INSERT_CONSTANT("insert into CONSTANT (text, start, end, winners) " +
            "values ((?), (?), (?), (?))"),
    DELETE_CONSTANT_BY_ID("delete from CONSTANT where ID = (?)"),
    UPDATE_CONSTANT_TEXT("update CONSTANT set TEXT = (?) where ID = (?)"),
    UPDATE_CONSTANT_START("update CONSTANT set START = (?) where ID = (?)"),
    UPDATE_CONSTANT_END("update CONSTANT set END = (?) where ID = (?)"),
    UPDATE_CONSTANT_WINNERS("update CONSTANT set WINNERS = (?) where ID = (?)"),
    UPDATE_CONSTANT_STARTED("update CONSTANT set STARTED = true where ID = (?)"),
    UPDATE_CONSTANT_FINISHED("update CONSTANT set FINISHED = true where ID = (?)"),
    GET_ALL_CONSTANT("select * from CONSTANT"),
    GET_CONSTANT_BY_TIME_FOR_START("select ID from CONSTANT where START = (?) and STARTED = false"),
    GET_CONSTANT_BY_TIME_FOR_FINISH("select ID from CONSTANT where END = (?) and FINISHED = false"),
    GET_CONSTANT_BY_ID("select * from CONSTANT where ID = (?)"),

    CREATE_CHANNEL("create table CHANNEL\n" +
            "(\n" +
            "id int auto_increment,\n" +
            "telegram_id long not null,\n" +
            "message long, \n" +
            "constant_id int not null,\n" +
            "constraint CHANNEL_CONSTANT_ID_FK\n" +
            "foreign key (constant_id) references CONSTANT\n" +
            ");\n" +
            "\n" +
            "create unique index CHANNEL_ID_UINDEX\n" +
            "on CHANNEL (id);\n" +
            "\n" +
            "alter table CHANNEL\n" +
            "add constraint CHANNEL_PK\n" +
            "primary key (id);\n" +
            "\n"),
    INSERT_CHANNEL("insert into CHANNEL (telegram_id, constant_id) values ((?), (?))"),
    UPDATE_CHANNEL_BY_MESSAGE_ID("update CHANNEL set MESSAGE = (?) where TELEGRAM_ID = (?)"),
    DELETE_CHANNEL_BY_CONSTANT_ID("delete from CHANNEL where CONSTANT_ID = (?)"),
    GET_CHANNEL_BY_CONSTANT_ID("select TELEGRAM_ID from CHANNEL where CONSTANT_ID = (?)"),
    GET_CHANNEL_AND_MESSAGE_BY_CONSTANT_ID("select TELEGRAM_ID, MESSAGE from CHANNEL where CONSTANT_ID = (?)"),
    GET_CONSTANT_BY_TELEGRAM_CHANNEL_ID("select CONSTANT_ID from CHANNEL where TELEGRAM_ID = (?)"),

    CREATE_PARTICIPANT("create table PARTICIPANT\n" +
            "(\n" +
            "id int auto_increment,\n" +
            "username text,\n" +
            "link text,\n" +
            "telegram_id int,\n" +
            "constant_id int,\n" +
            "constraint PARTICIPANT_CONSTANT_ID_FK\n" +
            "foreign key (constant_id) references CONSTANT\n" +
            ");\n" +
            "\n" +
            "create unique index PARTICIPANT_ID_UINDEX\n" +
            "on PARTICIPANT (id);\n" +
            "\n" +
            "alter table PARTICIPANT\n" +
            "add constraint PARTICIPANT_PK\n" +
            "primary key (id);\n" +
            "\n"),
    INSERT_PARTICIPANT("insert into PARTICIPANT (username, link, telegram_id, constant_id) " +
            "values ((?), (?), (?), (?))"),
    DELETE_PARTICIPANT_BY_CONSTANT_ID("delete from PARTICIPANT where CONSTANT_ID = (?)"),
    GET_PARTICIPANT_BY_TELEGRAM_ID_AND_CONSTANT("select id from PARTICIPANT " +
            "where TELEGRAM_ID = (?) and CONSTANT_ID = (?)"),
    GET_PARTICIPANTS_BY_CONSTANT_ID("select * from PARTICIPANT where CONSTANT_ID = (?)"),

    DELETE("DROP table (?)"),
    SHOW_TABLES("show TABLES");

    private final String text;

    BaseRequest(String str) {
        this.text = str;
    }

    public String getText() {
        return text;
    }
}
