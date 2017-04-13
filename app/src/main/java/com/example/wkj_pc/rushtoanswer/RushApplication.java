package com.example.wkj_pc.rushtoanswer;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by wkj_pc on 2017/3/31.
 */

public class RushApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*
        * 初始化JPush，设置模式
        * */
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
