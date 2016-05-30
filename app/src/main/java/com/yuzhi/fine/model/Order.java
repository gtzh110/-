package com.yuzhi.fine.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lemon on 2016/4/6.
 */
public class Order extends BmobObject {
    private String name;
    private String address;
    private String phoneNumber;
    private String content;
    private Boolean isComplete = false;
    private User worker;//一个订单对应一个工人
    private User customer;//客户和自己的订单是一对一



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
