package com.example.wkj_pc.rushtoanswer.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;

import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wkj_pc on 2017/5/16.
 */

public class SensorImp implements SensorEventListener {
    private final String url;
    private Sensor sensor;
    private SensorManager manager;
    private Context mContext;
    private long curTime;
    private long lastTime;
    private  long mInterval=60;
    private float nowX,nowY,nowZ;
    private double mSpeed=4000;
    private long startTime;
    private int num;
    public SensorImp(Context context,String url) {
        this.mContext=context;
        this.url=url;
    }
    public void start() {
        num=0;
        manager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (null!=manager){
            startTime=System.currentTimeMillis();   //抢答开始计数时间
            sensor=manager.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER);
            manager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);
        }

      }
    private void stop(){
        manager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        curTime = System.currentTimeMillis();
        if ((curTime-lastTime)<mInterval){
            return;
        }
        lastTime=curTime;
        float valueX = event.values[0];
        float valueY = event.values[1];
        float valueZ = event.values[2];
        float relX=valueX-nowX;
        float relY=valueY-nowY;
        float relZ=valueZ-nowZ;
        nowX=valueX;
        nowY=valueY;
        nowZ=valueZ;
        double relSpeed = Math.sqrt(relX * relX + relY * relY + relZ * relZ) / mInterval * 10000;
        if (relSpeed>mSpeed){
            if (++num>2){
                MediaPlayer player=MediaPlayer.create(mContext, R.raw.win);
                player.start();
                long endTime= System.currentTimeMillis();
                OrderService.joinMessage.setOrdernum(endTime-startTime);
                OrderService.joinMessage.setIntention(4);//发送抢答数据
                stop();
                NetWorkUtils.getNetWorkUtils().sendOrderToServer("order", GsonUtils.getGson().
                        toJson(OrderService.joinMessage),url,new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
