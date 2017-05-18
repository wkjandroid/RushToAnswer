package com.example.wkj_pc.rushtoanswer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.wkj_pc.rushtoanswer.OrderAdapter;
import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.po.OrderMessage;
import com.example.wkj_pc.rushtoanswer.receiver.MyReceiver;
import com.example.wkj_pc.rushtoanswer.service.GifView;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import com.example.wkj_pc.rushtoanswer.service.SensorImp;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class ShakeRushActivity extends AppCompatActivity {
    @BindView(R.id.shake_gif_view)
    GifView gifView;
    @BindView(R.id.shake_result_listview)
    ListView resultListview;
    @BindView(R.id.linear_layout)
    LinearLayout linearLayout;
    private static String url="http://119.29.154.229/RushToAnswer/OrderServlet";
    private SensorImp sensorImp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_rush);
        ButterKnife.bind(this);
        initResultView();
    }

    @Override
    protected void onResume() {
        resultListview.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        // gifView.setVisibility(View.VISIBLE);
        gifView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        if (null==sensorImp)
        {
            sensorImp = new SensorImp(this,url);
        }
        sensorImp.start();
        super.onResume();
    }

    /*
        * 展示抢答结果
        * */
    private void initResult() {
        if (OrderService.orderList!=null && OrderService.orderList.size()>0){
            OrderAdapter adapter=new OrderAdapter(ShakeRushActivity.this,R.layout.response_item,0,
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
                            OrderService.orderList= GsonUtils.parseListFromServerJson(responseData,OrderMessage.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                resultListview.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.GONE);
                                initResult();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

}

