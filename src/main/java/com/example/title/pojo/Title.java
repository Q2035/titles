package com.example.title.pojo;

public class Title {
    private Integer id;
    private String topicDescription;
    private String options;
    private String answer;
//    单选，多选,以及判断（分别使用1，2，3表示）
    private Integer topicType;
//    类型：毛概、马原
    private String type;
    private String note;
    private Integer countOfWrong;
    private Integer countOfDone;

//   页面显示的option
    private transient String[] splits;

    public String[] getSplits() {
        return splits;
    }

    public void setSplits(String[] splits) {
        this.splits = splits;
    }

    public Integer getTopicType() {
        return topicType;
    }

    public void setTopicType(Integer topicType) {
        this.topicType = topicType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getCountOfWrong() {
        return countOfWrong;
    }

    public void setCountOfWrong(Integer countOfWrong) {
        this.countOfWrong = countOfWrong;
    }

    public Integer getCountOfDone() {
        return countOfDone;
    }

    public void setCountOfDone(Integer countOfDone) {
        this.countOfDone = countOfDone;
    }
}
