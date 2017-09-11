package com.example.mrxu.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.AuthenticationDialog;
import com.example.mrxu.alldialog.SelectPosDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.TradeInfo.PageState;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.Welletquiry;
import com.example.mrxu.base.BaseFragment;
import com.example.mrxu.main.AuthenticationActivity;
import com.example.mrxu.main.BlueToothConnectActivity;
import com.example.mrxu.main.BusinessActivity;
import com.example.mrxu.main.MPOSConnectActivity;
import com.example.mrxu.main.PhoneReplenishingActivity;
import com.example.mrxu.main.ReplenishingActivity;
import com.example.mrxu.main.WalletActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.NetUtil;
import com.example.mrxu.mutils.PrefManager;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.shoujichongzhi_layout;


public class Main_Fragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Unbinder unbinder;

    @BindView(R.id.main_top_image)
    ImageView mainTopImage;
    @BindView(R.id.danqianshebei_text)
    TextView danqianshebeiText;

    @BindView(R.id.qiandaibao_layout)
    AutoRelativeLayout qiandaibaoLayout;
    @BindView(R.id.yinhangchaxun_layout)
    AutoRelativeLayout yinhangchaxun_layout;
    @BindView(R.id.shuakazhifu_layout)
    AutoRelativeLayout shuakazhifu_layout;
    @BindView(R.id.zhuanzhang_layout)
    AutoRelativeLayout zhuanzhangLayout;
    @BindView(R.id.shoujichongzhi_layout)
    AutoRelativeLayout shoujichongzhilayout;
    @BindView(R.id.shouyi_text)
    TextView shouyiText;
    @BindView(R.id.shouyi_text_layout)
    AutoRelativeLayout shouyiTextLayout;
    @BindView(R.id.look_qiandai_text)
    TextView lookQiandaiText;
    @BindView(R.id.qiandai_kaitong_bg)
    RelativeLayout qiandaiKaitongBg;
    @BindView(R.id.zhuanzhang_kaitong_bg)
    RelativeLayout zhuanzhangKaitongBg;
    @BindView(R.id.shouyi_kaitong_bg)
    RelativeLayout shouyiKaitongBg;
    @BindView(R.id.mainfragment_date)
    TextView mainfragmentDate;
    @BindView(R.id.fragment_mainpro)
    ProgressBar fragmentMainpro;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private boolean isfirst;
    private PrefManager prefManager;
    private int netMobile;

    //通知avtivity显示隐藏dialog
    public static String NETPOPVISIBLE = "netpopvisible";
    public static String NETPOPGONE = "netpopgone";
    /**
     * 判断是否是收益查询接口
     */
    private boolean iswellet;
    private String amount;
    public static String welletDay = "0";
    private String welletTotal;
    private String welletDate;
    private EventBus eventBus;
    private boolean isfristIn = true;


    public static Main_Fragment newInstance(String param1, String param2) {
        Main_Fragment fragment = new Main_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init() {
        prefManager = new PrefManager(getActivity());
        isfirst = prefManager.getFirstPos();
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        if (!inspectNet()) {
            onButtonPressed(Uri.parse(NETPOPVISIBLE));
        } else {
            onButtonPressed(Uri.parse(NETPOPGONE));
        }

        if (!isfirst) {
            String posName = prefManager.getPosName();
            if (posName != null && !posName.equals("")) {
                TradeInfo.setPosName(posName);
            }
        } else {

            firstDialogShow();
        }


    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_;


    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);

        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }

        danqianshebeiText.setText(TradeInfo.getPosName());

        initProgressbar();
        qiandaiKaitongBg.setOnClickListener(this);
        zhuanzhangKaitongBg.setOnClickListener(this);
        shouyiKaitongBg.setOnClickListener(this);

        danqianshebeiText.setOnClickListener(this);

        yinhangchaxun_layout.setOnClickListener(this);
        shuakazhifu_layout.setOnClickListener(this);
        shoujichongzhilayout.setOnClickListener(this);
        qiandaibaoLayout.setOnClickListener(this);
        zhuanzhangLayout.setOnClickListener(this);
        shouyiTextLayout.setOnClickListener(this);


        if(UserInfo.getState()!=0){
            //查询收益
            if (inspectNet()) {
                getWelletData();
            } else {
                MainUtils.showToast(getActivity(), "请检查网络连接");
            }
        }else{
            setKaitongbg();
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        checkAuth();

        setKaitongbg();

    }

    public void initProgressbar() {
        Circle circle = new Circle();
        circle.setColor(0xFF58A6EF);
        fragmentMainpro.setIndeterminateDrawable(circle);
    }


    /*
    * 收益
    * */
    private void getWelletData() {
        fragmentMainpro.setVisibility(View.VISIBLE);
        iswellet = true;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new Welletquiry(getActivity(), 1)
                            .toString());
        } catch (Exception e) {
            e.printStackTrace();
            tnRrror();
        }
    }


    @Override
    public void doBusiness(Context mContext) {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    /**
     * 初始化时有没有网
     */
    public boolean inspectNet() {

        this.netMobile = NetUtil.getNetWorkState(getActivity());

        return isNetConnect();

    }

    /**
     *  
     * 判断有无网络 。 
     *
     * @return true 有网, false 没有网络.
     */

    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;
        }
        return false;
    }


    public void onEvent(TcpRequestMessage message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentMainpro.setVisibility(View.GONE);
            }
        });

        if (message.getTextCode().equals("F20017")) {

            if (iswellet) {

                if (message.getCode() == 1) {
                    Log.d(Get_Message, new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(getActivity());

                    try {
                        final HashMap<String, String> data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));

                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")) {

//                                20170324|001001|000000012577|12300|000000000002

                            String datastr = data.get(XmlBuilder.fieldName(65));

                            if (!TextUtils.isEmpty(datastr)) {

                                String data65[] = datastr.split("##");
                                String welletData[] = data65[0].split("\\|");

                                amount = new BigDecimal(
                                        welletData[2])
                                        .divide(new BigDecimal(100))
                                        .setScale(2).toPlainString();

                                welletDay = new BigDecimal(
                                        welletData[4])
                                        .divide(new BigDecimal(100))
                                        .setScale(2).toPlainString();

                                welletTotal = new BigDecimal(
                                        welletData[3])
                                        .divide(new BigDecimal(100))
                                        .setScale(2).toPlainString();

                                Date date = new SimpleDateFormat("yyyyMMdd").parse(welletData[0]);

                                welletDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                                setWelletDay(welletDay);
                            } else {
                                setWelletDay("0");
                            }
                        } else {
                            setWelletDay("0");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(getActivity(), data.get(XmlBuilder.fieldName(124)));
                                }
                            });
                        }

                    } catch (SAXException e) {
                        e.printStackTrace();
                        tnRrror();
                    } catch (IOException e) {
                        e.printStackTrace();
                        tnRrror();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        tnRrror();
                    } catch (Exception e) {
                        e.printStackTrace();
                        tnRrror();
                    }

                } else if (message.getCode() == -2) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String chapshi = "服务器连接超时...";
                            MainUtils.showToast(getActivity(), chapshi
                            );
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainUtils.showToast(getActivity(), "tn查询失败，请重新登录");
                            UserInfo.state = 0;
                        }
                    });
                }

