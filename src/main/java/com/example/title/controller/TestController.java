package com.example.title.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Create: 28/04/2020 10:49
 * @Author: Q
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/t1")
    public String m1(Model model){
        model.addAttribute("errorMsg","Illegal Param");
        return "error/error";
    }
}
