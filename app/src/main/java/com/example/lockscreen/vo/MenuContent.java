package com.example.lockscreen.vo;

/**
 * Created by S9023192 on 2019/8/13.
 */

public class MenuContent {
    private int imageId;
    private String text;

    public MenuContent() {
    }

    public MenuContent(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int id) {
        this.imageId = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
