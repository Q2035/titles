package com.example.title.service.impl;

import com.example.title.mapper.TitleMapper;
import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import com.example.title.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Create: 27/04/2020 20:47
 * @Author: Q
 */
@Service
public class TitleServiceImpl implements TitleService {

    @Autowired
    private TitleMapper titleMapper;

    @Override
    public void createTitleTable(String synopsis) {
        titleMapper.createTitleTable(synopsis);
    }

    @Override
    public void setTitle(Title title,TitleType type) {
        titleMapper.setTitle(title,type);
    }

    @Override
    public List<TitleType> getAllTitleTypes() {
        return titleMapper.getAllTitleTypes();
    }

    @Override
    public void setTitleType(TitleType type) {
        titleMapper.setTitleType(type);
    }
}
