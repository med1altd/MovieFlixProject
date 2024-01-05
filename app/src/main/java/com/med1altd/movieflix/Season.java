package com.med1altd.movieflix;

public class Season {

    String
            Title,
            Image,
            Year;

    Boolean
            isUnlocked;

    public Season(String title, String image, String year, Boolean isUnlocked) {
        Title = title;
        Image = image;
        Year = year;
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

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public Boolean getUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        isUnlocked = unlocked;
    }

}