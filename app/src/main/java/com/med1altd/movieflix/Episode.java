package com.med1altd.movieflix;

public class Episode {

    String
            Title,
            Image;

    Boolean isUnlocked;

    public Episode(String title, String image, Boolean isUnlocked) {
        Title = title;
        Image = image;
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

    public Boolean getUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        isUnlocked = unlocked;
    }

}