package com.example.title.controller;

import com.example.title.exception.UsernameDuplicationException;
import com.example.title.pojo.LoginUser;
import com.example.title.pojo.TitleDoneByUser;
import com.example.title.pojo.TitleType;
import com.example.title.pojo.User;
import com.example.title.service.TitleService;
import com.example.title.service.UserService;
import com.example.title.util.Code;
import com.example.title.util.CommonResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @Create: 26/04/2020 10:55
 * @Author: Q
 */
@Controller
@RequestMapping("/titles/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TitleService titleService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping("/home")
    public String home(Model model,
                       HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<TitleType> allTitleTypes = titleService.getAllTitleTypes();
        List<TitleDoneByUser> userDone = userService.getUserDone(user.getId());
        for (TitleDoneByUser titleDoneByUser : userDone) {
            TitleType temp = null;
            for (TitleType allTitleType : allTitleTypes) {
                if (allTitleType.getSynopsis().equals(titleDoneByUser.getType())) {
                    temp = allTitleType;
                }
            }
            if (temp == null) {
                logger.warn("error! The synopsis doesn't exist! {}",titleDoneByUser.getType());
            }
            titleDoneByUser.setTopicDescription(titleService.getTitleById(titleDoneByUser.getTitleID(),temp.getSynopsis()).getTopicDescription());
        }
        model.addAttribute("titles", userDone);
        return "user/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "forward:/titles/";
    }

    @PostMapping("/authenticate")
    @ResponseBody
    public CommonResult authenticate(@RequestBody String reqUser,
                                     HttpServletRequest request){
        User user = null;
        try {
            String decode = URLDecoder.decode(reqUser,"utf-8");
            logger.info(decode);
            user = mapper.readValue(decode, User.class);
        } catch (UnsupportedEncodingException e) {
            logger.warn("UnsupportedEncodingException.");
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String username = user.getUsername();
        String password = user.getPassword();
        User resUser = userService.getUserByUsernameAndPassword(username, password);
        CommonResult result = new CommonResult();
        if (resUser == null){
            result.setMessage("the username or password is wrong.");
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
        }else {
            logger.info("{} {} login at {}",resUser.getRoles(),username,request.getRemoteAddr());
            HttpSession session = request.getSession();
            session.setAttribute("user",resUser);
            result.setMessage("SUCCESS");
            result.setCode(Code.SUCCESS);
            result.setSuccess(true);
        }
        return result;
    }

    @PostMapping("/register")
    @ResponseBody
    public CommonResult register(@RequestBody String userJson) throws UnsupportedEncodingException, JsonProcessingException {
        CommonResult commonResult = new CommonResult();
        String decode = URLDecoder.decode(userJson,"utf-8");
        User user = mapper.readValue(decode, User.class);
//        将创建的用户设置为普通用户
        user.setRoles("user");
        try {
            userService.setUserIntoDB(user);
            commonResult.setSuccess(true);
            commonResult.setCode(Code.SUCCESS);
            commonResult.setMessage("SUCCESS");
        }catch (UsernameDuplicationException e){
            commonResult.setSuccess(false);
            commonResult.setCode(Code.SYSTEM_ERROR);
            commonResult.setMessage("The username is duplicate, please try another one");
        }
        return commonResult;
    }

}
