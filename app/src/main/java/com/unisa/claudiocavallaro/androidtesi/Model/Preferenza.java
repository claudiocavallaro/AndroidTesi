package com.unisa.claudiocavallaro.androidtesi.Model;

import com.google.gson.annotations.SerializedName;

public class Preferenza {


    @SerializedName("id")
    private String id;
    @SerializedName("pref")
    private String ratingString;

    private long timestamp;


    public Preferenza(){}


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRatingString() {
        return ratingString;
    }

    public void setRatingString(String ratingString) {
        this.ratingString = ratingString;
    }
}
