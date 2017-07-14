package com.example.mrxu.main;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.assemble_xml.GetAccountAmountInfoTn;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.itheima.pulltorefreshlib.PullToRefreshBase;
import com.itheima.pulltorefreshlib.PullToRefreshListView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 *
 * 清算账单
 *
 * @author MrXu
 * */

public class QingSuanRecordActivity extends BaseActivity {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;

    @BindView(R.id.qinsuan_list_view)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.qinsuan_record_pro)
    ProgressBar qinsuanRecordPro;

    private EventBus eventBus;

    private String KEY_EDATE = "datenum";

    private int itempage = 1;

    private ArrayList<QinsuanInfo> adapterlist;
    private QinsuanRecordAdapter qinsuanRecordAdapter;
    private String qinsuanMoney;
    private String qinsuanbalance;
    private String qinsuandate;

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
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_qingsuan_record;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        adapterlist = new ArrayList<QinsuanInfo>();
        leftImagemage.setOnClickListener(this);
        titlebartext.setText("清算记录");

        Circle circle =new Circle();
        circle.setColor(0xFFa4a4a4);
        qinsuanRecordPro.setIndeterminateDrawable(circle);


    }

    @Override
    public void doBusiness(Context mContext) {

        //设置刷新监听和模式
        pullToRefreshListView.setOnRefreshListener(mListViewOnRefreshListener2);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        qinsuanRecordAdapter = new QinsuanRecordAdapter();
        pullToRefreshListView.setAdapter(qinsuanRecordAdapter);
        itempage = 1;
        getRecordData(itempage,true);
    }


    public void getRecordData(int page,boolean isdown) {
        if (isdown){
            qinsuanRecordPro.setVisibility(View.VISIBLE);
            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new GetAccountAmountInfoTn(this, page).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new GetAccountAmountInfoTn(this, page).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onEvent(TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qinsuanRecordPro.setVisibility(View.GONE);
            }
        });
        if (message.getCode() == 1) {
            Log.d(Get_Message, new String(message.getTcpRequestMessage()));
            TagUtils.Field011Add(this);
//            20161121|001001|000000011477|000000001100
            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));

                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                        "00") && data.get(XmlBuilder.fieldName(18)).equalsIgnoreCase(
                        "2")) {
                    String data65str = data.get(XmlBuilder.fieldName(65));

                    if(!TextUtils.isEmpty(data65str)) {
                        String data65data[] = data65str.split("##");

                        for (String data65 : data65data) {

                            String[] qinsuandata = data65.split("\\|");


                            String tempdate = qinsuandata[0];


                            Date date = new SimpleDateFormat("yyyyMMdd").parse(tempdate);

                            qinsuandate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                            qinsuanbalance = new BigDecimal(qinsuandata[2])
                                    .divide(new BigDecimal(100))
                                    .setScale(2).toPlainString();
                            qinsuanMoney = new BigDecimal(qinsuandata[3])
                                    .divide(new BigDecimal(100))
                                    .setScale(2).toPlainString();


                            QinsuanInfo qinsuanInfo = new QinsuanInfo(qinsuanMoney, qinsuanbalance, qinsuandate);
                            adapterlist.add(qinsuanInfo);

                        }
                    }
                }else if(data.get(XmlBuilder.fieldName(18)).equalsIgnoreCase(
                        "2")){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MainUtils.showToast(QingSuanRecordActivity.this,
                                    data.get(XmlBuilder.fieldName(124)));
                        }
                    });
                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                runOnUiThread(new Runnable() {
                    public void run() {
                        qinsuanRecordAdapter.notifyDataSetChanged();
                    }
                });

            }
        } else if (message.getCode() == -2) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainUtils.showToast(QingSuanRecordActivity.this, "服务器连接超时..."
                    );
                }
            });
        }
    }

    @Override
    public void widgetClick(View v) {


        switch (v.getId()) {
            case R.id.left_Imagemage:

                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;


            default:
                break;

        }
    }


    private PullToRefreshBase.OnRefreshListener2<ListView> mListViewOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        /**
         * 下拉刷新回调
         * @param refreshView
         */
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            adapterlist.clear();
            //刷新一下页面
            qinsuanRecordAdapter.notifyDataSetChanged();
            itempage = 1;
            getRecordData(itempage,false);
            //模拟延时三秒刷新
            pullToRefreshListView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    pullToRefreshListView.onRefreshComplete();//下拉刷新结束，下拉刷新头复位

                }
            }, 1000);
        }

        /**
         * 上拉加载更多回调
         * @param refreshView
         */
        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            itempage++;
            getRecordData(itempage,false);
            //模拟延时三秒加载更多数据
            pullToRefreshListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshListView.onRefreshComplete();//上拉加载更多结束，上拉加载头复位
                }
            }, 1000);
        }
    };


    public class QinsuanRecordAdapter extends BaseAdapter  {


        @Override
        public int getCount() {
            return adapterlist.size();
        }

        @Override
        public QinsuanInfo getItem(int position) {
            return adapterlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            HoldView holdView = null;
            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.qinsuanlist_item, parent, false);
                holdView = new HoldView(convertView);
                convertView.setTag(holdView);
            } else {
                holdView = (HoldView) convertView.getTag();
            }

            holdView.qinsuanItemMoney.setText("+ "+getItem(position).getQinsuanqian());
            double amoutmoney = Double.parseDouble(getItem(position).getQinsuanhou())+Double.parseDouble(getItem(position).getQinsuanqian());

            BigDecimal b = new BigDecimal(amoutmoney);
            //保留2位小数
            double amoutmoneystr = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            holdView.qinsuanItem_Hou.setText(amoutmoneystr+"");
            holdView.qinsaunItem_Date.setText(getItem(position).getQinsuandate());

            return convertView;
        }

        public class HoldView {

            TextView qinsuanItemMoney;
            TextView qinsuanItem_Hou;
            TextView qinsaunItem_Date;

            public HoldView(View view) {
                qinsuanItemMoney = (TextView) view.findViewById(R.id.qinsuanItem_Money);
                qinsuanItem_Hou = (TextView) view.findViewById(R.id.qinsuanItem_Hou);
                qinsaunItem_Date = (TextView) view.findViewById(R.id.qinsaunItem_Date);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);
    }



    class QinsuanInfo{

        String Qinsuanqian;
        String Qinsuanhou;
        String Qinsuandate;


        public QinsuanInfo(String qinsuanqian, String qinsuanhou, String qinsuandate) {
            Qinsuanqian = qinsuanqian;
            Qinsuanhou = qinsuanhou;
            Qinsuandate = qinsuandate;
        }

        public String getQinsuanqian() {
            return Qinsuanqian;
        }

        public void setQinsuanqian(String qinsuanqian) {
            Qinsuanqian = qinsuanqian;
        }

        public String getQinsuanhou() {
            return Qinsuanhou;
        }

        public void setQinsuanhou(String qinsuanhou) {
            Qinsuanhou = qinsuanhou;
        }

        public String getQinsuandate() {
            return Qinsuandate;
        }

        public void setQinsuandate(String qinsuandate) {
            Qinsuandate = qinsuandate;
        }
    }
}
