先做一个大纲吧，目的就是做一个可以做题目的网站
首页：
- 登录（支持登录，保存错题信息）
    - 可以选择只做错题，错题会有明显标示
- 选择题目类型（随机做，考试类型）
----
index：

Index页面应当分两种，一种是已登录的，一种是未登录的

已登录：   
- 忘记密码以后在做
- 具有导航栏
- 可以查看个人做题情况

未登录（访客模式）：
- 没有导航栏
- 可以直接做题，但是不计入情况
    

POJO:

 - User
    - id
    - username　用户登录名
    - password 用户登录密码
    - nickname 昵称
    - email 用户邮箱
  
 - TitleDoneBy#{username} 统计用户所做的题目(每个用户一张表)
    - id
    - type 题目类型
    - titleId 题目ID
    - isRight 题目是否做对
 
 - Title
    - id
    - topicDescribes 题干
    - options List<String>　选项
    - answer 答案
    - type 类型（马原,毛概..）
    - note 备注信息
    - countOfWrong 做错统计
    - countOfDo 做题统计

2020.4.27

    今天实现了将txt文件文件内容读取入数据库；
    完善了各个用户界面；