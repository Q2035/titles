package com.example.title.util;

/**
 * @Create: 27/04/2020 21:10
 * @Author: Q
 */
public enum TitleTypeEnum {

    SINGLE(1),MULTIPLE(2),JUDGEMENT(3);

    Integer type;

    TitleTypeEnum(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
