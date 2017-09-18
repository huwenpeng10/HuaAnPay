package com.example.mrxu.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.merxu.mypractice.R;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.fragment.Main_Fragment;
import com.example.mrxu.fragment.MessageFragment;
import com.example.mrxu.fragment.User_Fragment;
import com.example.mrxu.mutils.MainUtils;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener,
        RadioGroup.OnCheckedChangeListener,
        Main_Fragment.OnFragmentInteractionListener,
        MessageFragment.OnFragmentInteractionListener,
        User_Fragment.OnFragmentInteractionListener {


    @BindView(R.id.my_viewpager)
    ViewPager myViewpager;
    @BindView(R.id.main_layout)
    AutoLinearLayout mainLayout;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.radio_main)
    RadioButton radioMain;
    @BindView(R.id.radio_message)
    RadioButton radioMessage;
    @BindView(R.id.radio_user)
    RadioButton radioUser;
    @BindView(R.id.radiogrup)
    RadioGroup radiogrup;


    private Main_Fragment main_fragment;
    private User_Fragment user_fragment;
    private MessageFragment messageFragment;

    private PopupWindow mPopWindow;

    private List<Fragment> list;
    private long exitTime = 0;
//    公用activity，用于登录销毁
    public static MainActivity mainActivity;


    //初始化bundle
    @Override
    public void initParms(Bundle parms) {

    }

//     绑定布局

    @Override
    public int bindLayout() {
        return R.layout.activity_main;


    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        mainActivity=this;

    }

    //业务操作
    @Override
    public void doBusiness(Context mContext) {
        initPupupwindows();

        initFragment();

    }


    @Override
    public void onNetChange(int netMobile) {
        Log.d(TAG, "网络监听tag:"+netMobile);

        if (isNetConnect(netMobile)){
            dismissPopupWindow();
        }else{
            showPopupWindow();
        }

    }





    //构造页面整体框架
    private void initFragment() {
        list = new ArrayList<Fragment>();

        main_fragment = Main_Fragment.newInstance(null, null);
        user_fragment = User_Fragment.newInstance(null, null);
        messageFragment = MessageFragment.newInstance(null, null);

        list.add(main_fragment);
        list.add(messageFragment);
        list.add(user_fragment);

        ((RadioButton) radiogrup.getChildAt(0)).setChecked(true);

        radiogrup.setOnCheckedChangeListener(this);
        myViewpager.addOnPageChangeListener(this);
        myViewpager.setOffscreenPageLimit(2);
        FragmentManager fm = getSupportFragmentManager();
        Myadpater myadpater = new Myadpater(fm);
        myViewpager.setAdapter(myadpater);

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch (checkedId) {
            case R.id.radio_main:

                myViewpager.setCurrentItem(0);
                break;
            case R.id.radio_message:

                myViewpager.setCurrentItem(1);
                break;
            case R.id.radio_user:

                myViewpager.setCurrentItem(2);

                break;


            default:
                break;

        }
    }
//308100005432
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0:

                radiogrup.check(radioMain.getId());
                break;
            case 1:

                radiogrup.check(radioMessage.getId());
                break;
            case 2:

                radiogrup.check(radioUser.getId());
            default:
                break;

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class Myadpater extends FragmentPagerAdapter {

        public Myadpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return list.size();
        }


        @Override
        public Fragment getItem(int position) {

            return list.get(position);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

        Log.d(TAG, "onFragmentInteraction:"+uri);

        if (String.valueOf(uri).equals(Main_Fragment.NETPOPVISIBLE)){
            showPopupWindow();

        }else if(String.valueOf(uri).equals(Main_Fragment.NETPOPGONE)){

           dismissPopupWindow();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {

            MainUtils.showToast(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
