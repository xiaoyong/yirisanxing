package com.youdao.nanti.candy.yirisanxing;

import java.util.List;

class Question {
    private long id;
    private String question;
    private int isEnabled;
    private int repeatType;
    private int interval;
    private int daysOfWeek;
    private int alertType;
    private int hour;
    private int minute;
    private int created;
    private int updated;
    private List<Option> options;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public int getIsEnabled() {
        return isEnabled;
    }
    public void setIsEnabled(int isEnabled) {
        this.isEnabled = isEnabled;
    }
    public int getRepeatType() {
        return repeatType;
    }
    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }
    public int getInterval() {
        return interval;
    }
    public void setInterval(int interval) {
        this.interval = interval;
    }
    public int getDaysOfWeek() {
        return daysOfWeek;
    }
    public void setDaysOfWeek(int daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
    public int getAlertType() {
        return alertType;
    }
    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public int getCreated() {
        return created;
    }
    public void setCreated(int created) {
        this.created = created;
    }
    public int getUpdated() {
        return updated;
    }
    public void setUpdated(int updated) {
        this.updated = updated;
    }
    public List<Option> getOptions() {
        return options;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
}