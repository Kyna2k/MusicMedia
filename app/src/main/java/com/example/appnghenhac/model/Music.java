package com.example.appnghenhac.model;

import java.io.File;
import java.io.Serializable;

public class Music implements Serializable {
    private String name;
    private String singer;
    private String image;
    private String file;


    public Music() {
    }

    public Music(String name, String singer, String image, String file) {
        this.name = name;
        this.singer = singer;
        this.image = image;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
