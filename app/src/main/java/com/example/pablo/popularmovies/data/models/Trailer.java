package com.example.pablo.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    public static final String VIDEO_THUMBNAIL_URL = "https://img.youtube.com/vi/";
    public static final String VIDEO_THUMBNAIL_EXTENSION = "/0.jpg";

   @SerializedName("key")
   private String key;
   @SerializedName("site")
   private String site;
   @SerializedName("name")
   private String name;

    public Trailer(String key, String site, String name) {
        this.key = key;
        this.site = site;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl(){
        return VIDEO_THUMBNAIL_URL + this.key + VIDEO_THUMBNAIL_EXTENSION;
    }

}
