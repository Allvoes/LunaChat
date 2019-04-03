package com.allvoes.lunachat;

public class messages {
    private String message,type,from;
    private Long time;
    private boolean seen;


    public messages() {
    }

    public messages(String message, String type, String from, Long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
