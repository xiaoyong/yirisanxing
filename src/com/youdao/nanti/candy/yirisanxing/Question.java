package com.youdao.nanti.candy.yirisanxing;

import java.util.List;

import android.database.Cursor;

class Question {
    private long id;
    private String question;
    private boolean isEnabled;
    private int repeatType;
    private int interval;
    private String daysOfWeek;
    private int alertType;
    private int hour;
    private int minute;
    private long created;
    private long updated;
    private List<Option> options;
    
    public Question(Cursor cursor) {
        id = cursor.getLong(0);
        question = cursor.getString(1);
        isEnabled = (cursor.getInt(2) == 1);
        repeatType = cursor.getInt(3);
        interval = cursor.getInt(4);
        daysOfWeek = cursor.getString(5);
        alertType = cursor.getInt(6);
        hour = cursor.getInt(7);
        minute = cursor.getInt(8);
        created = cursor.getLong(9);
        updated = cursor.getLong(10);
    }
    
    public Question() {
        isEnabled = true;
        created = System.currentTimeMillis();
        updated = created;
    }

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
    public boolean getIsEnabled() {
        return isEnabled;
    }
    public void setIsEnabled(boolean isEnabled) {
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
    public String getDaysOfWeek() {
        return daysOfWeek;
    }
    public void setDaysOfWeek(String daysOfWeek) {
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
    public long getCreated() {
        return created;
    }
    public void setCreated(long created) {
        this.created = created;
    }
    public long getUpdated() {
        return updated;
    }
    public void setUpdated(long updated) {
        this.updated = updated;
    }
    public List<Option> getOptions() {
        return options;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
}