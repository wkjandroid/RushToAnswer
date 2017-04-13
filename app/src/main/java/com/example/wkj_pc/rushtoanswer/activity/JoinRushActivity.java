package com.example.wkj_pc.rushtoanswer.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.example.wkj_pc.rushtoanswer.OrderAdapter;
import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.po.OrderMessage;
import com.example.wkj_pc.rushtoanswer.receiver.MyReceiver;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class JoinRushActivity extends AppCompatActivity {
    @BindView(R.id.result_listview)
    ListView resultListview;
    @BindView(R.id.preare_to_rush)
    Button preareToRush;
    public static Activity instance;
    public static Button startBtn;
    private static String url="http://119.29.154.229/RushToAnswer/OrderServlet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rush);
        instance=this;
        startBtn = (Button) findViewById(R.id.preare_to_rush);
        ButterKnife.bind(this);
        initResultView();
        initListener();
        initVibrator(new long[]{0,2000},-1);
    }
    /*
    * 接口回调，启动震动事件
    * */
    private void initVibrator(final long[] pattern, final int repeat) {
        MyReceiver.setRushVibrator(new MyReceiver.RushVibratorListener() {
            @Override
            public void rushVibrator() {
                Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern,repeat);
            }
        });
    }
/*
* 展示抢答结果
* */
    private void initResult() {
        if (OrderService.orderList!=null && OrderService.orderList.size()>0){
            OrderAdapter adapter=new OrderAdapter(JoinRushActivity.this,R.layout.response_item,0,
                    OrderService.orderList);
            resultListview.setAdapter(adapter);
        }
    }
    /*
    * 运行中，展示抢答结果
    * */
    public void initResultView()
    {
        MyReceiver.setResultListView(new MyReceiver.ResultListView() {
            @Override
            public void initResultListView() {
                NetWorkUtils.getNetWorkUtils().sendOrderToServer("grade", OrderService.joinMessage.getTag(), url, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {}
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
    private void initListener() {
        /*
        * 抢答按钮设置
        * */
        preareToRush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preareToRush.getText().toString().equals("开始抢答")){
                    OrderService.joinMessage.setIntention(4);  //Intention=4代表发送抢答时间，进行比较排名
                    OrderService.joinMessage.setOrdernum(System.currentTimeMillis()-OrderService.begintime);
                    NetWorkUtils.getNetWorkUtils().sendOrderToServer("order", GsonUtils.getGson().
                            toJson(OrderService.joinMessage),url,new okhttp3.Callback(){
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                        }
                    });
                    preareToRush.setText("抢答完毕");
                    preareToRush.setBackgroundColor(getResources().getColor(R.color.stopBtn));
                }
            }
        });
    }
}
