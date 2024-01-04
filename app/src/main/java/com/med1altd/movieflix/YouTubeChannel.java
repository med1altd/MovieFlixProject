package com.med1altd.movieflix;

public class YouTubeChannel {

    String
            Title,
            Image,
            Url,
            JSON;

    Boolean
            isUnlocked;

    public YouTubeChannel(String title, String image, String url, String JSON, Boolean isUnlocked) {
        Title = title;
        Image = image;
        Url = url;
        this.JSON = JSON;
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

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }

    public Boolean getUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        isUnlocked = unlocked;
    }

}