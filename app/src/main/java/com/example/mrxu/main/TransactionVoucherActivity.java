package com.example.mrxu.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.CommonAppDialog;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.TradeInfo.PageState;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.SignatureCommit;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.CacheFile;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.NetUtil;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.name_tv;
import static com.example.mrxu.mutils.MainUtils.getViewBitmap;


/**
 * 电子小票页面
 * @author MrXu
 * */
public class TransactionVoucherActivity extends BaseActivity {

    public static String CARDNUM = "cardnum";
    public static String KEY_SERIAL_NO = "serial_no";
    public static String KEY_DATE = "textdate";
    public static String KEY_TIME = "texttime";
    public static String KEY_REF_NO = "ref";
    public static String KEY_MONER = "money";
    private boolean isSignature = false;

    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;


    @BindView(name_tv)
    TextView nameTv;
    @BindView(R.id.phoneNumber_tv)
    TextView phoneNumberTv;
    @BindView(R.id.cardNum_tv)
    TextView cardNumTv;
    @BindView(R.id.serial_number_tv)
    TextView serialNumberTv;
    @BindView(R.id.refno_tv)
    TextView refnoTv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.services_tv)
    TextView servicesTv;
    @BindView(R.id.money_tv)
    TextView moneyTv;
    @BindView(R.id.iv_sign)
    ImageView ivSign;
    @BindView(R.id.resign_tv)
    TextView resignTv;
    @BindView(R.id.transaction_but)
    Button transactionBut;
    @BindView(R.id.transactionVoucher)
    AutoRelativeLayout TransactionVoucher;
    @BindView(R.id.trantsaction_chongqian_tv)
    TextView trantsactionChongqianTv;
    @BindView(R.id.transaction_pro)
    ProgressBar transactionPro;
    private Date date;
    private EventBus eventbus;
    private Intent intent;

    /**
     * 小票显示
     **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventbus = EventBus.getDefault();
        eventbus.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!eventbus.isRegistered(this)) {
            eventbus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventbus.unregister(this);
    }

    @Override
    public void initParms(Bundle parms) {

    }


    @Override
    public int bindLayout() {
        return R.layout.activity_transaction_voucher;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);


        Circle circle=new Circle();
        circle.setColor(0xFF3798f4);
        transactionPro.setIndeterminateDrawable(circle);

        leftImagemage.setOnClickListener(this);
        titlebartext.setText("电子签名");

        resignTv.setVisibility(View.VISIBLE);
        ivSign.setVisibility(View.GONE);
        trantsactionChongqianTv.setVisibility(View.GONE);
        intent = getIntent();


    }

    @Override
    public void doBusiness(Context mContext) {
        transactionBut.setOnClickListener(this);
        resignTv.setOnClickListener(this);
        ivSign.setOnClickListener(this);
        trantsactionChongqianTv.setOnClickListener(this);

        nameTv.setText(UserInfo.getUserName());
        phoneNumberTv.setText(UserInfo.getPhoneNumber() + "");

        String cardNo = intent.getStringExtra(CARDNUM);
        if(cardNo!=null){
            String startNo = cardNo.substring(0, 4);
            String endNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
            cardNumTv.setText(startNo + "******" + endNo);
        }




        serialNumberTv.setText(intent.getStringExtra(KEY_SERIAL_NO));
        refnoTv.setText(intent.getStringExtra(KEY_REF_NO));
        try {
            date = new SimpleDateFormat("yyyyMMddHHmmss")
                    .parse(intent.getStringExtra(KEY_DATE)
                            + intent.getStringExtra(KEY_TIME));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timeTv.setText(DateFormat.format("yyyy-MM-dd\tHH:mm:ss", date));
        servicesTv.setText(getString(R.string.services_phone));
        moneyTv.setText(intent.getStringExtra(KEY_MONER)+"元");

    }


    public static final int REQUEST_SIGN = 22;


    public void sendTransaction() {
        String path = Environment.getExternalStorageDirectory() + "/asd.png";
        TransactionVoucherActivity.savePic(getViewBitmap(TransactionVoucher), path);

        try {
            transactionPro.setVisibility(View.VISIBLE);
            new TcpRequest(Construction.HOST,
                    Construction.PORT)
                    .request(new SignatureCommit(this,
                            getViewBitmap(TransactionVoucher),
                            DateFormat.format("yyyyMMdd", date)
                                    .toString()
                                    + refnoTv.getText().toString())
                            .toString());
        } catch (Exception e) {
            runErrorFunction(TradeInfo.getPagetates(), null);
            e.printStackTrace();
        }
    }


    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:
                sigNout();
                break;
            case R.id.resign_tv:
                startActivityForResult(new Intent(this, SignatureActivity.class),
                        REQUEST_SIGN);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.transaction_but:


                if(NetUtil.getNetWorkState(this) !=-1){

                    if(isSignature){
                        sendTransaction();
                    }else{
                        MainUtils.showToast(this,"请签名后提交");
                    }

                }else{
                    MainUtils.showToast(this,"请检查网络连接...");
                }
                break;
            case R.id.trantsaction_chongqian_tv:

                startActivityForResult(new Intent(this, SignatureActivity.class),
                        REQUEST_SIGN);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;

            default:
                break;

        }
    }


    public void onEvent(TcpRequestMessage message) {

        if(message.getTextCode().equals("F00004")){


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    transactionPro.setVisibility(View.GONE);
                }
            });

            if (message.getCode() == 1) {
                TagUtils.Field011Add(this);
                Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                try {
                    final HashMap<String, String> data = new XmlParser()
                            .parse(new String(message.getTcpRequestMessage()));
                    if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {
                        runFunction(TradeInfo.getPagetates(), data);
                    } else {
                        runErrorFunction(TradeInfo.getPagetates(), data);
                    }
                } catch (SAXException e) {
                    e.printStackTrace();
                    runErrorFunction(TradeInfo.getPagetates(), null);
                } catch (IOException e) {
                    e.printStackTrace();
                    runErrorFunction(TradeInfo.getPagetates(), null);
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    runErrorFunction(TradeInfo.getPagetates(), null);
                }
            } else {
                if (message.getCode() == -2) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MainUtils.showToast(getApplicationContext(),
                                    "网络连接超时...");
                            runErrorFunction(TradeInfo.getPagetates(), null);
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_SIGN:
                        ImageLoader.getInstance().displayImage(
                                Uri.fromFile(new File(getFilesDir(), CacheFile.SIGN))
                                        .toString(), ivSign);
                        resignTv.setVisibility(View.GONE);
                        ivSign.setVisibility(View.VISIBLE);
                        trantsactionChongqianTv.setVisibility(View.VISIBLE);
                        isSignature=true;
                        break;
                    default:
                        break;
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 获取指定Activity的截屏，保存到png文件
    private static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }


    // 保存到sdcard
    private static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runErrorFunction(final PageState function,
                                  final HashMap<String, String> data) {

        runOnUiThread(new Runnable() {
            public void run() {

                switch (function) {


                    case SHUAKAZHIFU:
                        final CommonAppDialog shuakazhifuAppDialog = new CommonAppDialog(TransactionVoucherActivity.this);
                        shuakazhifuAppDialog.setTitleStr("消息提示");
                        shuakazhifuAppDialog.setCanceledOnTouchOutside(false);
                        if (data != null) {
                            shuakazhifuAppDialog.setmsgStr(data.get(XmlBuilder.fieldName(124)) + "\n" + "稍后提交可在《交易记录》中完成签名");
                        } else {
                            shuakazhifuAppDialog.setmsgStr("上送电子签名失败！" + "\n" + "稍后提交可在《交易记录》中完成签名");
                        }
                        shuakazhifuAppDialog.setNoOnclickListener("稍后提交", new CommonAppDialog.onNoOnclickListener() {


                            @Override
                            public void onNoClick() {
                                startActivity(new Intent(
                                        TransactionVoucherActivity.this,
                                        ReplenishingActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                overridePendingTransition(
                                        R.animator.push_right_out,
                                        R.animator.push_right_in);

                                shuakazhifuAppDialog.dismiss();

                            }
                        });
                        shuakazhifuAppDialog.setYesOnclickListener("重新提交", new CommonAppDialog.onYesOnclickListener() {


                            @Override
                            public void onYesClick() {
                                shuakazhifuAppDialog.dismiss();
                                sendTransaction();
                            }
                        });

                        shuakazhifuAppDialog.show();
                        break;


                    case RECORD:


                        final CommonAppDialog recordAppDialog = new CommonAppDialog(TransactionVoucherActivity.this);
                        recordAppDialog.setTitleStr("消息提示");
                        recordAppDialog.setCanceledOnTouchOutside(false);
                        if (data != null) {
                            recordAppDialog.setmsgStr(data.get(XmlBuilder.fieldName(124)) + "\n" + "稍后提交可在《交易记录》中完成签名");
                        } else {
                            recordAppDialog.setmsgStr("上送电子签名失败！" + "\n" + "稍后提交可在《交易记录》中完成签名");
                        }
                        recordAppDialog.setNoOnclickListener("稍后提交", new CommonAppDialog.onNoOnclickListener() {


                            @Override
                            public void onNoClick() {

                                recordAppDialog.dismiss();
                                TransactionVoucherActivity.this.finish();
                                overridePendingTransition(
                                        R.animator.push_right_out,
                                        R.animator.push_right_in);

                            }
                        });
                        recordAppDialog.setYesOnclickListener("重新提交", new CommonAppDialog.onYesOnclickListener() {


                            @Override
                            public void onYesClick() {
                                recordAppDialog.dismiss();
                                sendTransaction();
                            }
                        });
                        recordAppDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void runFunction(final PageState function,
                             HashMap<String, String> data) {
        runOnUiThread(new Runnable() {
            public void run() {
                switch (function) {
                    case SHUAKAZHIFU:

                        final OkAppDialog sokAppDialog = new OkAppDialog(TransactionVoucherActivity.this);
                        sokAppDialog.setTitleStr("消息提示");
                        sokAppDialog.setmsgStr("上送电子签名成功");
                        sokAppDialog.setCanceledOnTouchOutside(false);
                        sokAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {


                            @Override
                            public void onYesClick() {
                                sokAppDialog.dismiss();
                                startActivity(new Intent(
                                        TransactionVoucherActivity.this,
                                        ReplenishingActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                overridePendingTransition(
                                        R.animator.push_left_in,
                                        R.animator.push_left_out);
                            }
                        });
                        sokAppDialog.show();


                        break;
                    case RECORD:

                        final OkAppDialog rokAppDialog = new OkAppDialog(TransactionVoucherActivity.this);
                        rokAppDialog.setTitleStr("消息提示");
                        rokAppDialog.setmsgStr("上传签名成功");
                        rokAppDialog.setCanceledOnTouchOutside(false);
                        rokAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {


                            @Override
                            public void onYesClick() {
                                rokAppDialog.dismiss();
                                setResult(RESULT_OK);
                                TransactionVoucherActivity.this.finish();
                                overridePendingTransition(
                                        R.animator.push_right_out,
                                        R.animator.push_right_in);
                            }
                        });
                        rokAppDialog.show();


                        break;
                    default:
                        break;
                }
            }
        });

    }

    //直接退出
    private void sigNout(){

        switch (TradeInfo.getPagetates()) {


            case SHUAKAZHIFU:

                final CommonAppDialog skeyDownDialog = new CommonAppDialog(TransactionVoucherActivity.this);
                skeyDownDialog.setTitleStr("消息提示");
                skeyDownDialog.setCanceledOnTouchOutside(false);
                skeyDownDialog.setmsgStr("现在离开可在《交易记录》中完成签名");
                skeyDownDialog.setNoOnclickListener("稍后提交", new CommonAppDialog.onNoOnclickListener() {


                    @Override
                    public void onNoClick() {
                        startActivity(new Intent(
                                TransactionVoucherActivity.this,
                                ReplenishingActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(
                                R.animator.push_right_out,
                                R.animator.push_right_in);
                        skeyDownDialog.dismiss();
                        finish();

                    }
                });
                skeyDownDialog.setYesOnclickListener("取消", new CommonAppDialog.onYesOnclickListener() {


                    @Override
                    public void onYesClick() {
                        skeyDownDialog.dismiss();
                    }
                });
                skeyDownDialog.show();

            case RECORD:


                final CommonAppDialog rkeyDownDialog = new CommonAppDialog(TransactionVoucherActivity.this);
                rkeyDownDialog.setTitleStr("消息提示");
                rkeyDownDialog.setCanceledOnTouchOutside(false);
                rkeyDownDialog.setmsgStr("现在离开可在《交易记录》中完成签名");
                rkeyDownDialog.setNoOnclickListener("稍后提交", new CommonAppDialog.onNoOnclickListener() {


                    @Override
                    public void onNoClick() {

                        rkeyDownDialog.dismiss();
                        setResult(RESULT_OK);
                        TransactionVoucherActivity.this.finish();

                    }
                });
                rkeyDownDialog.setYesOnclickListener("取消", new CommonAppDialog.onYesOnclickListener() {


                    @Override
                    public void onYesClick() {
                        rkeyDownDialog.dismiss();
                    }
                });
                rkeyDownDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        sigNout();

        return true;
    }
}
