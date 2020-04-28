package com.example.title.controller;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleDoneByUser;
import com.example.title.pojo.TitleType;
import com.example.title.service.TitleService;
import com.example.title.service.UserService;
import com.example.title.util.Code;
import com.example.title.util.CommonResult;
import com.example.title.util.CookieInfo;
import com.example.title.util.TitleTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Random rand = new Random(System.currentTimeMillis());

    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/single")
    public String single(HttpServletRequest request,
                         Model model){
        String synopsis = ifSynopsisInCookie(request);
        if (synopsis != null) {
            TitleType titleType = titleService.getTitleTypeBySynopsis(synopsis);
            if (titleType == null) {
                model.addAttribute("errorMsg","Illegal Param :" + synopsis);
                logger.warn("Illegal Param : {} | Single",synopsis);
                return "error/error";
            }
//            生成单选题
            Title title = generateTheTitleBySynopsis(titleType, TitleTypeEnum.SINGLE, synopsis);
//                数据库中的Options为一个字符串，以分号分割
            String[] split = title.getOptions().split(";");
            model.addAttribute("options",split);
            model.addAttribute("methodType","single");
            model.addAttribute("titles", Arrays.asList(title));
            return "exercise";
        }
        logger.warn("Not the formal way to access!");
        model.addAttribute("errorMsg","No cookie information !");
        return "error/error";
    }

    @GetMapping("/multiple")
    public String multiple(Model model,
                           HttpServletRequest request){

        String synopsis = ifSynopsisInCookie(request);
        if (synopsis != null){
            TitleType titleType = titleService.getTitleTypeBySynopsis(synopsis);
            if (titleType == null) {
                model.addAttribute("errorMsg","Illegal Param :" + synopsis);
                logger.warn("Illegal Param : {} | Multiple",synopsis);
                return "error/error";
            }
            final Title title = generateTheTitleBySynopsis(titleType, TitleTypeEnum.MULTIPLE, synopsis);
//                数据库中的Options为一个字符串，以分号分割
            String[] split = title.getOptions().split(";");
            model.addAttribute("options",split);
            model.addAttribute("methodType","multiple");
            model.addAttribute("titles", Arrays.asList(title));
            return "exercise";
        }
        logger.warn("Not the formal way to access!");
        model.addAttribute("errorMsg","No cookie information !");
        return "error/error";
    }

    String ifSynopsisInCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieInfo.synopsis)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @GetMapping("/judgement")
    public String judgement(Model model,
                            HttpServletRequest request){
        String synopsis = ifSynopsisInCookie(request);
        if (synopsis != null){
            TitleType titleType = titleService.getTitleTypeBySynopsis(synopsis);
            if (titleType == null) {
                model.addAttribute("errorMsg","Illegal Param :" + synopsis);
                logger.warn("Illegal Param : {} | Judgement",synopsis);
                return "error/error";
            }
            Title title = generateTheTitleBySynopsis(titleType, TitleTypeEnum.JUDGEMENT, synopsis);
//                数据库中的Options为一个字符串，以分号分割
            model.addAttribute("methodType","judgement");
            model.addAttribute("titles", Arrays.asList(title));
            return "exercise";
        }
        logger.warn("Not the formal way to access at {}!",request.getRemoteAddr());
        model.addAttribute("errorMsg","No cookie information !");
        return "error/error";
    }

    @GetMapping("/exam")
    public String exam(){
        return "exercise";
    }

    @GetMapping("/random")
    public String random(){
        return "exercise";
    }

    @ResponseBody
    @PostMapping("/submit")
    public CommonResult submit(@RequestBody String reqAnswer,
                               HttpSession session) throws JsonProcessingException {
        CommonResult result;
//        解析JSON数据
        String decode = URLDecoder.decode(reqAnswer, Charset.forName("utf-8"));
        logger.debug("{}",decode);
        RequestSubmit requestSubmit = mapper.readValue(decode, RequestSubmit.class);
        String synopsis = (String) session.getAttribute(CookieInfo.synopsis);
        Title title = titleService.getTitleById(requestSubmit.getTitleId(), synopsis);
        String answer = requestSubmit.getAnswer();
        String rightAnswer = title.getAnswer();
//        只有一个答案的话最多也就选一个答案
        result = ifAnswerIsTrue(answer,rightAnswer);
//        做了一道题
        title.setCountOfDone(title.getCountOfDone() + 1);
//        没做对
        if (!result.isSuccess()){
            title.setCountOfWrong(title.getCountOfWrong() + 1);
        }
        if (session.getAttribute("user") != null) {
//            这里插入失败明天搞
//            更新用户做题信息
            final TitleDoneByUser titleDoneByUser = new TitleDoneByUser(synopsis, title.getId(), result.isSuccess());
            userService.setUserDone(titleDoneByUser);
        }
//        更新题目信息
        titleService.updateTitle(title,synopsis);
        return result;
    }

    CommonResult ifAnswerIsTrue(String answer,String rightAnswer){
        CommonResult result = new CommonResult();
        if (answer.length() == 1  && !rightAnswer.equals("0") && !rightAnswer.equals("1")){
            String temp = String.valueOf((char) ('a' - 1 + Integer.parseInt(answer)));
            if (temp.toLowerCase().equals(rightAnswer.toLowerCase())) {
                result.setSuccess(true);
                result.setMessage("√ Congratulation! The correct answer is " + rightAnswer.toUpperCase());
                result.setCode(Code.SUCCESS);
                return result;
            }
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
            result.setMessage("× Wrong Answer! The correct answer is " + rightAnswer.toUpperCase());
        } else if (answer.length() > 1) {
            String temp = "";
            char[] chars = answer.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                temp += String.valueOf((char)('a' - 1 + Integer.parseInt(String.valueOf(chars[i]))));
            }
            if (temp.toUpperCase().equals(rightAnswer.toUpperCase())){
                result.setSuccess(true);
                result.setMessage("√ Congratulation! The correct answer is " + rightAnswer.toUpperCase());
                result.setCode(Code.SUCCESS);
                return result;
            }
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
            result.setMessage("× Wrong Answer! The correct answer is " + rightAnswer.toUpperCase());
            return result;
        }else {
            if (answer.equals(rightAnswer)) {
                result.setSuccess(true);
                result.setMessage("√ Congratulation! The correct answer is " + rightAnswer.toUpperCase());
                result.setCode(Code.SUCCESS);
                return result;
            }
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
            result.setMessage("× Wrong Answer! The correct answer is " + rightAnswer.toUpperCase());
            return result;
        }
        return result;
    }

    Title generateTheTitleBySynopsis(TitleType titleType,
                                     TitleTypeEnum typeEnum,
                                     String synopsis){
        int tempNum = rand.nextInt(titleType.getTitleCount());
        Title title = titleService.getTitleById(tempNum, synopsis);
//                判断随机生成的题目是否为判断题
        while (title.getTopicType() != typeEnum.getType()){
            tempNum = rand.nextInt(titleType.getTitleCount());
            title = titleService.getTitleById(tempNum,synopsis);
        }
        return title;
    }

    static class RequestSubmit<T> {
        private Integer titleId;
        private String answer;

        public Integer getTitleId() {
            return titleId;
        }

        public void setTitleId(Integer titleId) {
            this.titleId = titleId;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

}
