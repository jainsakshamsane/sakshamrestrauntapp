package com.sakshamrestrauntapp.Models;

public class ReviewModel {
    private String reviewId;
    private String dishId;
    private String userid;
    private String reviewText;
    private String timestamp;

    public ReviewModel(String reviewText, String userid) {
        this.reviewText = reviewText;
        this.userid = userid;
    }

    public ReviewModel(String reviewText, String userid, String timestamp) {
        this.reviewText = reviewText;
        this.userid = userid;
        this.timestamp = timestamp;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
