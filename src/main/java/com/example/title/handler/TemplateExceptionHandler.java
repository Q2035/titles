package com.example.title.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.exceptions.TemplateInputException;

import javax.servlet.http.HttpServletRequest;

/**
 * @Create: 29/04/2020 10:46
 * @Author: Q
 */
@ControllerAdvice
public class TemplateExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(TemplateInputException.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        logger.error("Request URL:{},Exception:{}",request.getRequestURL(),e);
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) !=null){
            throw e;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMsg","Template Resolve Exception! Please go back index!");
        modelAndView.setViewName("/error/error");
        return modelAndView;
    }
}
