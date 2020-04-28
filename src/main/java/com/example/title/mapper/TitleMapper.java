package com.example.title.mapper;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Create: 27/04/2020 20:32
 * @Author: Q
 */
public interface TitleMapper {

    /**
     * 新建表
     */
    void createTitleTable(String synopsis);

    /**
     * 将题目信息插入数据库
     * @param title
     */
    void setTitle(@Param("title") Title title, @Param("type") TitleType type);

    void updateTitle(@Param("title") Title title,@Param("synopsis") String synopsis);

    Title getTitleById(@Param("id") Integer id,@Param("synopsis") String synopsis);

    List<TitleType> getAllTitleTypes();

    TitleType getTitleTypeBySynopsis(String synopsis);

    void setTitleType(TitleType type);

}
