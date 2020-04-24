package com.example.title.pojo;

/**
 * @Create: 24/04/2020 18:40
 * @Author: Q
 */
public class TitleDoneByUser {
    private Integer id;
    private String type;
    private Integer typeId;
    private boolean isRight;

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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }
}
