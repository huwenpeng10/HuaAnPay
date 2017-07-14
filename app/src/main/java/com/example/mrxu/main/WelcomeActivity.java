package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


    @BindView(R.id.welcomevp)
    ViewPager welcomevp;
    @BindView(R.id.yuandian_layout1)
    ImageView yuandianLayout1;
    @BindView(R.id.yuandian_layout2)
    ImageView yuandianLayout2;
    @BindView(R.id.yuandian_layout3)
    ImageView yuandianLayout3;
    List <View> list;
    private TextView tv_break;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initParms(Bundle parms) {



    }

    @Override
    public int bindLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        list= new ArrayList<View>();
        LayoutInflater inflater=getLayoutInflater();
        View imageview1= (View) inflater.inflate(R.layout.welcome_layout1,null);
        View imageview2= (View) inflater.inflate(R.layout.welcome_layout2,null);
        View imageview3= (View) inflater.inflate(R.layout.welcome_layout3,null);
        tv_break=(TextView) imageview3.findViewById(R.id.tv_break);
        tv_break.setOnClickListener(this);
        list.add(imageview1);
        list.add(imageview2);
        list.add(imageview3);

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter();
        welcomevp.setOnPageChangeListener(this);
        welcomevp.setAdapter(viewPagerAdapter);
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()){
            case R.id.tv_break :
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                WelcomeActivity.this.finish();

                break;

                default:
                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position){
            case 0 :

                yuandianLayout1.setBackgroundResource(R.mipmap.yuandian1);
                yuandianLayout2.setBackgroundResource(R.mipmap.yuandian2);
                yuandianLayout3.setBackgroundResource(R.mipmap.yuandian2);

                break;
            case 1 :
                yuandianLayout1.setBackgroundResource(R.mipmap.yuandian2);
                yuandianLayout2.setBackgroundResource(R.mipmap.yuandian1);
                yuandianLayout3.setBackgroundResource(R.mipmap.yuandian2);

                break;
            case 2 :
                yuandianLayout1.setBackgroundResource(R.mipmap.yuandian2);
                yuandianLayout2.setBackgroundResource(R.mipmap.yuandian2);
                yuandianLayout3.setBackgroundResource(R.mipmap.yuandian1);

                break;


                default:
                break;

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class ViewPagerAdapter extends PagerAdapter{


        public ViewPagerAdapter(){

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}
