package com.example.title.controller;

import com.example.title.pojo.TitleType;
import com.example.title.pojo.User;
import com.example.title.service.TitleService;
import com.example.title.util.CookieInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Create: 24/04/2020 18:45
 * @Author: Q
 */
@RequestMapping("/titles")
@Controller
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private TitleService titleService;

    /**
     * 做题的首页，应当增加题目类型以共选择
     * @return
     */
    @GetMapping({"/","/index"})
    public String index(Model model,
                        HttpServletRequest request){
        logger.info("the ip {} access.",request.getRemoteAddr());
        List<TitleType> types = titleService.getAllTitleTypes();
        model.addAttribute("types",types);
        return "index";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    /**
     *
     * @param type
     * @param typeMethod 做题方式： 1（只有单选） 2（只有多选） 3（只有判断） 4（考试） 5（全部随机）
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping("/exercise")
    public String exercise(@RequestParam("type")String type,
                           @RequestParam("typeMethod")String typeMethod,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model){
        boolean validParam = false;
        List<TitleType> titleTypes = titleService.getAllTitleTypes();
//        找到对应的题目类型
        for (TitleType titleType : titleTypes) {
            if (titleType.getSynopsis().equals(type)){
                validParam = true;
                break;
            }
        }
//        做题的类型是否是指定的种类，如果是其他的，那么就是不规范的参数
//        1（只有单选） 2（只有多选） 3（只有判断） 4（考试） 5（全部随机）
        switch (typeMethod){
            case "exam":
                break;
            case "single":
                break;
            case "multiple":
                break;
            case "judgement":
                break;
            case "random":
                break;
            default:
                validParam = false;
                break;
        }
//        不存在对应的题目类型，返回到错误页面
        if (!validParam){
            logger.warn("{} have change the param type:{} method:{}",request.getRemoteAddr(),type,typeMethod);
            model.addAttribute("errorMsg","Illegal Parameter");
            return "error/error";
        }
        logger.info("user {} choose the {}",request.getRemoteAddr(),type);
//        将用户的选择存入Cookie以及Session
        HttpSession session = request.getSession();
        session.setAttribute(CookieInfo.synopsis,type);
//        题库类型（mao、ma）
        Cookie synopsisCookie = new Cookie(CookieInfo.synopsis,type);
//        Cookie有效期设置为30分钟
        synopsisCookie.setMaxAge(30*60);
        synopsisCookie.setPath("/");
        Cookie typeMethodCookie = new Cookie(CookieInfo.typeMethod, typeMethod);
        typeMethodCookie.setMaxAge(30*60);
        typeMethodCookie.setPath("/");
        response.addCookie(synopsisCookie);
        response.addCookie(typeMethodCookie);
        return "forward:/titles/exercise/"+typeMethod;
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