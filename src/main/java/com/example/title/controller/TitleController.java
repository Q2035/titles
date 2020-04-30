package com.example.title.controller;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleDoneByUser;
import com.example.title.pojo.TitleType;
import com.example.title.pojo.User;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private final String answerCookieName = "EXAM_ANSWER";

    private final String titleIdCookieName = "TITLE_IDS";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Random rand = new Random(System.currentTimeMillis());

    private ObjectMapper mapper = new ObjectMapper();

    public final int SINGLE_COUNT = 60;
    public final int JUDGEMENT_COUNT = 20;
    public final int MULTIPLE_COUNT = 10;

    static ExecutorService executorService = Executors.newFixedThreadPool(4);


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

    /**
     * 处理发送的考试请求，假设题目为50单选 10多选 20判断
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/exam")
    public String exam(Model model,
                       HttpServletRequest request,
                       HttpServletResponse response){
        String synopsis = ifSynopsisInCookie(request);
        if (synopsis != null) {
            TitleType titleType = titleService.getTitleTypeBySynopsis(synopsis);
            if (titleType == null) {
                model.addAttribute("errorMsg","Illegal Param :" + synopsis);
                logger.warn("Illegal Param : {} | Single",synopsis);
                return "error/error";
            }
            List<Title> titles = generateExamTitles(titleType);
            StringBuffer cookieValue = new StringBuffer();
            StringBuffer cookieTitleIDsValue = new StringBuffer();
            titles.stream().forEach(t->{
//                单选，多选的选项处理
                if (t.getTopicType() != TitleTypeEnum.JUDGEMENT.getType()) {
                    t.setSplits(t.getOptions().split(";"));
                }
                cookieValue.append("-"+t.getAnswer());
                cookieTitleIDsValue.append("-"+t.getId());
            });
            logger.info("{}:{}",request.getRemoteAddr(),cookieTitleIDsValue.toString().substring(0,20));
//            还是需要将答案信息存入Cookie
            Cookie cookie = new Cookie(answerCookieName, cookieValue.toString());
//            设置Cookie有效期为30分钟
//            存储的是严格按照顺序排列的答案
            cookie.setMaxAge(30 * 60);
            cookie.setPath("/");
            Cookie idCookie = new Cookie(titleIdCookieName, cookieTitleIDsValue.toString());
            idCookie.setPath("/");
            idCookie.setMaxAge(30 * 60);
            response.addCookie(idCookie);
            response.addCookie(cookie);
            model.addAttribute("titles",titles);
            return "exam";
        }
        logger.warn("Not the formal way to access!");
        model.addAttribute("errorMsg","No cookie information !");
        return "error/error";
    }

    List<Title> generateExamTitles(TitleType titleType){
        List<Title> single = new ArrayList<>();
        List<Title> multiple = new ArrayList<>();
        List<Title> judgement = new ArrayList<>();
        boolean singleFlag = false;
        boolean multipleFlag = false;
        boolean judgementFlag = false;
        List<Title> result = new ArrayList<>();
        Set<Integer> titleNums = new HashSet<>();
        Set<Integer> titleNumsHaveChoose = new HashSet<>();
//        随机生成80个不同的数字
        while(titleNums.size() < SINGLE_COUNT + JUDGEMENT_COUNT + MULTIPLE_COUNT){
            titleNums.add(rand.nextInt(titleType.getTitleCount()) + 1);
        }
        List<Title> tempTitles;
        tempTitles = titleService.getTitlesByIds(titleNums,titleType.getSynopsis());
        for (Title tempTitle : tempTitles) {
            if (tempTitle.getTopicType() == TitleTypeEnum.SINGLE.getType() && !singleFlag) {
                single.add(tempTitle);
                if (single.size() > SINGLE_COUNT) {
                    singleFlag = true;
                }
//                只要题目符合要求并且被选择了，则加入集合
               titleNumsHaveChoose.add(tempTitle.getId());
            } else if (tempTitle.getTopicType() == TitleTypeEnum.MULTIPLE.getType() && !multipleFlag) {
                multiple.add(tempTitle);
                if (multiple.size() == MULTIPLE_COUNT) {
                    multipleFlag = true;
                }
               titleNumsHaveChoose.add(tempTitle.getId());
            } else if (tempTitle.getTopicType() == TitleTypeEnum.JUDGEMENT.getType() && !judgementFlag) {
                judgement.add(tempTitle);
                if (judgement.size() == JUDGEMENT_COUNT) {
                    judgementFlag = true;
                }
               titleNumsHaveChoose.add(tempTitle.getId());
            }
        }

//        不管那种类型没有满足数据要求都继续执行
        while (!singleFlag || !multipleFlag || !judgementFlag) {
            int tempId = rand.nextInt(titleType.getTitleCount()) + 1;
//            选出未被选择过的ID
            while (titleNumsHaveChoose.contains(tempId)) {
                tempId = rand.nextInt(titleType.getTitleCount()) + 1;
            }
            Title tempTitle = titleService.getTitleById(tempId, titleType.getSynopsis());
            if (!singleFlag && tempTitle.getTopicType() == TitleTypeEnum.SINGLE.getType()) {
                single.add(tempTitle);
                if (single.size() == SINGLE_COUNT) {
                    singleFlag = true;
                }
            }
            if (!multipleFlag && tempTitle.getTopicType() == TitleTypeEnum.MULTIPLE.getType()) {
                multiple.add(tempTitle);
                if (multiple.size() == MULTIPLE_COUNT) {
                    multipleFlag = true;
                }
            }
            if (!judgementFlag && tempTitle.getTopicType() == TitleTypeEnum.JUDGEMENT.getType()) {
                judgement.add(tempTitle);
                if (judgement.size() == JUDGEMENT_COUNT) {
                    judgementFlag = true;
                }
            }
        }
//        循环结束得到80道符合要求的题目
        result.addAll(single);
        result.addAll(multiple);
        result.addAll(judgement);
        return result;
    }

    @GetMapping("/random")
    public String random(HttpServletRequest request,
                         Model model){
        String synopsis = ifSynopsisInCookie(request);
        if (synopsis != null) {
            TitleType titleType = titleService.getTitleTypeBySynopsis(synopsis);
            if (titleType == null) {
                model.addAttribute("errorMsg","Illegal Param :" + synopsis);
                logger.warn("Illegal Param : {} | Random",synopsis);
                return "error/error";
            }
//            生成题目
//            随机题目不需要指定类型
            Title title = titleService.getTitleById(rand.nextInt(titleType.getTitleCount()) + 1, synopsis);
//            判断是否需要生成选项
//            12345 单多判考随(TitleTypeEnum)
            switch (title.getTopicType()) {
//                单选
                case 1:
                    String[] split = title.getOptions().split(";");
                    model.addAttribute("options",split);
                    model.addAttribute("choose",1);
                    break;
                    //                多选，他们都有Option
                case 2:
                    String[] split1 = title.getOptions().split(";");
                    model.addAttribute("options",split1);
                    model.addAttribute("choose",2);
                    break;
                default:
                    break;
            }
            model.addAttribute("methodType","random");
            model.addAttribute("titles",Arrays.asList(title));
            return "exercise";
        }
        logger.warn("Not the formal way to access at {}!",request.getRemoteAddr());
        model.addAttribute("errorMsg","No cookie information !");
        return "error/error";
    }

    /**
     *
     * @param request
     * @param reqAnswers 假设格式为：answer1:2;answer2:1...
     * @return
     */
    @ResponseBody
    @PostMapping("/examSubmit")
    public CommonResult examSubmit(HttpServletRequest request,
                                   @RequestBody String reqAnswers){
        CommonResult result = new CommonResult();
        String decode = URLDecoder.decode(reqAnswers, Charset.forName("utf-8"));
        String rightAns = ifCookieHas(request, answerCookieName);
        String tempIDs = ifCookieHas(request, titleIdCookieName);
        String synopsis = ifSynopsisInCookie(request);
//        Cookie没有对应信息，过期了
        if (rightAns == null || tempIDs == null) {
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
            result.setMessage("Sorry,TIMEOUT!");
            return result;
        }
//        第一个元素为空
//        这个用来存储做错的题目ID
        List<String> wrongTitleIds = Arrays.asList(tempIDs.split("-"));
        String[] answers = rightAns.split("-");
//        1:2,
        String[] reqAns = decode.split("answer");
        String[] afterDeal= new String[reqAns.length];
        for (int i =0; i < reqAns.length;i++) {
            String temp;
            temp = reqAns[i].replaceAll(",","");
            temp = temp.replaceAll("}","");
            afterDeal[i] = temp;
        }

//        这里用来处理没有完成的情况
        float score = 0.0f;
        List<Integer> errorTitle = new ArrayList<>();
        for (String tempAns : afterDeal) {
//            很明显，最小都是"1:1"
            if (tempAns.length() < 3) {
                continue;
            }
//            这样处理之后所有数据都是标准的 "a:b"的形式
//            其中a为答案编号
            tempAns = tempAns.replaceAll("=","");
//            这里长度应该是2
            String[] split = tempAns.split(":");
            if (split.length == 1 || split[1].equals("")) {
                continue;
            }
            int i = Integer.parseInt(split[0]);
//            题目顺序为单选、多选、判断 60 20 10
            if (i <= SINGLE_COUNT) {
                char[] chars = split[1].toCharArray();
                char[] tempChar = answers[i].toCharArray();
                if (chars.length==1 && tempChar.length == 1) {
                    if (chars[0] + 'a' - '1' == tempChar[0]) {
                        score += 1.11f;
                        wrongTitleIds.set(i,wrongTitleIds.get(i) + "-");
                    }else {
                        errorTitle.add(i);
                    }
                }
                continue;
            }
            if (i <= MULTIPLE_COUNT + SINGLE_COUNT) {
//                多选需要转换一下 ， 传递过来的是 1 2 3等 代表 a b c
                split[1].chars().map(ch -> ch + 'a' - '1');
                if (String.valueOf(split[1]).equals(answers[i])) {
                    score += 1.11f;
                    wrongTitleIds.set(i,wrongTitleIds.get(i) + "-");
                }else {
                    errorTitle.add(i);
                }
                continue;
            }
            if (i <= JUDGEMENT_COUNT + MULTIPLE_COUNT + SINGLE_COUNT) {
                if (answers[i].equals(split[1])) {
                    score += 1.11f;
                    wrongTitleIds.set(i,wrongTitleIds.get(i) + "-");
                }else{
                    errorTitle.add(i);
                }
            }
        }
//        将试题信息异步插入数据库
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        executorService.execute(() -> {
                    logger.info("Start inserting wrong count and user done information asynchronously.");
                    long start = System.currentTimeMillis();
                    wrongTitleIds.stream()
                            .filter(str -> str.length() > 0)
                            .forEach(str -> {
//                        结尾不为'-'代表这个题目做错了
                                String  s = "";
                                boolean flag = false;
                                if (!str.endsWith("-")) {
                                    s = str.replaceAll("-", "");
                                    titleService.updateWrongCountOfTitle(Integer.parseInt(s), synopsis);
                                }else {
                                    s = str.replaceAll("-","");
                                    flag = true;
                                }
                                titleService.updateCountOfTitle(Integer.parseInt(s),synopsis);
//                                    用户已登录
                                if (user != null) {
                                    userService.setUserDone(new TitleDoneByUser(user.getId(),synopsis,Integer.parseInt(s),new Date(),flag));
                                }
                            });
                            logger.info("count insert end, cost: {} ms",System.currentTimeMillis() - start);
                }
        );
        result.setSuccess(true);
        if (score > 99) {
            result.setMessage("Congratulation! You got full marks!");
        } else if (score == 0) {
            result.setMessage("Congratulation! 这么久了我还没见过零分的");
        }else {
            result.setMessage("You got "+ String.valueOf(score).substring(0,score > 10 ? 5 : 4) + "/100!");
        }
        result.setData(errorTitle);
        result.setCode(Code.SUCCESS);
        return result;
    }

    String ifCookieHas(HttpServletRequest request,String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 这个方法针对的是除了考试之外的提交信息
     * @param reqAnswer
     * @param session
     * @return
     * @throws JsonProcessingException
     */
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
        User user = (User) session.getAttribute("user");
        if (user != null) {
//            更新用户做题信息
            userService.setUserDone(new TitleDoneByUser(user.getId(),synopsis, title.getId(), new Date(),result.isSuccess()));
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
//            判断题在这里
            if (answer.equals(rightAnswer)) {
                result.setSuccess(true);
                result.setMessage("√ Congratulation! The correct answer is " + rightAnswer.equals("1"));
                result.setCode(Code.SUCCESS);
                return result;
            }
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
            result.setMessage("× Wrong Answer! The correct answer is " + !rightAnswer.equals("0"));
            return result;
        }
        return result;
    }

    Title generateTheTitleBySynopsis(TitleType titleType,
                                     TitleTypeEnum typeEnum,
                                     String synopsis){
        int tempNum = rand.nextInt(titleType.getTitleCount() )+ 1;
        Title title = titleService.getTitleById(tempNum, synopsis);
//                判断随机生成的题目是否为判断题
        while (title.getTopicType() != typeEnum.getType()){
            tempNum = rand.nextInt(titleType.getTitleCount() )+ 1;
            title = titleService.getTitleById(tempNum,synopsis);
        }
        return title;
    }

    static class RequestSubmit {
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
