package com.example.wkj_pc.rushtoanswer.service;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.hardware.Sensor;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.wkj_pc.rushtoanswer.R;

/**
 * Created by wkj_pc on 2017/5/16.
 */

public class GifView extends View {
    private long movieStart;
    private Movie movie;

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        movie = Movie.decodeStream(getResources().openRawResource(R.raw.shake));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long curTime= SystemClock.uptimeMillis();
        //第一次播放
        //第一次播放
        if (movieStart==0){
            movieStart=curTime;
        }
        if (null!=movie){
            long duration =movie.duration();
            int relTime= (int) ((curTime-movieStart)%duration);
            movie.setTime(relTime);
            //强制绘制
            movie.draw(canvas,0,0);
            invalidate();
        }

        super.onDraw(canvas);
    }
}
