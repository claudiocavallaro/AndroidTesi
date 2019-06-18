package com.unisa.claudiocavallaro.androidtesi.Model;

public class Preferenza {

    private String title;
    private String artist;

    private String id;
    private String ratingString;

    private int rating;


    public Preferenza(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
