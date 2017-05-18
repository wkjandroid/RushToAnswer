package com.example.wkj_pc.rushtoanswer.po;

import android.graphics.Bitmap;

/**
 * Created by wkj_pc on 2017/3/31.
 */

public class OrderMessage {
    private String account;     //抢答活动名或者参加者昵称
    private long ordernum;      //抢答名词
    private byte[] imageAvatar; //参与者头像
    private int intention;       //意图
    private String tag;         //活动名
    private String rushMethod;  //抢答方法

    public String getRushMethod() {
        return rushMethod;
    }

    public void setRushMethod(String rushMethod) {
        this.rushMethod = rushMethod;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public int getIntention() {
        return intention;
    }
    public void setIntention(int intention) {
        this.intention = intention;
    }
    public OrderMessage(String account, long ordernum, byte[] imageAvatar) {
        this.account = account;
        this.ordernum=ordernum;
        this.imageAvatar=imageAvatar;
    }
    public OrderMessage(){
    }
    public OrderMessage(String account, long ordernum, byte[] imageAvatar, int intention, String tag) {
        this.account = account;
        this.ordernum = ordernum;
        this.imageAvatar = imageAvatar;
        this.intention = intention;
        this.tag = tag;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public long getOrdernum() {
        return ordernum;
    }
    public void setOrdernum(long ordernum) {
        this.ordernum = ordernum;
    }
    public byte[] getImageAvatar() {
        return imageAvatar;
    }
    public void setImageAvatar(byte[] imageAvatar) {
        this.imageAvatar = imageAvatar;
    }
}
