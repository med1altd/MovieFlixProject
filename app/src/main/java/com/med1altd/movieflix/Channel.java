package com.med1altd.movieflix;

import java.io.Serializable;

public class Channel implements Serializable {

    String
            Title,
            Image,
            Video,
            LiveUrl,
            StartString,
            EndString,
            StartStringD,
            EndStringD;

    Integer
            Start,
            End,
            StartD,
            EndD;

    Boolean
            IsUnlocked,
            Live,
            Description,
            AnotherVideo;

    public Channel(String title, String image, String video, String liveUrl, String startString, String endString, String startStringD, String endStringD, Integer start, Integer end, Integer startD, Integer endD, Boolean isUnlocked, Boolean live, Boolean description, Boolean anotherVideo) {
        Title = title;
        Image = image;
        Video = video;
        LiveUrl = liveUrl;
        StartString = startString;
        EndString = endString;
        StartStringD = startStringD;
        EndStringD = endStringD;
        Start = start;
        End = end;
        StartD = startD;
        EndD = endD;
        IsUnlocked = isUnlocked;
        Live = live;
        Description = description;
        AnotherVideo = anotherVideo;
    }

    public Channel(Boolean anotherVideo) {
        AnotherVideo = anotherVideo;
    }

    public Boolean getAnotherVideo() {
        return AnotherVideo;
    }

    public void setAnotherVideo(Boolean anotherVideo) {
        AnotherVideo = anotherVideo;
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

    public String getLiveUrl() {
        return LiveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        LiveUrl = liveUrl;
    }

    public String getStartString() {
        return StartString;
    }

    public void setStartString(String startString) {
        StartString = startString;
    }

    public String getEndString() {
        return EndString;
    }

    public void setEndString(String endString) {
        EndString = endString;
    }

    public String getStartStringD() {
        return StartStringD;
    }

    public void setStartStringD(String startStringD) {
        StartStringD = startStringD;
    }

    public String getEndStringD() {
        return EndStringD;
    }

    public void setEndStringD(String endStringD) {
        EndStringD = endStringD;
    }

    public Integer getStart() {
        return Start;
    }

    public void setStart(Integer start) {
        Start = start;
    }

    public Integer getEnd() {
        return End;
    }

    public void setEnd(Integer end) {
        End = end;
    }

    public Integer getStartD() {
        return StartD;
    }

    public void setStartD(Integer startD) {
        StartD = startD;
    }

    public Integer getEndD() {
        return EndD;
    }

    public void setEndD(Integer endD) {
        EndD = endD;
    }

    public Boolean getUnlocked() {
        return IsUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        IsUnlocked = unlocked;
    }

    public Boolean getLive() {
        return Live;
    }

    public void setLive(Boolean live) {
        Live = live;
    }

    public Boolean getDescription() {
        return Description;
    }

    public void setDescription(Boolean description) {
        Description = description;
    }

}