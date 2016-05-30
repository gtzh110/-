package com.yuzhi.fine.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by lemon on 2016/4/1.
 */
public class User extends BmobUser {
    //父类中已有的变量
/*    private String id;
    private String userName;
    private String password;
    private String mobilePhoneNumber;*/
    private Boolean isCustomer;//区分工人和客户
    private String score;//用户积分
    private Integer level;//用户积分对应的等级
    private String address;//客户的地址
    private String name;
    private String identity;
    private String selfIntroduce;
    private String certification;
    private String phoneNumber;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    private Boolean isApply = false;//工人是否申请工作
    private Boolean isOccupied = false;


    public void setIsOccupied(Boolean occupied) {
        isOccupied = occupied;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsApply(Boolean apply) {
        isApply = apply;
    }

    public Boolean getIsApply() {
        return isApply;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getCertification() {

        return certification;

    }

    public void setSelfIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public String getSelfIntroduce() {

        return selfIntroduce;
    }

    public void setIdentity(String identity) {

        this.identity = identity;
    }

    public String getIdentity() {

        return identity;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(Boolean customer) {
        isCustomer = customer;
    }
}
