package com.example.title.handler;

import com.example.title.pojo.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 权限管理
 * @Create: 27/04/2020 15:46
 * @Author: Q
 */
public class AdminInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();
        if (requestURI.contains("/admin/")){
            User user = (User) session.getAttribute("user");
//            用户未登录
            if (user == null) {
                response.sendRedirect("/login");
                return false;
            }
//            已登录用户但不是管理员
            if (user.getRoles().equals("user")){
                response.sendRedirect("/user/home");
                return false;
            }
        }
        return true;
    }
}
