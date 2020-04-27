package com.example.title.controller;

import com.example.title.pojo.Title;
import com.example.title.pojo.TitleType;
import com.example.title.pojo.User;
import com.example.title.service.TitleService;
import com.example.title.service.UserService;
import com.example.title.util.Code;
import com.example.title.util.CommonResult;
import com.example.title.util.TitleTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * @Create: 27/04/2020 15:33
 * @Author: Q
 */
@Controller
@RequestMapping("/titles/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TitleService titleService;

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
            return "forward:/titles/admin/manage";
        }
        logger.warn("{} will be deleted",user);
        userService.deleteUserById(id);
        return "forward:/titles/admin/manage";
    }

    @GetMapping("import")
    public String importFile(){
        return "admin/import";
    }

    @ResponseBody
    @PostMapping("/fileupload")
    public CommonResult upload(@RequestParam("file") MultipartFile file,
                               HttpServletRequest request,
                               @RequestParam("synopsis")String synopsis,
                               @RequestParam("description")String description){
        CommonResult result = new CommonResult();
        if (file.isEmpty()){
            logger.warn("{} upload an empty file.",request.getRemoteAddr());
        }
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        logger.info("upload name:{} type:{}",fileName, contentType);
        if (!contentType.contains("text")){
            result.setMessage("Not the support type:txt!");
            result.setSuccess(false);
            result.setCode(Code.USER_ERROR);
            return result;
        }
//        新建表，需要前端传入一个synopsis以及description
        logger.info("It will create a table {}",synopsis);
        titleService.createTitleTable(synopsis);
        TitleType titleType = new TitleType();
        titleType.setSynopsis(synopsis);
        titleType.setDescription(description);
        logger.info("It will create a titleType {}",titleType);
        titleService.setTitleType(titleType);
//        假设传入的数据严格按照顺序进入(都以\t分隔)
//        单选题	"以俄国十月革命的胜利为标志,中国资产阶级民主革命的范畴已经转变为属于()"	世界资产阶级革命;世界无产阶级社会主义革命;世界农民阶级革命;世界新民主主义革命	b
//        对应于Title对象
//        topicType topicDescription options answer
        try(InputStream is = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            Title title = new Title();
            String tempStr;
            while((tempStr = reader.readLine()) != null){
//            那么这里split的大小应该固定为4，或者4（判断题第三位原本应该是选项值的位置为空）
                String[] split = tempStr.split("\t");
                TitleTypeEnum titleTypeEnum = null;
                if (split[0].contains("单选")){
                    titleTypeEnum = TitleTypeEnum.SINGLE;
                }else if (split[0].contains("多选")){
                    titleTypeEnum = TitleTypeEnum.MULTIPLE;
                }else{
                    titleTypeEnum = TitleTypeEnum.JUDGEMENT;
                    title.setTopicType(titleTypeEnum.getType());
                    title.setTopicDescription(split[1]);
                    title.setType(description);
//                判断题没有选项
                    final String answer = split[3];
                    title.setAnswer(answer);
                    titleService.setTitle(title,titleType);
                    continue;
                }
//                我觉得这列是多余的
                title.setType(description);
                title.setTopicType(titleTypeEnum.getType());
                title.setTopicDescription(split[1]);
                title.setOptions(split[2]);
                title.setAnswer(split[3]);
                titleService.setTitle(title,titleType);
            }
        } catch (IOException e) {
            logger.error("upload error!");
            result.setCode(Code.SYSTEM_ERROR);
            result.setMessage("upload error!");
            result.setSuccess(false);
            return result;
        }
        result.setMessage("Success!");
        result.setCode(Code.SUCCESS);
        result.setSuccess(true);
        logger.info(result.toString());
        return result;
    }
}
