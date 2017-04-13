package com.example.wkj_pc.rushtoanswer.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkj_pc on 2017/4/2.
 */

public class ActivityCollections {
    public static List<Activity> activityList=new ArrayList<>();
    public static void  addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static void removeAll(){
        activityList.clear();
    }
}
