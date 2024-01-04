package com.med1altd.movieflix;

public class Category {

    String
            Title,
            Image,
            JSON_URL;

    Integer
            Category;
    Boolean
            isUnlocked;

    public Category(String title, String image, String JSON_URL, Integer category, Boolean isUnlocked) {
        Title = title;
        Image = image;
        this.JSON_URL = JSON_URL;
        Category = category;
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

    public String getJSON_URL() {
        return JSON_URL;
    }

    public void setJSON_URL(String JSON_URL) {
        this.JSON_URL = JSON_URL;
    }

    public Integer getCategory() {
        return Category;
    }

    public void setCategory(Integer category) {
        Category = category;
    }

    public Boolean getUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        isUnlocked = unlocked;
    }

}