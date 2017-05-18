package com.example.wkj_pc.rushtoanswer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wkj_pc.rushtoanswer.OrderAdapter;
import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.po.OrderMessage;
import com.example.wkj_pc.rushtoanswer.receiver.MyReceiver;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
public class LaunchRushActivity extends AppCompatActivity {
    private static String url="http://119.29.154.229/RushToAnswer/OrderServlet";
    public static Button startLaunchBtn;
    @BindView(R.id.start_rush_btn)
    Button startRushBtn;
    @BindView(R.id.random_quiz_btn)
    Button randomQuiz;
    @BindView(R.id.order_listview)
    ListView orderListview;         //结果展示链表
    public static Activity instance;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    Toast.makeText(LaunchRushActivity.this,"服务器繁忙！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListener();
        instance=this;
        startLaunchBtn=(Button)findViewById(R.id.start_rush_btn);
        initResultView();
    }
    private void initResult(){
        /*
        * 将接收到的抢答数据进行展示
        * */
        if (OrderService.orderList!=null && OrderService.orderList.size()>0){
            OrderAdapter adapter=new OrderAdapter(LaunchRushActivity.this,R.layout.response_item,0,
                    OrderService.orderList);
            orderListview.setAdapter(adapter);
        }
    }
    /*
        1，接口回调得到结果命令，访问服务器
    * 运行中获取到结果数据，进行展示
    * */
    public void initResultView()
    {
        MyReceiver.setResultListView(new MyReceiver.ResultListView() {
            @Override
            public void initResultListView() {
                NetWorkUtils.getNetWorkUtils().sendOrderToServer("grade", OrderService.launchMessage.getTag(), url, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        if (null != responseData) {
                            OrderService.orderList=GsonUtils.parseListFromServerJson(responseData,OrderMessage.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initResult();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    /*
    * 初始化按钮，随着抢答更新文字和图片，以及发送命令
    * */
    private void initListener() {
        randomQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderService.launchMessage.setIntention(6);     //intenttion==6 代表随机提问

                NetWorkUtils.getNetWorkUtils().sendOrderToServer("order",
                        GsonUtils.getGson().toJson(OrderService.launchMessage),url,new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
            }
        });
        startRushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startRushBtn.getText().toString().equals("停止抢答")) {
                    OrderMessage message = OrderService.launchMessage;
                    message.setIntention(5);        //intenttion=5代表发起停止抢答命令
                    NetWorkUtils.getNetWorkUtils().sendOrderToServer("order", GsonUtils.getGson().toJson(message),
                            url, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                }
                            });
                    startRushBtn.setText("重新抢答");
                    startRushBtn.setBackgroundColor(getResources().getColor(R.color.startBtn));
                    return;
                }
                if (startRushBtn.getText().toString().equals("开始抢答") ||
                        startRushBtn.getText().toString().equals("重新抢答")) {
                    OrderMessage message = OrderService.launchMessage;
                    message.setIntention(3);        //intenttion=3代表发起抢答命令
                    NetWorkUtils.getNetWorkUtils().sendOrderToServer("order", GsonUtils.getGson().toJson(message),
                            url, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                }
                            });
                    startRushBtn.setText("停止抢答");
                    startRushBtn.setBackgroundColor(getResources().getColor(R.color.stopRush));
                }
            }
        });
    }
}