package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constant {
    private Long id;
    private String text;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer countWinners;
    private boolean isStarted;
    private boolean isEnded;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Integer getCountWinners() {
        return countWinners;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setCountWinners(Integer countWinners) {
        this.countWinners = countWinners;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String status = "";
        if (!isStarted) {
            status = "Не начат";
        }
        if (isStarted && !isEnded) {
            status = "В процессе";
        }
        if (isEnded) {
            status = "Завершен";
        }
        return "*Конкурс с номером: " + id + "*" +
                "\nТекст: " + text +
                "\nДата и время начала: " + (start == null ? "Не установлена" : start.format(formatter)) +
                "\nДата и время окончания: " + (end == null ? "Не установлена" : end.format(formatter)) +
                "\nКоличество победителей: " + (countWinners == null ? "Не установлен" : countWinners)  +
                "\nСтатус: " + status;
    }
}
