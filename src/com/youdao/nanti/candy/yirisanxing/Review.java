package com.youdao.nanti.candy.yirisanxing;

public class Review {
    private long id;
    private long questionId;
    private long optionId;
    private int reviewed;
    private int created;
    private int isReviewed;
    
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
    public int getReviewed() {
        return reviewed;
    }
    public void setReviewed(int reviewed) {
        this.reviewed = reviewed;
    }
    public int getCreated() {
        return created;
    }
    public void setCreated(int created) {
        this.created = created;
    }
    public int getIsReviewed() {
        return isReviewed;
    }
    public void setIsReviewed(int isReviewed) {
        this.isReviewed = isReviewed;
    }

    
}
