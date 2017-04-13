package com.example.wkj_pc.rushtoanswer.service;

import com.example.wkj_pc.rushtoanswer.po.OrderMessage;

import java.util.List;

/**
 * Created by wkj_pc on 2017/4/3.
 */

public class OrderService {
    public static OrderMessage joinMessage;     //参与者信息
    public static OrderMessage launchMessage;   //发起者信息
    public static String [] orderTeam;          //或送小组名称
    public static String major;                 //身份
    public static long begintime;                   //抢答开始事件
    public static int joinActivtylife;      //1表示存在0表示不存在
    public static List <OrderMessage>orderList;     //抢答结果数据
    public static List <String> createTeam; //代表launcher创建了几个抢答活动，好在程序结束后，将创建的活动进行销毁。
    public static boolean sound=true;       //提示音和震动
    public static boolean vibrator=true;
}
