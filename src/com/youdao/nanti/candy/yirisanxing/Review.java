package com.youdao.nanti.candy.yirisanxing;

import android.database.Cursor;

public class Review {
    private long id;
    private long questionId;
    private long optionId;
    private long reviewed;
    private long created;
    private String comment;
    
    public Review(Cursor cursor) {
        id = cursor.getLong(0);
        questionId = cursor.getLong(1);
        optionId = cursor.getLong(2);
        reviewed = cursor.getLong(3);
        created = cursor.getLong(4);
        comment = cursor.getString(5);
    }
    
    public Review() {
        reviewed = System.currentTimeMillis();
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
    public long getOptionId() {
        return optionId;
    }
    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }
    public long getReviewed() {
        return reviewed;
    }
    public void setReviewed(long reviewed) {
        this.reviewed = reviewed;
    }
    public long getCreated() {
        return created;
    }
    public void setCreated(long created) {
        this.created = created;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
}
