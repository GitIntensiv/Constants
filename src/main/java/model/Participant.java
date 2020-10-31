package model;

public class Participant {
    private Long id;
    private String name;
    private String username;
    private Integer telegramId;
    private Long constantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Integer telegramId) {
        this.telegramId = telegramId;
    }

    public Long getConstantId() {
        return constantId;
    }

    public void setConstantId(long constantId) {
        this.constantId = constantId;
    }
}
