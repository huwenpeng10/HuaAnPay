package com.example.mrxu.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.base.BaseFragment;
import com.example.mrxu.main.AuthenticationActivity;
import com.example.mrxu.main.BusinessActivity;
import com.example.mrxu.main.RecordActivity;
import com.example.mrxu.main.SetingsActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.mrxu.allinfo.UserInfo.authTips;


public class User_Fragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.user_icon_tv)
    TextView userIconTv;
    @BindView(R.id.user_transaction_recordtext)
    TextView userTransactionRecordtext;
    @BindView(R.id.user_transaction_record_layout)
    AutoRelativeLayout userTransactionRecordLayout;
    @BindView(R.id.user_nametext)
    TextView userNametext;
    @BindView(R.id.user_account_id)
    TextView userAccountId;
    @BindView(R.id.user_nametext_layout)
    AutoRelativeLayout userNametextLayout;
    @BindView(R.id.user_business_layout)
    AutoRelativeLayout userBusinessLayout;
    @BindView(R.id.user_setlayout)
    AutoRelativeLayout userSetlayout;


    private Unbinder unbinder;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Boolean isAuthTip;

    public User_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static User_Fragment newInstance(String param1, String param2) {
        User_Fragment fragment = new User_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void init() {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_user_;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        userTransactionRecordLayout.setOnClickListener(this);
        userBusinessLayout.setOnClickListener(this);
        userNametextLayout.setOnClickListener(this);
        userSetlayout.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (authTips.equals("认证成功")) {
            isAuthTip = true;
            Drawable myImage = this.getResources().getDrawable(R.mipmap.new_zhanghuxinxi_renzheng_img);
            myImage.setBounds(0, 0, myImage.getMinimumWidth(), myImage.getMinimumHeight());
            userIconTv.setCompoundDrawables(null, null, null, myImage);
            String userName=UserInfo.getUserName();
            String userPhoneNumber=UserInfo.getPhoneNumber()+"";
            initUserInfo(userName,userPhoneNumber);
        } else {

            isAuthTip = false;
            Drawable myImage = this.getResources().getDrawable(R.mipmap.new_zhanghuxinxi_weirenzheng_img);
            myImage.setBounds(0, 0, myImage.getMinimumWidth(), myImage.getMinimumHeight());
            userIconTv.setCompoundDrawables(null, null, null, myImage);
            initUserInfo("未认证","");
        }


    }

    private void initUserInfo(String userName, String userPhoneNumber) {
        userNametext.setText(userName);
        userAccountId.setText(userPhoneNumber);
    }


    @Override
    public void doBusiness(Context mContext) {

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void widgetClick(View v) {

            switch (v.getId()) {
                case R.id.user_transaction_record_layout:
                    if(isAuthTip){
                        Intent recordIntent=new Intent(getActivity(), RecordActivity.class);
                        startActivity(recordIntent);
                        getActivity().overridePendingTransition(R.animator.push_left_in,
                                R.animator.push_left_out);
                    }else{
                        startAuthTipActivity();
                    }

                    break;
                case R.id.user_nametext_layout:
                    if(!isAuthTip){
                        startAuthTipActivity();
                    }
                    break;
                case R.id.user_business_layout://业务开通


                    if(isAuthTip){

                        Intent tnintent =new Intent(getActivity(),BusinessActivity.class);
                        startActivity(tnintent);
                        getActivity().overridePendingTransition(R.animator.push_left_in,
                                R.animator.push_left_out);
                    }else{
                        startAuthTipActivity();
                    }

                    break;
                case R.id.user_setlayout:
                    Intent setintent =new Intent(getActivity(),SetingsActivity.class);
                        startActivity(setintent);
                        getActivity().overridePendingTransition(R.animator.push_left_in,
                                R.animator.push_left_out);


                    break;
            }


    }

    private void startAuthTipActivity(){
        Intent recordIntent=new Intent(getActivity(), AuthenticationActivity.class);
        startActivity(recordIntent);
        getActivity().overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
