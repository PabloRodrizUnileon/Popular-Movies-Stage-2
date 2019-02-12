package com.example.pablo.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersData {

    @SerializedName("results")
    private List<Trailer> trailerList = null;

    public TrailersData(List<Trailer> trailerList) {
        this.trailerList = trailerList;

    }


    public List<Trailer> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
    }
}
