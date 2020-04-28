package com.example.title.pojo;

/**
 * @Create: 24/04/2020 18:40
 * @Author: Q
 */
public class TitleDoneByUser {
    private Integer id;
//    synopsis
    private String type;
    private Integer titleID;
    private boolean isRight;

    public TitleDoneByUser() {
    }

    public TitleDoneByUser(String type, Integer titleID, boolean isRight) {
        this.type = type;
        this.titleID = titleID;
        this.isRight = isRight;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTitleID() {
        return titleID;
    }

    public void setTitleID(Integer titleID) {
        this.titleID = titleID;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }
}
