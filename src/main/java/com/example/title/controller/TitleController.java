package com.example.title.controller;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import com.example.title.service.TitleService;
import com.example.title.util.CookieInfo;
import com.example.title.util.TitleTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Random;

/**
 * @Create: 27/04/2020 15:33
 * @Author: Q
 */
@Controller
@RequestMapping("/titles/exercise")
public class TitleController {

    @Autowired
    private TitleService titleService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Random rand = new Random(System.currentTimeMillis());

    @GetMapping("/single")
    public String single(HttpServletRequest request,
                         Model model){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieInfo.synopsis)){
                String synopsis = cookie.getValue();
//                取得Cookie中存储的synopsis
                TitleType titleType = titleService.getTitleTypeBySynopsis(synopsis);
                if (titleType == null) {
                    model.addAttribute("errorMsg","Illegal Param :" + synopsis);
                    logger.warn("Illegal Param : {}",synopsis);
                    return "error/error";
                }
                int tempNum = rand.nextInt(titleType.getTitleCount());
                Title title = titleService.getTitleById(tempNum, synopsis);
//                判断随机生成的题目是否为单选题
                while (title.getTopicType() != TitleTypeEnum.SINGLE.getType()){
                    tempNum = rand.nextInt(titleType.getTitleCount());
                    title = titleService.getTitleById(tempNum,synopsis);
                }
//                数据库中的Options为一个字符串，以分号分割
                String[] split = title.getOptions().split(";");
                model.addAttribute("options",split);
                model.addAttribute("methodType","single");
                model.addAttribute("titles", Arrays.asList(title));
                return "exercise";
            }
        }
        logger.warn("Not the formal way to access!");
        model.addAttribute("errorMsg","No cookie information !");
        return "error/error";
    }
    @GetMapping("/multiple")
    public String multiple(){
        return "exercise";
    }
    @GetMapping("/judgement")
    public String judgement(){
        return "exercise";
    }
    @GetMapping("/exam")
    public String exam(){
        return "exercise";
    }
    @GetMapping("/random")
    public String random(){
        return "exercise";
    }

}
