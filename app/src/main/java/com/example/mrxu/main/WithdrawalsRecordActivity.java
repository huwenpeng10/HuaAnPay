package com.example.mrxu.main;

import android.content.Context;
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
import com.example.mrxu.assemble_xml.WithdrawalsRecord;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class WithdrawalsRecordActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;

    @BindView(R.id.withdrawals_record_lv)
    PullToRefreshListView withdrawalsRecordLv;
    @BindView(R.id.withdrawals_record_pro)
    ProgressBar withdrawalsRecordPro;

    private ArrayList<WithrecordInfo> adapterlist;
    private EventBus eventBus;
    private int pageNumber;
    private WithdrawalsRecordAdapter withdrawalsRecordAdapter;

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
        return R.layout.activity_withdrawals_record;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titleBarText.setText("提现记录");
        leftImagemage.setOnClickListener(this);

        Circle circle =new Circle();
        circle.setColor(0xFF3798f4);
        withdrawalsRecordPro.setIndeterminateDrawable(circle);


        withdrawalsRecordLv.setOnRefreshListener(this);
        withdrawalsRecordLv.setMode(PullToRefreshBase.Mode.BOTH);
        adapterlist=new ArrayList<WithrecordInfo>();
        withdrawalsRecordAdapter=new WithdrawalsRecordAdapter();

        withdrawalsRecordLv.setAdapter(withdrawalsRecordAdapter);

    }




    @Override
    public void doBusiness(Context mContext) {

        pageNumber = 1;
        getWithdrawalsData(pageNumber,true);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.left_Imagemage :

                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);

                break;


                default:
                break;

        }
    }


    public void onEvent(TcpRequestMessage message){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                withdrawalsRecordPro.setVisibility(View.GONE);
            }
        });

        if(message.getCode()==1){

            Log.d(Get_Message, new String(message.getTcpRequestMessage()));

            TagUtils.Field011Add(this);
            //<Field065>20170518|145238|000000000001|000000000000|2000##


            try {
                final HashMap <String , String> data = new XmlParser().parse(new String(message.getTcpRequestMessage()));

                if(data.get(XmlBuilder.fieldName(39)).equals("00")){
                    String data65str = data.get(XmlBuilder.fieldName(65));

                    if(!TextUtils.isEmpty(data65str)){
                        String data65 []= data65str.split("##");

                        for (String itemdata : data65){

                            String item []= itemdata.split("\\|");
                            String itemdate = item[0];
                            String itemtime = item[1];
                            String itemamount = item[2];
                            String itemcounterFee = item[3];
                            String itemstate = item[4];

                            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(itemdate+itemtime);
                            itemdate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                            itemtime= new SimpleDateFormat("HH:mm:ss").format(date);
                            itemamount=new BigDecimal(itemamount).divide(new BigDecimal(100)).setScale(2).toPlainString();
                            itemcounterFee=new BigDecimal(itemcounterFee).divide(new BigDecimal(100)).setScale(2).toPlainString();

                            WithrecordInfo withrecordInfo = new WithrecordInfo(itemdate,itemtime,itemamount,itemcounterFee,itemstate);
                            adapterlist.add(withrecordInfo);
                        }
                    }



                }else{

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainUtils.showToast(WithdrawalsRecordActivity.this,data.get(XmlBuilder.fieldName(124)));
                        }
                    });

                }


            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        withdrawalsRecordAdapter.notifyDataSetChanged();
                    }
                });

            }


        }


    }



    private void getWithdrawalsData(int page,boolean isdown){
        if (isdown){
            withdrawalsRecordPro.setVisibility(View.VISIBLE);

        }

        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new WithdrawalsRecord(
                            WithdrawalsRecordActivity.this, "0", page)
                            .toString());
            // isDay=false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(WithdrawalsRecordActivity.this, "异常");
            e.printStackTrace();
        }

    }



    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {

        adapterlist.clear();
        withdrawalsRecordAdapter.notifyDataSetChanged();
        pageNumber = 1;
        getWithdrawalsData(pageNumber,false);
        //模拟延时三秒刷新
        withdrawalsRecordLv.postDelayed(new Runnable() {
            @Override
            public void run() {


                withdrawalsRecordLv.onRefreshComplete();//下拉刷新结束，下拉刷新头复位


            }
        }, 1000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {


        pageNumber++;
        getWithdrawalsData(pageNumber,false);
        //模拟延时三秒刷新
        withdrawalsRecordLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                withdrawalsRecordLv.onRefreshComplete();//下拉刷新结束，下拉刷新头复位

            }
        }, 1000);

    }


    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    public class WithdrawalsRecordAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return adapterlist.size();
        }

        @Override
        public WithrecordInfo getItem(int position) {
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

                convertView = getLayoutInflater().inflate(R.layout.withdrawals_recorditemlayout, parent, false);
                holdView = new HoldView(convertView);
                convertView.setTag(holdView);
            } else {
                holdView = (HoldView) convertView.getTag();
            }

            holdView.tv_type.setText("提现");
            holdView.tv_amount.setText(getItem(position).getAmount());
            holdView.tv_datetime.setText(getItem(position).getDate()+"  "+getItem(position).getTime());

            String state=getItem(position).getState();

            if(state.equals("0000")){

                holdView.item_state.setText("验证提现");
            }else if(state.equals("1000")){

                holdView.item_state.setText("待付中");
            }else if(state.equals("2000")){
                holdView.item_state.setText("已到账");
            }
            holdView.item_recordnumber.setText("手续费："+getItem(position).getCounterFee());


            return convertView;
        }

        public class HoldView {

            TextView tv_type;
            TextView tv_amount;
            TextView tv_datetime;
            TextView item_state;
            TextView item_recordnumber;

            public HoldView(View view) {
                tv_type = (TextView) view.findViewById(R.id.item_type);
                tv_amount = (TextView) view.findViewById(R.id.money_item_tv);
                tv_datetime = (TextView) view.findViewById(R.id.item_datetime_tv);
                item_state = (TextView) view.findViewById(R.id.item_state_tv);
                item_recordnumber = (TextView) view.findViewById(R.id.item_recordnumber_tv);
            }
        }
    }


    public class WithrecordInfo {
        private String date;
        private String time;
        private String amount;
        private String counterFee;
        private String state;


        public WithrecordInfo(String date, String time, String amount, String counterFee, String state) {
            this.date = date;
            this.time = time;
            this.amount = amount;
            this.counterFee = counterFee;
            this.state = state;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCounterFee() {
            return counterFee;
        }

        public void setCounterFee(String counterFee) {
            this.counterFee = counterFee;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

}
