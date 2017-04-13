package com.example.wkj_pc.rushtoanswer.utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * Created by wkj_pc on 2017/3/31.
 */

public class NetWorkUtils {
    public NetWorkUtils(){}
    private OkHttpClient client;
    public static NetWorkUtils getNetWorkUtils(){
        return new NetWorkUtils();
    }
    public  void sendOrderToServer(String order, String uploadContent, String uploadUrl,okhttp3.Callback callback){
        client=new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add(order,uploadContent)
                .build();
        Request request=new Request.Builder().url(uploadUrl)
                .post(body).build();
        client.newCall(request).enqueue(callback);

    }
    public void getFromServer(final String downloadUrl,okhttp3.Callback callback){
        client =new OkHttpClient();
        Request request=new Request.Builder().
                url(downloadUrl).build();
        client.newCall(request).enqueue(callback);
    }

}
