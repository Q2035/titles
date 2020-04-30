package com.example.title.controller;

import com.example.title.mapper.TitleMapper;
import com.example.title.mapper.UserMapper;
import com.example.title.pojo.TitleDoneByUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Create: 28/04/2020 10:49
 * @Author: Q
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TitleMapper titleMapper;

    @RequestMapping("/t1")
    public String m1(Model model){
        model.addAttribute("errorMsg","Illegal Param");
        return "error/error";
    }

    @ResponseBody
    @RequestMapping("/t2")
    public List<TitleDoneByUser> t2(){
        String synopsis = "mao";
        List<TitleDoneByUser> userDone = userMapper.getUserDone(1);
        for (TitleDoneByUser titleDoneByUser : userDone) {
            titleDoneByUser.setTopicDescription(titleMapper.getTitleById(titleDoneByUser.getTitleID(),synopsis).getTopicDescription());
        }
        return userDone;
    }
}