/*-------------------------------*/
            }
        }
    }


    private void tnRrror() {
        MainUtils.showToast(getActivity(), "tn查询失败，请重新登录");
        UserInfo.state = 0;
        setKaitongbg();
    }

    //用户开通状态
    private void setKaitongbg() {

        Log.d(TAG, "setKaitongbg: "+UserInfo.state);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (UserInfo.state != 0) {

                    qiandaiKaitongBg.setVisibility(View.GONE);
                    zhuanzhangKaitongBg.setVisibility(View.GONE);
                    shouyiKaitongBg.setVisibility(View.GONE);
                } else {
                    qiandaiKaitongBg.setVisibility(View.VISIBLE);
                    zhuanzhangKaitongBg.setVisibility(View.VISIBLE);
                    shouyiKaitongBg.setVisibility(View.VISIBLE);

                }
            }
        });


    }

    private void setWelletDay(final String amount) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (amount.equals("0")) {

                    shouyiText.setText("--");
                } else {
                    shouyiText.setText("+ " + amount);
                }
                mainfragmentDate.setText(welletDate);
            }
        });
    }


    private void firstDialogShow() {

        final SelectPosDialog selectPosDialog = new SelectPosDialog(getActivity());

        selectPosDialog.setTitleStr("请选择设备");
        selectPosDialog.setCanceledOnTouchOutside(false);
        selectPosDialog.setYesOnclickListener(getString(R.string.Qpos), new SelectPosDialog.onYesOnclickListener() {


            @Override
            public void onYesClick() {

                prefManager.setFirstPos(false, getString(R.string.Qpos));
                //记录选择的刷卡器
                TradeInfo.setPosName(getString(R.string.Qpos));
                danqianshebeiText.setText(TradeInfo.getPosName());
                selectPosDialog.dismiss();
            }
        });

        selectPosDialog.setNoOnclickListener(getString(R.string.Xdl), new SelectPosDialog.onNoOnclickListener() {


            @Override
            public void onNoClick() {

                prefManager.setFirstPos(false, getString(R.string.Xdl));
                TradeInfo.setPosName(getString(R.string.Xdl));
                danqianshebeiText.setText(TradeInfo.getPosName());
                selectPosDialog.dismiss();
            }
        });
        selectPosDialog.show();
    }

    private void authenticationDialogShow(String authent, String msg, final int num) {
        final AuthenticationDialog authenticationDialog = new AuthenticationDialog(getActivity());
        authenticationDialog.setTitleStr("实名认证提醒");
        authenticationDialog.setmsgStr(msg);
        authenticationDialog.setCanceledOnTouchOutside(false);
        authenticationDialog.setYesOnclickListener(authent, new AuthenticationDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {

                authenticationDialog.dismiss();
                if (num == 0 || num == 3) {
                    Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
                    startActivity(intent);
                }
            }
        });
        authenticationDialog.show();
    }


    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
