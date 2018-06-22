package com.allvoes.lunachat;

public class User {
    private String name;
    private String status;
    private String image;
    private String Thumb_image;

    public User(){

    }

    public User(String name, String status, String image, String Thumb_image) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.Thumb_image = Thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return Thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        Thumb_image = thumb_image;
    }
}
