package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.assemble_xml.Welletquiry;
import com.example.mrxu.assemble_xml.WelletquiryMoney;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.itheima.pulltorefreshlib.PullToRefreshBase;
import com.itheima.pulltorefreshlib.PullToRefreshBase.OnRefreshListener2;
import com.itheima.pulltorefreshlib.PullToRefreshListView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


/**
 * 钱袋收益页面
 *
 * @author MrXu
 */

public class WalletActivity extends BaseActivity implements OnRefreshListener2 {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.right_Tv)
    TextView rightTv;


    @BindView(R.id.amount_tv)
    TextView amountTv;
    @BindView(R.id.day_shouyi_tv)
    TextView dayShouyiTv;
    @BindView(R.id.gongshoiyi_tv)
    TextView gongshoiyiTv;
    @BindView(R.id.wallet_listview)
    PullToRefreshListView walletListview;
    @BindView(R.id.wallet_replenishing_tv)
    TextView walletReplenishingTv;
    @BindView(R.id.wallet_withdrawals_tv)
    TextView walletWithdrawalsTv;

    @BindView(R.id.wallet_pro1)
    ProgressBar wallet_pro1;
    @BindView(R.id.wallet_pro2)
    ProgressBar wallet_pro2;
    @BindView(R.id.wallet_pro3)
    ProgressBar wallet_pro3;
    @BindView(R.id.wallet_pro4)
    ProgressBar wallet_pro4;

    private EventBus eventBus;
    private String amount;
    private String welletDay;
    private String welletTotal;

    private String itemWelletDay;
    private String itemWelletDate;

    private List<WelletInfo> infos;
    private WalletDayAdapter walletDayAdapter;
    private int pageNum;
    private int FRESULT = 11;
    private int queryState;
    private boolean isdown;


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_wallet;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titleBarText.setText("钱袋");
        leftImagemage.setOnClickListener(this);
        walletReplenishingTv.setOnClickListener(this);
        walletWithdrawalsTv.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        walletListview.setMode(PullToRefreshBase.Mode.BOTH);
        walletListview.setOnRefreshListener(this);

        //注册网络监听的popupwindows
        initPupupwindows();
        isHaveNet(this, "请检查网络连接");
    }

    @Override
    public void onNetChange(int netMobile) {
        Log.d(TAG, "网络监听tag:" + netMobile);

        if (isNetConnect(netMobile)) {
            dismissPopupWindow();
        } else {
            showPopupWindow();
        }

    }


    @Override
    public void doBusiness(Context mContext) {
        infos = new ArrayList<WelletInfo>();
        walletDayAdapter = new WalletDayAdapter();
        walletListview.setAdapter(walletDayAdapter);

        Circle circle = new Circle();
        circle.setColor(0xFFFFFFFF);
        Circle circle1 = new Circle();
        circle1.setColor(0xFFFFFFFF);
        Circle circle2 = new Circle();
        circle2.setColor(0xFFFFFFFF);
        Circle circle3 = new Circle();
        circle3.setColor(0xFFA3A3A3);

        wallet_pro1.setIndeterminateDrawable(circle);
        wallet_pro2.setIndeterminateDrawable(circle1);
        wallet_pro3.setIndeterminateDrawable(circle2);
        wallet_pro4.setIndeterminateDrawable(circle3);

        pageNum = 1;
        isdown = true;

        getQueryMoneyData();
    }


    /**
     * 0所有隐藏 1所有显示
     */
    private void setProVisibility(int num) {

        switch (num){
            case 0 :
                wallet_pro4.setVisibility(View.GONE);
                wallet_pro1.setVisibility(View.GONE);
                wallet_pro2.setVisibility(View.GONE);
                wallet_pro3.setVisibility(View.GONE);

                break;
            case 1 :
                amountTv.setText("");
                dayShouyiTv.setText("");
                gongshoiyiTv.setText("");

                wallet_pro1.setVisibility(View.VISIBLE);
                wallet_pro2.setVisibility(View.VISIBLE);
                wallet_pro3.setVisibility(View.VISIBLE);

                break;
            case 2 :

                wallet_pro4.setVisibility(View.VISIBLE);
                break;


                default:
                break;

        }

    }


    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:
                this.finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);

                break;
            case R.id.wallet_replenishing_tv:
                Intent reintent = new Intent(this, ReplenishingActivity.class);
                startActivity(reintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;
            case R.id.wallet_withdrawals_tv:
                Intent wiintent = new Intent(this, WithdrawalsActivity.class);
                startActivityForResult(wiintent, FRESULT);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;
            case R.id.right_Tv:
                Intent qinsuanintent = new Intent(this, QingSuanRecordActivity.class);
                startActivity(qinsuanintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;

            default:
                break;

        }
    }

    private void getQueryMoneyData() {

        queryState = 1;



        if (isdown) {
            setProVisibility(1);
            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new WelletquiryMoney(this)
                                .toString());
            } catch (Exception e) {
                e.printStackTrace();
                tnRrror();
            }
        } else {
            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new WelletquiryMoney(this)
                                .toString());
            } catch (Exception e) {
                e.printStackTrace();
                tnRrror();
            }
        }

    }

    private void getWelletData() {
        queryState = 2;
        if (isdown) {
            setProVisibility(2);
            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new Welletquiry(this, pageNum)
                                .toString());
            } catch (Exception e) {
                e.printStackTrace();
                tnRrror();
            }
        } else {
            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new Welletquiry(this, pageNum)
                                .toString());
            } catch (Exception e) {
                e.printStackTrace();
                tnRrror();
            }
        }

    }

    public void onEvent(TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProVisibility(0);
            }
        });

        if (queryState == 1) {


            if (message.getTextCode().equals("F20017")) {
                if (message.getCode() == 1) {
                    Log.d(Get_Message, new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);

                    try {
                        final HashMap<String, String> data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));


                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")) {
                            String data65[] = data.get(XmlBuilder.fieldName(65)).split("##");
                            String welletData[] = data65[0].split("\\|");
                            amount = new BigDecimal(
                                        welletData[2])
                                        .divide(new BigDecimal(100))
                                        .setScale(2).toPlainString();
                            //设置总资产金额
                                TradeInfo.setUserMoney(amount);

                            welletTotal = new BigDecimal(
                                    welletData[3])
                                    .divide(new BigDecimal(100))
                                    .setScale(2).toPlainString();

                                welletDay = new BigDecimal(
                                        welletData[4])
                                        .divide(new BigDecimal(100))
                                        .setScale(2).toPlainString();


                                setWelletDay(amount,welletDay,welletTotal);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getWelletData();
                                }
                            });

                        } else {

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(WalletActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                    } finally {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                walletDayAdapter.notifyDataSetChanged();
                            }
                        });
                    }


                } else if (message.getCode() == -2) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String chapshi = "服务器连接超时...";
                            MainUtils.showToast(WalletActivity.this, chapshi
                            );
                        }
                    });
                }
            }



        } else if (queryState == 2) {


            if (message.getTextCode().equals("F20017")) {
                if (message.getCode() == 1) {
                    Log.d(Get_Message, new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);

                    try {
                        final HashMap<String, String> data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));

                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")) {

//                                20170324|001001|000000012577|12300|000000000002


                            if(!TextUtils.isEmpty(data.get(XmlBuilder.fieldName(65)))){

                                String data65[] = data.get(XmlBuilder.fieldName(65)).split("##");


                                for (int i = 0; i < data65.length; i++) {
                                    String welletData[] = data65[i].split("\\|");


                                    Date date = new SimpleDateFormat("yyyyMMdd").parse(welletData[0]);

                                    itemWelletDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                                    itemWelletDay = new BigDecimal(
                                            welletData[4])
                                            .divide(new BigDecimal(100))
                                            .setScale(2).toPlainString();

                                    WelletInfo welletInfo = new WelletInfo(itemWelletDay, itemWelletDate);

                                    infos.add(welletInfo);

                                }
                            }



                        } else {

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(WalletActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                    } finally {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                walletDayAdapter.notifyDataSetChanged();
                            }
                        });
                    }


                } else if (message.getCode() == -2) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String chapshi = "服务器连接超时...";
                            MainUtils.showToast(WalletActivity.this, chapshi
                            );
                        }
                    });
                }
            }
        }
    }

    private void setWelletDay(final String amount, final String welletDay, final String welletTotal) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                amountTv.setText(amount);
                dayShouyiTv.setText(welletDay);
                gongshoiyiTv.setText(welletTotal);
            }
        });
    }


    private void tnRrror() {


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!eventBus.isRegistered(this)) {

            eventBus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {

        infos.clear();
        walletDayAdapter.notifyDataSetChanged();
        pageNum = 1;
        isdown=false;
        getQueryMoneyData();
        walletListview.postDelayed(new Runnable() {
            @Override
            public void run() {

                walletListview.onRefreshComplete();
            }
        }, 1000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        pageNum++;
        isdown=false;
        getQueryMoneyData();
        walletListview.postDelayed(new Runnable() {
            @Override
            public void run() {

                walletListview.onRefreshComplete();
            }
        }, 1000);
    }


    public class WalletDayAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public WelletInfo getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            HoldView holdView = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.walletlist_item, parent, false);
                holdView = new HoldView(convertView);
                convertView.setTag(holdView);
            } else {
                holdView = (HoldView) convertView.getTag();
            }

            holdView.walletItem_Money_tv.setText("+ " + getItem(position).getItemWelletDay());
            holdView.walletItem_Date_tv.setText(getItem(position).getItemWelletDate());

            return convertView;
        }


        private class HoldView {

            private TextView walletItem_Money_tv;
            private TextView walletItem_Date_tv;

            public HoldView(View view) {
                walletItem_Money_tv = (TextView) view.findViewById(R.id.walletItem_Money_tv);
                walletItem_Date_tv = (TextView) view.findViewById(R.id.walletItem_Date_tv);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FRESULT) {
                pageNum=1;
                isdown=false;
                getQueryMoneyData();
            }
        }

    }

    class WelletInfo {
        private String itemWelletDay;
        private String itemWelletDate;

        public String getItemWelletDay() {
            return itemWelletDay;
        }

        public void setItemWelletDay(String itemWelletDay) {
            this.itemWelletDay = itemWelletDay;
        }

        public String getItemWelletDate() {
            return itemWelletDate;
        }

        public void setItemWelletDate(String itemWelletDate) {
            this.itemWelletDate = itemWelletDate;
        }

        public WelletInfo(String itemWelletDay, String itemWelletDate) {
            this.itemWelletDay = itemWelletDay;
            this.itemWelletDate = itemWelletDate;

        }
    }
}
