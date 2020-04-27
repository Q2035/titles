package com.example.title.controller;

import com.example.title.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * @Create: 24/04/2020 18:45
 * @Author: Q
 */
@Controller
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/exercise")
    public String exercise(){
        return "exercise";
    }

    @GetMapping("/login")
    public String login(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user != null){
            return "user/home";
        }
        return "login";
    }
}
