package com.med1altd.movieflix;

public class Movie {

    String
            Title,
            Image,
            Video,
            Genre,
            Duration,
            Description,
            DescriptionIndicatorTitle,
            JSON;

    Integer
            Year,
            Position;

    Boolean
            isUnlocked;

    public Movie(String title, String image, String video, String genre, String duration, String description, String descriptionIndicatorTitle, String JSON, Integer year, Integer position, Boolean isUnlocked) {
        Title = title;
        Image = image;
        Video = video;
        Genre = genre;
        Duration = duration;
        Description = description;
        DescriptionIndicatorTitle = descriptionIndicatorTitle;
        this.JSON = JSON;
        Year = year;
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

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescriptionIndicatorTitle() {
        return DescriptionIndicatorTitle;
    }

    public void setDescriptionIndicatorTitle(String descriptionIndicatorTitle) {
        DescriptionIndicatorTitle = descriptionIndicatorTitle;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }

    public Integer getYear() {
        return Year;
    }

    public void setYear(Integer year) {
        Year = year;
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