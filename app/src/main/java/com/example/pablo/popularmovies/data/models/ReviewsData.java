package com.example.pablo.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsData {

    @SerializedName("results")
    private List<Review> reviewList = null;

    public ReviewsData(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }
}
