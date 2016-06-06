package com.yuzhi.fine.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lemon on 2016/6/3.
 */
public class UserInfo extends BmobObject {
    User worker;
    User customer;
    Order order;
    Integer score;
    Integer level;
    String comment;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setOrder(Order order) {

        this.order = order;
    }

    public User getCustomer() {

        return customer;
    }

    public Order getOrder() {
        return order;
    }


    public void setWorker(User worker) {
        this.worker = worker;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public User getWorker() {

        return worker;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getLevel() {
        return level;
    }
}
