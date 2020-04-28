package com.example.title.service;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Create: 27/04/2020 20:47
 * @Author: Q
 */
public interface TitleService {

    void createTitleTable(String synopsis);

//    插入Title
    void setTitle(Title title,TitleType type);

//    通过ID获取Title
Title getTitleById(Integer id,String synopsis);

//    针对TitleType
    List<TitleType> getAllTitleTypes();

    TitleType getTitleTypeBySynopsis(String synopsis);

    void setTitleType(TitleType type);
}
