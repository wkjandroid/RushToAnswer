package com.example.wkj_pc.rushtoanswer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wkj_pc.rushtoanswer.po.OrderMessage;

import java.util.List;

/**
 * Created by wkj_pc on 2017/3/31.
 */

public class OrderAdapter extends ArrayAdapter {
    private View view;
    private List <OrderMessage> responseList;
    private int resoure;
    public OrderAdapter(@NonNull Context context, @LayoutRes int resource,
                        @IdRes int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
        this.responseList=objects;
        this.resoure=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        OrderMessage message=null;
        message = responseList.get(position);
        ViewHolder holder=null;
        if (null==convertView){
            view = LayoutInflater.from(getContext()).inflate(R.layout.response_item, parent, false);
            holder=new ViewHolder();
            holder.accountText= (TextView) view.findViewById(R.id.account_text);
            holder.imageView=(ImageView) view.findViewById(R.id.image_avatar);
            holder.orderText=(TextView) view.findViewById(R.id.order_text);
            view.setTag(holder);
        }else {
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        long ordernum = message.getOrdernum();
        holder.orderText.setText(String.valueOf(position+1));
        holder.accountText.setText(message.getAccount());
        if (null!=message.getImageAvatar()){
            byte[] bytes = message.getImageAvatar();
            System.out.println("接受大小+"+bytes.length);
            holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
        }
        return view;
    }
    class ViewHolder{
        ImageView imageView;
        TextView accountText;
        TextView orderText;
    }
}
