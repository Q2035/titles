package com.example.title.handler;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Create: 26/04/2020 14:30
 * @Author: Q
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    /**
     * 按照顺序执行所有拦截器的preHandle方法，一直遇到return false
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
