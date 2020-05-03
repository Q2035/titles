package com.example.title.service.impl;

import com.example.title.mapper.TitleMapper;
import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import com.example.title.service.TitleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public void updateWrongCountOfTitle(Integer id, String synopsis) {
        titleMapper.updateWrongCountOfTitle(id,synopsis);
    }

    @Override
    public void updateCountOfTitle(Integer id, String synopsis) {
        titleMapper.updateCountOfTitle(id, synopsis);
    }

    @Override
    public Title getTitleMaxCount(String synopsis) {
        return titleMapper.getTitleMaxCount(synopsis);
    }

    @Override
    public void setTitleTypeMaxCount(TitleType type) {
        titleMapper.setTitleTypeMaxCount(type);
    }

    /**
     * Redis获取不到就从数据库取
     * @param id
     * @param synopsis
     * @return
     */
    @Override
    public Title getTitleById(Integer id, String synopsis) {
        Title title = getTileFromRedis(id, synopsis);
        return title;
    }

    Title getTileFromRedis(Integer id,String synopsis){
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        String s = (String) opsForHash.get(synopsis, synopsis + id);
        Title title = null;
        if (s == null) {
            title = titleMapper.getTitleById(id, synopsis);
            try {
                String writeValueAsString = objectMapper.writeValueAsString(title);
                opsForHash.put(synopsis,synopsis + title.getId() , writeValueAsString);
            } catch (JsonProcessingException e) {
                logger.error("Failed to transfer JSON to string! Obj {}",title);
            }
            return title;
        } else {
            try {
                title = objectMapper.readValue(s, Title.class);
            } catch (JsonProcessingException e) {
                logger.error("json transfer to obj error {}",title);
            }
        }
        return title;
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
