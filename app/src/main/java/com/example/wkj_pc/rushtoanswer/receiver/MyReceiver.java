package com.example.wkj_pc.rushtoanswer.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.WindowManager;

import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.activity.JoinRushActivity;
import com.example.wkj_pc.rushtoanswer.activity.LaunchRushActivity;
import com.example.wkj_pc.rushtoanswer.activity.SplashActivity;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import cn.jpush.android.api.JPushInterface;
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收到服务器发送的命令
        Bundle bundle = intent.getExtras();
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        /*
        * 当接收到随机提问命令后
        * 弹出对话框，通知回答问题
        * */
        if (null!= message && message.contains("random")){
            String content = message.substring(7);
            AlertDialog alertDialog=new AlertDialog.Builder(context)
                .setCancelable(true)
                    .setIcon(R.mipmap.ic_head)
                    .setMessage("请 "+content+" 回答问题！")
                    .setPositiveButton("确定",null)
                    .setTitle("提问")
                    .create();
            if (Build.VERSION.SDK_INT>=23){
                if (!Settings.canDrawOverlays(context)){
                    Intent intent1=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

                    //JoinRushActivity.instance.startActivityForResult(intent1,1);
                    return;
                }else{
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();
                }
            }else{
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
                alertDialog.show();
            }

        }
        /*
        * 参与者接收到创建的活动
        * */
        if (null!=message && message.equals("launchrush")){
            if (null==OrderService.major && OrderService.joinActivtylife==1){   //1代表存在，0代表不存在
                if (null!=extras && extras.length()>0){
                    initSpinnerView.initSpinnerTeam(extras);
                }
            }
        }
        /*
        * 接受者接到开始抢答命令，执行抢答
        * */
        if(null!=message && message.equals("beginrush")){
            if (null!=OrderService.major && OrderService.major.equals("launcher")){
            }else{
                JoinRushActivity.instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (OrderService.vibrator &&OrderService.sound){    //提示音和震动同时开启
                            rushVibratorListener.rushVibrator();
                            rushSoundListener.setRushSound();
                            SystemClock.sleep(2000);
                            rushSoundListener.stop();
                        }else if (OrderService.sound && !OrderService.vibrator) { //声音开启
                            rushSoundListener.setRushSound();
                            SystemClock.sleep(2000);
                            rushSoundListener.stop();
                        }else if (!OrderService.sound && OrderService.vibrator){   //震动开启
                            rushVibratorListener.rushVibrator();
                            SystemClock.sleep(2000);
                        }
                        OrderService.begintime=System.currentTimeMillis();
                        JoinRushActivity.startBtn.setText("开始抢答");
                        JoinRushActivity.startBtn.setBackgroundColor(JoinRushActivity.instance
                                .getResources().getColor(R.color.startBtn));
                    }
                });
            }
        }
        /*
        * 得到接收抢答结果命令，向服务器请求结果数据
        * */
        if (null!=message && message.equals("rushresult")){
           if (null!=OrderService.major && OrderService.major.equals("launcher")) {
               resultListView.initResultListView();
               LaunchRushActivity.instance.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       LaunchRushActivity.startLaunchBtn.setText("重新抢答");
                       LaunchRushActivity.startLaunchBtn.setBackgroundColor(
                               LaunchRushActivity.instance.getResources().getColor(R.color.startBtn)
                       );
                   }
               });
           }else{
               resultListView.initResultListView();
           }
        }
        /*
        * 得到关闭活动命令，进行跳转
        * */
        if(null!=message && message.equals("closerush")){
            if (null==OrderService.major && OrderService.joinActivtylife==1 && null!=extras){
                extras=extras.substring(9,extras.length()-2);
                if (OrderService.joinMessage.getTag().equals(extras)){
                    closeRushActivity.closeRush();
                }
            }
        }
    }
    /*
    * 接口回调，声音
    * */
    public static RushSoundListener rushSoundListener;
    public interface RushSoundListener{
        void setRushSound();
        void stop();
    }
    public static void setRushSound(RushSoundListener listener){
        rushSoundListener=listener;
    }
    /*
       * 接口回调，震动
       * */
    public static RushVibratorListener rushVibratorListener;
    public interface RushVibratorListener{
        void rushVibrator();
    }
    public static void setRushVibrator(RushVibratorListener rushVibrator){
        rushVibratorListener=rushVibrator;
    }
    /*
   * 接口回调，关闭活动
   * */
    public static CloseRushActivity closeRushActivity;
    public interface CloseRushActivity {
        void closeRush();
    }
    public static void setCloseRush(CloseRushActivity closeRush){
        closeRushActivity=closeRush;
    }
    /*
       * 接口回调，设置活动名字
       * */
    public static InitSpinnerView initSpinnerView;
    public interface InitSpinnerView{
        void initSpinnerTeam(String content);
    }
    public static void setSpinnerView(InitSpinnerView minitSpinnerView) {
       initSpinnerView=minitSpinnerView;
    }
    /*
   * 接口回调，展示结果视图
   * */
    public static ResultListView resultListView;
    public interface ResultListView{
      void  initResultListView();
    }
    public static void setResultListView(ResultListView mResultListView){
        resultListView=mResultListView;
    };
}
