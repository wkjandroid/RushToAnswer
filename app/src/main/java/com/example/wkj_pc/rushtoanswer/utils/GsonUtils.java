package com.example.wkj_pc.rushtoanswer.utils;

import com.example.wkj_pc.rushtoanswer.po.OrderMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkj_pc on 2017/4/2.
 */

public class GsonUtils {
    private static Gson gson;
    static{
        gson=new Gson();
    }
    public static Gson getGson(){
        return gson;
    }
    public static <T> List <T> parseListFromServerJson(String str, Class<T> cls){
        JsonArray array=new JsonParser().parse(str).getAsJsonArray();
        List<T> list=new ArrayList<>();
        for (JsonElement element:array){
            list.add(GsonUtils.getGson().fromJson(element, cls));
        }
        return list;
    }
}
