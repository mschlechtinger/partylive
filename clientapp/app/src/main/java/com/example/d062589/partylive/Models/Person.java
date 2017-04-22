package com.example.d062589.partylive.Models;

/**
 * Created by D062589 on 13.04.2017.
 */

public class Person {

    private String _id;
    private String name;
    private String imgUrl;
    private String username;

    public Person(String name, String imgUrl, String username) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.username = username;
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

}