//            支付
            case R.id.shuakazhifu_layout:

                //检查实名认证通过没有
                if (!checkAuth()) {
                    return;
                }

                TradeInfo.setPageState(PageState.SHUAKAZHIFU);
                startActivity(new Intent(getActivity(), ReplenishingActivity.class));
                getActivity().overridePendingTransition(R.animator.push_zhong_in, R.animator.push_zhong_out);
                break;
//            余额
            case R.id.yinhangchaxun_layout:
                //检查实名认证通过没有
                if (!checkAuth()) {
                    return;
                }

                Intent intent = null;
                if (TradeInfo.getPosName().equals(getString(R.string.Qpos))) {

                    intent = new Intent(getActivity(), BlueToothConnectActivity.class);

                } else {

                    intent = new Intent(getActivity(), MPOSConnectActivity.class);
                }
                TradeInfo.setPageState(TradeInfo.PageState.YUECHAXUN);
                startActivity(intent);
                getActivity().overridePendingTransition(R.animator.push_zhong_in, R.animator.push_zhong_out);
                break;
//            手机充值
            case shoujichongzhi_layout:
                //检查实名认证通过没有
                if (!checkAuth()) {
                    return;
                }

//                if(UserInfo.getPayPasswordState().equals("01")){

                    TradeInfo.setPageState(TradeInfo.PageState.SHOUJICHONGZHI);
                    startActivity(new Intent(getActivity(), PhoneReplenishingActivity.class));
                    getActivity().overridePendingTransition(R.animator.push_zhong_in, R.animator.push_zhong_out);

//                }else{
//                    Intent setpaypassword = new Intent(getActivity(), FristSetPayPassActivity.class);
//                    startActivity(setpaypassword);
//                    getActivity().overridePendingTransition(R.animator.push_zhong_in, R.animator.push_zhong_out);
//                }


                break;
            case R.id.qiandaibao_layout:
            case R.id.shouyi_text_layout:
                //检查实名认证通过没有
                if (!checkAuth()) {
                    return;
                }

//                if(UserInfo.getPayPasswordState().equals("01")){
                    TradeInfo.setPageState(TradeInfo.PageState.QIANDAIBAO);

                    Intent qianDaintent = new Intent(getActivity(), WalletActivity.class);
                    startActivity(qianDaintent);
                    getActivity().overridePendingTransition(R.animator.push_zhong_in, R.animator.push_zhong_out);
//                }else{
//                    Intent setpaypassword = new Intent(getActivity(), FristSetPayPassActivity.class);
//                    startActivity(setpaypassword);
//                    getActivity().overridePendingTransition(R.animator.push_zhong_in, R.animator.push_zhong_out);
//                }


                break;

            case R.id.zhuanzhang_layout:

                MainUtils.showToast(getActivity(),"维护中...");

//                //检查实名认证通过没有
//                if (!checkAuth()) {
//                    return;
//                }
//
//                TradeInfo.setPageState(PageState.ZHUANGZHAN);

                break;

            case R.id.qiandai_kaitong_bg:
            case R.id.zhuanzhang_kaitong_bg:
            case R.id.shouyi_kaitong_bg:
                //检查实名认证通过没有
                if (!checkAuth()) {
                    return;
                }
                Intent startTnIntent = new Intent(getActivity(), BusinessActivity.class);
                startActivity(startTnIntent);
                getActivity().overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);

                break;


            case R.id.danqianshebei_text:

                firstDialogShow();

                break;


            default:
                break;

        }
    }


    private Boolean checkAuth() {

        /**
         *0-未认证 1-认证成功 2-认证中 3-认证失败；；
         *
         * */
        switch (UserInfo.getAuthTipsNumber()) {
            case 0:

                authenticationDialogShow("实名认证", "请先进行实名认证", UserInfo.getAuthTipsNumber());

                return false;
            case 1:

                return true;
            case 2:

                authenticationDialogShow("好的", "实名认证审核中..", UserInfo.getAuthTipsNumber());

                return false;
            case 3:

                authenticationDialogShow("实名认证", "认证失败，请重新进行实名认证或联系客服（" + getString(R.string.services_phone) + "）", UserInfo.getAuthTipsNumber());

                return false;

        }
        return false;
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
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
