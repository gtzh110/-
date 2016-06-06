package com.yuzhi.fine.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by lemon on 2016/4/6.
 */
public class Order extends BmobObject {
    private String name;
    private String address;
    private String phoneNumber;
    private String content;
    private Integer score;
    private Boolean isComplete = false;
    private Boolean isComment = false;
    private String comments;
    private User worker;//一个订单对应一个工人
    private User customer;//客户和自己的订单是一对一
    private String endDate ;


    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {

        return endDate;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {

        return score;
    }

    public void setIsComment(Boolean comment) {
        isComment = comment;
    }

    public Boolean getIsComment() {

        return isComment;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {

        return comments;
    }




    public void setCustomer(User customer) {
        this.customer = customer;
    }


    public User getCustomer() {
        return customer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {

        return content;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setIsComplete(Boolean complete) {
        isComplete = complete;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public void setName(String name) {

        this.name = name;
    }

    public User getWorker() {

        return worker;
    }

    public Boolean getIsComplete() {

        return isComplete;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }
}
