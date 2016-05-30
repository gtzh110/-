package com.yuzhi.fine.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lemon on 2016/4/11.
 */
public class AppliedWorker extends BmobObject {
    private User worker;
    private String name;
    private Integer level;
    private String score;

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {

        return name;
    }

    public Integer getLevel() {
        return level;
    }

    public String getScore() {
        return score;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }
}
