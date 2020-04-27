package com.example.title.controller;

import com.example.title.pojo.User;
import com.example.title.service.UserService;
import com.example.title.util.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Create: 27/04/2020 15:33
 * @Author: Q
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/manage")
    public String manage(Model model){
        model.addAttribute("users",userService.getAllUsers());
        return "admin/manage";
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);
        if (user.getRoles().equals("admin")){
            logger.error("try to delete admin user but failed!");
            return "forward:/admin/manage";
        }
        logger.warn("{} will be deleted",user);
        userService.deleteUserById(id);
        return "forward:/admin/manage";
    }
}
