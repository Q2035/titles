package com.example.title.pojo;

/**
 * 只是题目类型，抽象出来给Index页面使用
 * @Create: 27/04/2020 19:05
 * @Author: Q
 */
public class TitleType {
    private Integer id;
//    这个是短述 eg.mao 便于前端向服务器传送数据
//    同时用作数据表名 eg.title_${synopsis}
    private String synopsis;
//    这个是描述 eg.毛概
    private String description;

    private Integer titleCount;

    public Integer getTitleCount() {
        return this.titleCount;
    }

    public void setTitleCount(Integer titleCount) {
        this.titleCount = titleCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TitleType{" +
                "id=" + id +
                ", synopsis='" + synopsis + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}