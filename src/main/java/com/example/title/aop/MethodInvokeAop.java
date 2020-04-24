package com.example.title.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Create: 24/04/2020 19:03
 * @Author: Q
 */
@Aspect
@Component
public class MethodInvokeAop {
    private final Logger logger = LoggerFactory.getLogger(MethodInvokeAop.class);

    /**
     * 在Controller包下所有方法执行的时候打印日志
     */
    @Pointcut("execution(* com.example.title.controller.*.*(..))")
    public void log(){
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        logger.info("-------------Method |{}| doBefore-------------",joinPoint.getSignature().getName());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url =request.getRequestURL().toString();
        String ip =request.getRemoteAddr();
        String classMethod =joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
        logger.info("{} ",requestLog);
    }

    @After("log()")
    public void doAfter(){
        logger.info("-------------doAfter-------------");
    }

    /**
     * 方便日志记录
     */
    private class RequestLog{
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        @Override
        public String toString() {
            return "RequestLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }
    }
}
