package com.example.title.pojo;

import java.util.Date;

/**
 * @Create: 24/04/2020 18:40
 * @Author: Q
 */
public class TitleDoneByUser {
    private Integer id;
    private Integer userID;
    //    synopsis
    private String type;
    private Integer titleID;
    private Date date;
    private boolean isRight;

    private String topicDescription;

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public TitleDoneByUser() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public TitleDoneByUser(Integer userID, String type, Integer titleID, Date date, boolean isRight) {
        this.userID = userID;
        this.type = type;
        this.titleID = titleID;
        this.date = date;
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
