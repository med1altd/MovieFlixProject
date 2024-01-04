package com.med1altd.movieflix;

public class Serie {

    String
            Title,
            Image,
            JSON;

    Integer
            Position;

    Boolean
            isUnlocked;

    public Serie(String title, String image, String JSON, Integer position, Boolean isUnlocked) {
        Title = title;
        Image = image;
        this.JSON = JSON;
        Position = position;
        this.isUnlocked = isUnlocked;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }

    public Integer getPosition() {
        return Position;
    }

    public void setPosition(Integer position) {
        Position = position;
    }

    public Boolean getUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        isUnlocked = unlocked;
    }

}