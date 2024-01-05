package com.med1altd.movieflix;

public class User {

    String
            Code;

    Boolean
            activated;

    public User(String code, Boolean activated) {

        Code = code;

        this.activated = activated;

    }

    public String getCode() {

        return Code;

    }

    public void setCode(String code) {

        Code = code;

    }

    public Boolean getActivated() {

        return activated;

    }

    public void setActivated(Boolean activated) {

        this.activated = activated;

    }

}
