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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.TradeInfo.PageState;
import com.example.mrxu.assemble_xml.TradingRecord;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.Function;
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



public class RecordActivity extends BaseActivity {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;
    @BindView(R.id.record_pro)
    ProgressBar recordPro;

    @BindView(R.id.pull_to_refresh_list_view)
    PullToRefreshListView pullToRefreshListView;

    private EventBus eventBus;
    private String KEY_REF_DATA = "ref_data";
    private String KEY_SERIAL_DATA = "serial_data";
    private String KEY_CARD_DATA = "card_data";
    private String KEY_REDATE = "date";
    private String KEY_RETIME = "time";
    private String KEY_TYPE = "type_data";
    private String KEY_AMOUNT = "amount_data";
    private String KEY_STATE = "state";

    private String KEY_EDATE = "datenum";
    private String KEY_ETIME = "timenum";

    private int itempage = 1;

    private ArrayList<HashMap<String, String>> adapterlist;
    private RecordAdapter recordAdapter;
    private String KEY_STATE_NUMBER;
    private int RESULTCODE = 10;

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
        return R.layout.activity_record;
    }

    @Override
    public void initView(View view) {


        ButterKnife.bind(this);

        Circle circle=new Circle();
        circle.setColor(0xFF3798f4);
        recordPro.setIndeterminateDrawable(circle);

        adapterlist = new ArrayList<HashMap<String, String>>();
        leftImagemage.setOnClickListener(this);
        titlebartext.setText("账单记录");

    }

    @Override
    public void doBusiness(Context mContext) {

        //设置刷新监听和模式
        pullToRefreshListView.setOnRefreshListener(mListViewOnRefreshListener2);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        recordAdapter = new RecordAdapter();
        pullToRefreshListView.setAdapter(recordAdapter);
        itempage = 1;
        getRecordData(itempage,true);
    }


    public void getRecordData(int page,boolean isdown) {
        if(isdown){
            recordPro.setVisibility(View.VISIBLE);
        }
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new TradingRecord(this, page).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onEvent(TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recordPro.setVisibility(View.GONE);
            }
        });
        if (message.getCode() == 1) {
            Log.d(Get_Message, new String(message.getTcpRequestMessage()));
            TagUtils.Field011Add(this);

            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));

                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                        "00")) {

                    for (String data65 : data.get(XmlBuilder.fieldName(65)).split("##")) {


                        if (!TextUtils.isEmpty(data65)) {

                            HashMap<String, String> item = new HashMap<String, String>();

                            String strdata[] = data65.split("\\|");

                            item.put(KEY_EDATE,strdata[0]);
                            item.put(KEY_ETIME,strdata[1]);
                            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(strdata[0]
                                    + strdata[1]);
                            item.put(KEY_RETIME,
                                    new SimpleDateFormat("HH:mm:ss").format(date));
                            item.put(KEY_REDATE,
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd").format(date));
                            item.put(KEY_REF_DATA, strdata[4]);
                            item.put(KEY_SERIAL_DATA, strdata[5]);
                            item.put(KEY_CARD_DATA, strdata[6].trim());


                            item.put(KEY_TYPE, Function.getInstance()
                                    .queryNameByCode(strdata[2]));
                            item.put(KEY_AMOUNT, new BigDecimal(strdata[3])
                                    .divide(new BigDecimal(100))
                                    .setScale(2).toPlainString());
                            String result = strdata[7];

                            if (strdata[2].equalsIgnoreCase("F10002")) {
                                switch (result.charAt(0)) {
                                    case '1':

                                        String resultData = result.substring(1,
                                                result.length());
                                        if (resultData.equals("00")) {
                                            item.put(KEY_STATE, "请重新签名");
                                            item.put(KEY_STATE_NUMBER, "0");
                                        } else if (resultData.equals("11")) {
                                            item.put(KEY_STATE, "交易成功");
                                            item.put(KEY_STATE_NUMBER, "1");
                                        } else if (resultData.equals("10")) {
                                            item.put(KEY_STATE, "交易受理中");
                                            item.put(KEY_STATE_NUMBER, "2");
                                        } else if (resultData.equals("12")) {
                                            item.put(KEY_STATE, "审核失败请重新签名");
                                            item.put(KEY_STATE_NUMBER, "3");
                                        }
                                        break;
                                    case '2':
                                        item.put(KEY_STATE, "交易失败");
                                        item.put(KEY_STATE_NUMBER, "4");
                                        break;
                                    default:
                                        item.put(KEY_STATE, "交易受理中");
                                        item.put(KEY_STATE_NUMBER, "2");
                                        break;
                                }
                            } else {
                                switch (result.charAt(0)) {
                                    case '1':
                                        item.put(KEY_STATE, "交易成功");
                                        item.put(KEY_STATE_NUMBER, "1");
                                        break;
                                    case '2':
                                        item.put(KEY_STATE, "交易失败");
                                        item.put(KEY_STATE_NUMBER, "4");
                                        break;
                                    default:
                                        item.put(KEY_STATE, "交易受理中");
                                        item.put(KEY_STATE_NUMBER, "2");
                                        break;
                                }
                            }

                            adapterlist.add(item);
                        }
                    }
                }else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainUtils.showToast(RecordActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                        recordAdapter.notifyDataSetChanged();
                    }
                });

            }
        } else if (message.getCode() == -2) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainUtils.showToast(RecordActivity.this, "服务器连接超时..."
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
            recordAdapter.notifyDataSetChanged();
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


    public class RecordAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return adapterlist.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return adapterlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            HoldView holdView = null;

                convertView = getLayoutInflater().inflate(R.layout.recorditemlayout, parent, false);
                holdView = new HoldView(convertView);


            String tapy = String.valueOf(getItem(position).get(KEY_TYPE));
            Log.d(TAG, "getView: " + tapy);

            if (tapy.equals("刷卡收款")) {
                holdView.tv_type.setCompoundDrawablesWithIntrinsicBounds(
                        getResources().getDrawable(
                                R.mipmap.new_xiaoxi_shoukuan_icon), null, null,
                        null);
            } else if (tapy.equals("话费充值")) {
                holdView.tv_type.setCompoundDrawablesWithIntrinsicBounds(

                        getResources().getDrawable(
                                R.mipmap.new_xiaoxi_huafei_icon), null, null,
                        null);

            } else if (tapy.equals("扫码收款")) {
                holdView.tv_type.setCompoundDrawablesWithIntrinsicBounds(

                        getResources().getDrawable(
                                R.mipmap.new_xiaoxi_erweima_icon), null, null, null);

            } else if (tapy.equals("账户提现")) {
                holdView.tv_type.setCompoundDrawablesWithIntrinsicBounds(

                        getResources().getDrawable(
                                R.mipmap.new_xiaoxi_tixian_icon), null, null, null);


            }
            holdView.tv_type.setText(tapy);

            /**
             *
             * 适配器不显示，
             *
             * */

            final String amount = String.valueOf(getItem(position).get(KEY_AMOUNT));
            String date= String.valueOf(getItem(position).get(KEY_REDATE));
            String time= String.valueOf(getItem(position).get(KEY_RETIME));
            String state = String.valueOf(getItem(position).get(KEY_STATE));
            final String recordnumber = String.valueOf(getItem(position).get(KEY_REF_DATA));
            String statenumber = String.valueOf(getItem(position).get(KEY_STATE_NUMBER));

            holdView.tv_amount.setText(amount);
            holdView.tv_datetime.setText(date+" "+time);
            holdView.item_recordnumber.setText(recordnumber);
            holdView.item_state.setText(state);
            switch (statenumber.charAt(0)){

                case '0':
                case '3':
                    //需要签名
                    holdView.item_state.setTextColor(getResources().getColor(R.color.state_item_color0));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


//
//                            public static String CARDNUM = "cardnum";
//                            public static String KEY_SERIAL_NO = "serial_no";
//                            public static String KEY_DATE = "textdate";
//                            public static String KEY_TIME = "texttime";
//                            public static String KEY_REF_NO = "ref";
//                            public static String KEY_MONER = "money";



                            String cardnumber = String.valueOf(getItem(position).get(KEY_CARD_DATA));
                            String edate = String.valueOf(getItem(position).get(KEY_EDATE));
                            String etime = String.valueOf(getItem(position).get(KEY_ETIME));
                            String seralno = String.valueOf(getItem(position).get(KEY_SERIAL_DATA));

                            Log.d(TAG, "onClick: "+cardnumber+"--"+edate+"--"+etime+"--"+amount+"--"+recordnumber+"--"+seralno+"--");

                            Intent intent=new Intent(RecordActivity.this , TransactionVoucherActivity.class);
                            intent.putExtra(TransactionVoucherActivity.CARDNUM,cardnumber);
                            intent.putExtra(TransactionVoucherActivity.KEY_SERIAL_NO,seralno);
                            intent.putExtra(TransactionVoucherActivity.KEY_DATE,edate);
                            intent.putExtra(TransactionVoucherActivity.KEY_TIME,etime);
                            intent.putExtra(TransactionVoucherActivity.KEY_REF_NO,recordnumber);
                            intent.putExtra(TransactionVoucherActivity.KEY_MONER,amount);

                            TradeInfo.setPageState(PageState.RECORD);
                            RecordActivity.this.startActivityForResult(intent,RESULTCODE);
                        }
                    });

                    break;
                case '1':
                    holdView.item_state.setTextColor(getResources().getColor(R.color.state_item_color1));
                    break;
                case '2':
                    holdView.item_state.setTextColor(getResources().getColor(R.color.state_item_color2));
                    break;
                case '4':
                    holdView.item_state.setTextColor(getResources().getColor(R.color.state_item_color4));
                    break;
            }



            

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


    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==RESULT_OK){

            if (requestCode==RESULTCODE){

                adapterlist.clear();
                //刷新一下页面
                recordAdapter.notifyDataSetChanged();
                itempage=1;
                getRecordData(itempage,true);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
