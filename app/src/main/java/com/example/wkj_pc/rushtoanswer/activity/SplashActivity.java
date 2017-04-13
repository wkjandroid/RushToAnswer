package com.example.wkj_pc.rushtoanswer.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.receiver.MyReceiver;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {
    private static String url = "http://119.29.154.229/RushToAnswer/OrderServlet";
    public static MediaPlayer player;
    @BindView(R.id.create_rush_btn)
    ImageView createRushBtn;
    @BindView(R.id.join_rush_btn)
    ImageView joinRushBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initListener();
        /*
        * 接口回调，当参加的活动结束，跳转到加入界面
        * */
        MyReceiver.setCloseRush(new MyReceiver.CloseRushActivity() {
            @Override
            public void closeRush() {
                finish();
                Intent intent=new Intent(SplashActivity.this,JoinActivity.class);
                intent.putExtra("key","Sorry, you participate in the activities has ended");
                startActivity(intent);

            }
        });
        /*
        * 接口回调，播放提示音
        * */
        MyReceiver.setRushSound(new MyReceiver.RushSoundListener() {
            @Override
            public void setRushSound() {
                player=MediaPlayer.create(SplashActivity.this,R.raw.a);
                player.start();
            }
            @Override
            public void stop() {
                player.stop();
            }
        });
    }
    /*
    * 创建菜单按钮
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tongzhi_item,menu);
        return super.onCreateOptionsMenu(menu);
    }
    /*
    * 为菜单添加选项卡，定义时候需要警示音和震动提醒
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sound:
                if (item.isChecked()){
                    OrderService.sound=false;
                    item.setChecked(false);
                }else{
                    OrderService.sound=true;
                    item.setChecked(true);
                }
                break;
            case R.id.vibrator:
                if (item.isChecked()){
                    OrderService.vibrator=false;
                    item.setChecked(false);
                }else{
                    OrderService.vibrator=true;
                    item.setChecked(true);
                }
                break;
        }
        return true;
    }
    /*
    * 为button设置点击事件进行跳转
    * */
    private void initListener() {
        createRushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, LaunchActivity.class);
                startActivity(intent);
            }
        });
        joinRushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }
    /*
    * 在程序结束后，销毁自己创建的活动
    * */
    @Override
    protected void onDestroy() {
        if (null != OrderService.createTeam && OrderService.createTeam.size() > 0) {
            NetWorkUtils.getNetWorkUtils().sendOrderToServer("closeTeam", GsonUtils.getGson().toJson
                    (OrderService.createTeam), url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }
            });
        }
        super.onDestroy();
    }
}
