package com.example.mrxu.myviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.merxu.mypractice.R;

/**
 * Created by MrXu on 2017/6/13.
 */

public class ChartView extends View {

    public int XPoint = 120;    //原点的X坐标
    public int YPoint = 700;     //原点的Y坐标
    public int XScale = 60;     //X的刻度长度
    public int YScale = 70;     //Y的刻度长度
    public int XLength = 420;        //X轴的长度
    public int YLength = 350;        //Y轴的长度
    public String Title;    //显示的标题
    public String[] XLabel;    //X的刻度
               
    public String[] YLabel;    //Y的刻度
               
    public String[] Data;      //数据
    private int width;
    private int heigth;

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


   public void setData() {
       DisplayMetrics dm = new DisplayMetrics();
       // 通过Resources获取
       DisplayMetrics dm2 = getResources().getDisplayMetrics();

       width=dm2.widthPixels;
       heigth=dm2.heightPixels;


       XPoint=width/10;
       YPoint=heigth/2;
       XScale=width/9;
       YScale=heigth/3/7;
       XLength = width/10*8;        //X轴的长度
       YLength = heigth/3/10*9;

   }

    public void SetInfo(String[] XLabels, String[] YLabels, String[] AllData, String strTitle)
    {
        XLabel=XLabels;
        YLabel=YLabels;
        Data=AllData;
        Title=strTitle;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);//重写onDraw方法
//        canvas.drawColor(0x55ffceed);//设置背景颜色
        Paint paint= new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//去锯齿
        paint.setColor(getResources().getColor(R.color.chartview_line_color));//颜色
        paint.setStrokeWidth(5);
        Paint paint1=new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);//去锯齿
        paint1.setColor(Color.DKGRAY);
        paint1.setStrokeWidth(2);
        paint1.setTextSize(24);  //设置轴文字大小

        
        //设置Y轴
        canvas.drawLine(XPoint, YPoint-YLength, XPoint, YPoint, paint);   //轴线
        for(int i=0;i*YScale<YLength ;i++)                
        {
            canvas.drawLine(XPoint,YPoint-i*YScale, XPoint+20, YPoint-i*YScale, paint);  //刻度
            try
            {
                canvas.drawText(YLabel[i] , XPoint-60, YPoint-i*YScale+5, paint1);  //文字
            }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(XPoint,YPoint-YLength,XPoint-10,YPoint-YLength+26,paint);  //箭头
        canvas.drawLine(XPoint,YPoint-YLength,XPoint+10,YPoint-YLength+26,paint);
        //设置X轴
        canvas.drawLine(XPoint,YPoint,XPoint+XLength,YPoint,paint);   //轴线
        for(int i=0;i*XScale<XLength;i++)    
        {
            canvas.drawLine(XPoint+i*XScale, YPoint, XPoint+i*XScale, YPoint-20, paint);  //刻度
            try
            {
                canvas.drawText(XLabel[i] , XPoint+i*XScale-30, YPoint+40, paint1);  //文字
                //数据值
                    if(i>0&&YCoord(Data[i-1]+"")!=-999&&YCoord(Data[i]+"")!=-999)  //保证有效数据
                        canvas.drawLine(XPoint+(i-1)*XScale, YCoord(Data[i-1]+""), XPoint+i*XScale, YCoord(Data[i]+""), paint);
                    canvas.drawCircle(XPoint+i*XScale,YCoord(Data[i]+""), 2, paint);
           }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-26,YPoint-10,paint);    //箭头
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-26,YPoint+10,paint);
        paint.setTextSize(16);
        canvas.drawText(Title, 150, 50, paint1);
    }
    private float YCoord(String y0)  //计算绘制时的Y坐标，无数据时返回-999
                {
                    float y;
        try
        {
            y=Float.parseFloat(y0);
        }
        catch(Exception e)
        {
            return -999;    //出错则返回-999
        }
        try
        {
            return YPoint-y*YScale/Integer.parseInt(YLabel[1]);
        }
        catch(Exception e)
        {
        }
        return y;
    }

}
