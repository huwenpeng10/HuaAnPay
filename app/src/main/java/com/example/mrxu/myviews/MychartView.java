package com.example.mrxu.myviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by MrXu on 2017/6/13.
 */

public class MychartView extends View {
    Context context;
    public MychartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    private int Xp=80;
    private int Yp=300;

    private int Xplenth=400;
    private int Yplenth=250;


    @Override
    protected void onDraw(Canvas canvas) {


        DisplayMetrics dm = new DisplayMetrics();
        // 通过Resources获取
        DisplayMetrics dm2 = getResources().getDisplayMetrics();

        int width=dm2.widthPixels;
        int heigth=dm2.heightPixels;
        System.out.println("heigth2 : " + heigth);
        System.out.println("width2 : " + width );

//        canvas.drawColor(0x55ffFF00);
        Paint paint=new Paint();
        paint.setColor(0x55FF0000);
//        paint.setTextSize(100);
//        //让画出的图形是空心的  
        paint.setStyle(Paint.Style.STROKE);
//        //设置画出的线的 粗细程度  
        paint.setStrokeWidth(5);
//
//        canvas.drawLine(Xp,Yp,Xp,Yp-Yplenth,paint);
//        canvas.drawLine(Xp,Yp-Yplenth,Xplenth,Yp-Yplenth,paint);
//        canvas.drawLine(Xplenth,Yp-Yplenth,Xplenth,Yp,paint);
//        canvas.drawLine(Xplenth,Yp,Xp,Yp,paint);


        paint.setColor(Color.LTGRAY);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        canvas.drawText("画圆角矩形:", 10, 260, paint);
        RectF oval3 = new RectF();// 设置个新的长方形
        canvas.drawRoundRect(oval3, 20, 15, paint);//第二个参数是x半径，第三个参数是y半径

        super.onDraw(canvas);

    }
}
