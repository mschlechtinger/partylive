package com.example.d062589.partylive.Models;

/**
 * Created by D062589 on 13.04.2017.
 */

public class Person {

    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    private String _id;
    private String name;
    private String imgUrl;
    private String username;
    private String gender;
    private String facebookId;

    public Person(String name, String imgUrl, String username) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.username = username;
    }

    public Person(String name, String imgUrl, String username, String gender) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.username = username;
        this.gender = gender;
    }

    public Person(String name, String imgUrl, String username, String gender, String facebookId) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.username = username;
        this.gender = gender;
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
