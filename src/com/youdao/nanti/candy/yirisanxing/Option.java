package com.youdao.nanti.candy.yirisanxing;

import android.database.Cursor;

public class Option {
    private long id;
    private long questionId;
    private String option;
    private int value;
    
    public Option(Cursor cursor) {
        id = cursor.getLong(0);
        questionId = cursor.getLong(1);
        option = cursor.getString(2);
        value = cursor.getInt(3);
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
    public String getOption() {
        return option;
    }
    public void setOption(String option) {
        this.option = option;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

}