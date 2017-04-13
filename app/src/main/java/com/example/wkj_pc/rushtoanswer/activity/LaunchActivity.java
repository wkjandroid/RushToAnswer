package com.example.wkj_pc.rushtoanswer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.po.OrderMessage;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Response;
public class LaunchActivity extends AppCompatActivity {

    @BindView(R.id.team_name)
    EditText teamName;
    @BindView(R.id.create_btn)
    Button createBtn;
    private String url = "http://119.29.154.229/RushToAnswer/OrderServlet";
    private String tag=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).setCancelable(true).setTitle("提醒")
                            .setMessage("服务器繁忙...").create();
                    alertDialog.show();
                    break;
                case 2:
                    teamName.setError("该组已被创建...");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        initListener();
    }
    private void initListener() {
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*设置活动名称*/
                if (TextUtils.isEmpty(teamName.getText().toString())){
                    teamName.setError("活动名称不能为空！");
                    return;
                }else{
                    tag=teamName.getText().toString();
                    JPushInterface.setAlias(LaunchActivity.this, tag, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                        }
                    });
                    /*
                    * 记住自己创建的活动，以便在程序结束后，销毁自己创建的活动
                    * */
                    if (null==OrderService.createTeam){
                        OrderService.createTeam=new ArrayList<String>();
                    }
                    OrderService.createTeam.add(tag);
                    OrderMessage message=new OrderMessage(tag,0,null);
                    message.setIntention(1);    //1代表创建 2代表加入
                    message.setTag(tag);        //代表创建哪个组
                    /*访问服务器，创建自己的或送*/
                    NetWorkUtils.getNetWorkUtils().sendOrderToServer("order", GsonUtils.getGson().toJson(message),
                            url,new okhttp3.Callback(){
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message message=new Message ();
                                    message.what=1;     //1代表失败
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData=response.body().string();
                                    /*
                                    * 自己创建的活动已经存在
                                    * */
                                    if (null!=responseData && responseData.trim().equals("false")){
                                        Message message=new Message();
                                        message.what=2;
                                        handler.sendMessage(message);
                                    }else{
                                        /*
                                        * 创建成功
                                        * */
                                        OrderMessage mg = new Gson().fromJson(responseData,OrderMessage.class);
                                        if (mg.getIntention()==1){
                                            OrderService.launchMessage=mg;
                                            OrderService.major="launcher";
                                            Intent intent=new Intent(LaunchActivity.this,LaunchRushActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}
