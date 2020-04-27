package com.example.title.service;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;

import java.util.List;

/**
 * @Create: 27/04/2020 20:47
 * @Author: Q
 */
public interface TitleService {

    void createTitleTable(String synopsis);

//    针对Title
    void setTitle(Title title,TitleType type);

//    针对TitleType
    List<TitleType> getAllTitleTypes();

    void setTitleType(TitleType type);
}
