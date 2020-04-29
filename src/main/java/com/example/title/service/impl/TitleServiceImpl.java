package com.example.title.service.impl;

import com.example.title.mapper.TitleMapper;
import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import com.example.title.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
    public void updateTitle(Title title, String synopsis) {
        titleMapper.updateTitle(title,synopsis);
    }

    @Override
    public Title getTitleById(Integer id, String synopsis) {
        return titleMapper.getTitleById(id,synopsis);
    }

    @Override
    public List<Title> getTitlesByIds(Set<Integer> set, String synopsis) {
        return titleMapper.getTitlesByIds(set,synopsis);
    }

    @Override
    public List<TitleType> getAllTitleTypes() {
        return titleMapper.getAllTitleTypes();
    }

    @Override
    public TitleType getTitleTypeBySynopsis(String synopsis) {
        return titleMapper.getTitleTypeBySynopsis(synopsis);
    }

    @Override
    public void setTitleType(TitleType type) {
        titleMapper.setTitleType(type);
    }
}
