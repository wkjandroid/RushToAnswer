package com.example.wkj_pc.rushtoanswer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.wkj_pc.rushtoanswer.R;
import com.example.wkj_pc.rushtoanswer.po.OrderMessage;
import com.example.wkj_pc.rushtoanswer.receiver.MyReceiver;
import com.example.wkj_pc.rushtoanswer.service.OrderService;
import com.example.wkj_pc.rushtoanswer.utils.GsonUtils;
import com.example.wkj_pc.rushtoanswer.utils.NetWorkUtils;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Response;
public class JoinActivity extends AppCompatActivity {
    private String url="http://119.29.154.229/RushToAnswer/OrderServlet";
    @BindView(R.id.image_avatar)
    ImageView imageAvatar;
    @BindView(R.id.nick_name)
    EditText nickName;
    @BindView(R.id.jointorush_btn)
    Button jointorush_btn;
    @BindView(R.id.spinner)
    Spinner spinner;
    private byte[] orderbitmap;
    private String tag=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).
                            setCancelable(true).setTitle("提醒").setPositiveButton("确定",null)
                            .setMessage("服务器繁忙...").create();
                    alertDialog.show();
                    break;
                case 2:
                    nickName.setError("该昵称已存在！");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        OrderService.joinActivtylife = 1;
         /*
        * 当参加的活动，技术后跳转到该界面。弹出对话框
        * */
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        if (!TextUtils.isEmpty(key)){
            AlertDialog alertDialog=new AlertDialog.Builder(this).setTitle("提醒")
                    .setMessage(key).setPositiveButton("确定",null)
                    .setCancelable(true).create();
            alertDialog.show();
        }
        ButterKnife.bind(this);
        initSpinnerData();
        initListener();
        initData();
    }
    private void initSpinnerData() {
        /*
        * 访问服务器获取已经存在的活动，显示在活动下拉框中
        * */
        NetWorkUtils.getNetWorkUtils().sendOrderToServer("tag","tag",url,new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if (!TextUtils.isEmpty(responseData)){
                    Set<String> set= GsonUtils.getGson().fromJson(responseData,
                            new TypeToken<Set<String>>(){}.getType());
                    if (null!=set){
                       OrderService.orderTeam=new String[set.size()];
                        set.toArray(OrderService.orderTeam);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter adapter=new ArrayAdapter(JoinActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,OrderService.orderTeam);
                                spinner.setAdapter(adapter);
                            }
                        });
                    }
                }
            }
        });
    }
    /*
    * 将获取到的json数据，进行切割展示成活动名称
    * */
    private void initData() {
        MyReceiver.setSpinnerView(new MyReceiver.InitSpinnerView() {
            @Override
            public void initSpinnerTeam(String content) {
                if (null!=content && content.length()>0){
                    List<String> list = new ArrayList<>();
                    String[] split = content.substring(content.indexOf("[")+1, content.lastIndexOf("]")).split("\"");
                    for (int i=1;i<split.length;i++){
                        if (!split[i].contains(",")){
                            list.add(split[i].substring(0,split[i].length()-1));
                        }
                    }
                    OrderService.orderTeam=new String[list.size()];
                    list.toArray(OrderService.orderTeam);
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(JoinActivity.this,android.R.layout.simple_spinner_dropdown_item,OrderService.orderTeam);
                    spinner.setAdapter(adapter);
                }
            }
        });
    }
    private void initListener() {
        /*
        * 定义下拉框的点击事件
        * */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null!=OrderService.orderTeam){
                    tag=OrderService.orderTeam[position];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        jointorush_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nickName.getText().toString();
                if (null==tag){
                    nickName.setError("没有可选的活动！");
                    return;
                }
                if (TextUtils.isEmpty(nickname)) {
                    nickName.setError("昵称不能为空！");
                } else {
                    OrderMessage message = new OrderMessage(nickname, 0,orderbitmap);
                    message.setIntention(2);            //一代表创建抢答，二代表加入抢答
                    message.setTag(tag);
                    JPushInterface.setAlias(JoinActivity.this, message.getTag().trim(), new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                        }
                    });
                    /*
                    * 访问服务器，加入抢答活动
                    * */
                    NetWorkUtils.getNetWorkUtils().sendOrderToServer("order",GsonUtils.getGson().toJson(message),
                            url,new okhttp3.Callback(){
                         @Override
                         public void onFailure(Call call, IOException e) {
                             e.printStackTrace();
                               Message mg=new Message();
                               mg.what=1;
                               handler.sendMessage(mg);
                         }
                         @Override
                         public void onResponse(Call call, Response response) throws IOException {
                                OrderMessage mg = null;
                                String responseData = response.body().string();
                             /*
                             * 昵称已经存在
                             * */
                             if (!TextUtils.isEmpty(responseData) && responseData.trim().equals("false")) {
                                    Message message = new Message();        //false表示已经存在
                                    message.what = 2;
                                    handler.sendMessage(message);
                                } else if (!TextUtils.isEmpty(responseData)) {
                                 /*
                                 * 加入成功
                                 * */
                                 mg = GsonUtils.getGson().fromJson(responseData, OrderMessage.class);
                                    if (mg.getIntention() == 2) {      //intention==2表示加入成功
                                        OrderService.joinMessage = mg;
                                        OrderService.major=null;
                                        Intent intent = new Intent(JoinActivity.this, JoinRushActivity.class);
                                        startActivity(intent);
                                    }
                                }
                         }
                    });
                }
            }
        });
        /*、
        * 点击图形弹出菜单按钮
        * */
        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openOptionsMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /*
    * 打开菜单按钮方法
    * */
    @Override
    public void openOptionsMenu() {
        View toolbar = getWindow().findViewById(R.id.action_bar);
        if (toolbar instanceof Toolbar) {
            ((Toolbar) toolbar).showOverflowMenu();
        } else {
            openOptionsMenu();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }
    /*
    * 设置按钮点击事件
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.takephoto:
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takephoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
                startActivityForResult(takephoto, 1);
                break;
            case R.id.album:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
                break;
        }
        return true;
    }
    /*
    * 得到活动处理结果
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0)
            return;
        // 拍照
        if (requestCode == 1) {
            //设置文件保存路径这里放在跟目录下
            File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
            startPhotoZoom(Uri.fromFile(outputImage));
        }
        if (data == null)
            return;
        // 读取相册缩放图片
        if (requestCode == 2) {
            startPhotoZoom(data.getData());
        }
        // 处理结果
        if (requestCode == 3) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件
                imageAvatar.setImageBitmap(photo);
                orderbitmap=stream.toByteArray();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*
    * 将图片进行剪切
    * */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    @Override
    protected void onDestroy() {
        OrderService.joinActivtylife=0;
        super.onDestroy();
    }
}
