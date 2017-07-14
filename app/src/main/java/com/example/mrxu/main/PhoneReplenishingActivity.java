package com.example.mrxu.main;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.assemble_xml.PhoneReplenishType;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.PhoneCheckout;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.example.mrxu.myviews.CheckableLinearLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.phone_user_IV;

public class PhoneReplenishingActivity extends BaseActivity {

    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.phone_Number_et)
    EditText phoneNumberEt;
    @BindView(R.id.phoneReplenishing_gv)
    GridView phoneReplenishingGv;
    @BindView(phone_user_IV)
    ImageView phoneUserIV;
//    @BindView(R.id.payType_kuaijie)
//    TextView payTypeKuaijie;
//    @BindView(R.id.payType_shuaka)
//    TextView payTypeShuaka;
    @BindView(R.id.phoneReplenishing_commit_but)
    Button phoneReplenishingCommitBut;
    private EventBus eventBus;
    private String replenishingCode;

    private ArrayList<PmItemInfo> pmItemInfos;
    private PmAdapater pmAdapater;
    public static String ACTUALMONEY = "actualMoney";
    public static String SURFACEMONEY = "surfaceMoney";
    public static String REPLENISHINGCODE = "replenishingCode";
    public static String INPUTPHONENUMBER = "inputPhoneNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus= EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_phone_replenishing;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titleBarText.setText("话费充值");
        leftImagemage.setOnClickListener(this);
        phoneReplenishingCommitBut.setOnClickListener(this);
        phoneUserIV.setOnClickListener(this);
        pmItemInfos=new ArrayList<PmItemInfo>();
        pmAdapater=new PmAdapater();
        phoneReplenishingGv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//开启单选模式
        phoneReplenishingGv.setAdapter(pmAdapater);
    }

    @Override
    public void doBusiness(Context mContext) {
        getPhoneType();
    }


    private void getPhoneType(){
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new PhoneReplenishType(this).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEvent(TcpRequest.TcpRequestMessage message){


        if (message.getTextCode().equals("F20010")){


            if (message.getCode() == 1) {
                Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                TagUtils.Field011Add(this);
                try {
                    final HashMap<String, String> data = new XmlParser()
                            .parse(new String(message.getTcpRequestMessage()));
                    Log.d("state", data.get(XmlBuilder.fieldName(39)) + "");
                    if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                            "00")|| data.get(XmlBuilder.fieldName(39))
                            .equalsIgnoreCase("TT")) {
//<Field065>100002 0050 00005000|100003010000009950|100004020000019900|</Field065>

                        String data65[]=data.get(XmlBuilder.fieldName(65)).split("\\|");

                        for (String itemdata : data65){
                            replenishingCode =itemdata.substring(0,6);
                            String surfaceMoney =itemdata.substring(6,10);
                            String actualMoney =itemdata.substring(itemdata.length()-8);

                            actualMoney =new BigDecimal(actualMoney)
                                    .divide(new BigDecimal(100))+"";
                            surfaceMoney =new BigDecimal(surfaceMoney)+"";

                            PmItemInfo pmItemInfo=new PmItemInfo(replenishingCode,surfaceMoney,actualMoney);

                            if (!pmItemInfos.contains(pmItemInfo)){
                                pmItemInfos.add(pmItemInfo);
                            }
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainUtils.showToast(PhoneReplenishingActivity.this,data.get(XmlBuilder.fieldName(124)));
                            }
                        });

                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //默认选中
                            phoneReplenishingGv.setItemChecked(0,true);
                            pmAdapater.notifyDataSetChanged();

                        }
                    });
                }
            }
        }
    }









    @Override
    public void widgetClick(View v) {

        switch (v.getId()){
            case R.id.left_Imagemage :
                finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);

                break;
            case R.id.phone_user_IV :
                Uri uri = Uri.parse("content://contacts/people");
                Intent phoneintent = new Intent(Intent.ACTION_PICK, uri);
                startActivityForResult(phoneintent, 0);
                break;
            case R.id.phoneReplenishing_commit_but :


                String inputPhoneNumber=phoneNumberEt.getText().toString();

                if(TextUtils.isEmpty(inputPhoneNumber)){
                    MainUtils.showToast(this,"请输入手机号");
                    return;
                }
                if(!PhoneCheckout.isMobileNumber(inputPhoneNumber)){
                    MainUtils.showToast(this,"手机号不合法");
                    return;
                }

                int postion = phoneReplenishingGv.getCheckedItemPosition();

                Log.d(TAG, "widgetClick: "+phoneReplenishingGv.getCheckedItemPosition());

                PmItemInfo pmItemInfo = pmItemInfos.get(postion);
                String actualMoney=pmItemInfo.getActualMoney();
                String surfaceMoney=pmItemInfo.getSurfaceMoney();
                String replenishingCode=pmItemInfo.getReplenishingCode();

                Intent intent=null;
                intent=new Intent(PhoneReplenishingActivity.this,PhoneReplenishDetailActivity.class);
                intent.putExtra(ACTUALMONEY,actualMoney);
                intent.putExtra(SURFACEMONEY,surfaceMoney);
                intent.putExtra(REPLENISHINGCODE,replenishingCode);
                intent.putExtra(INPUTPHONENUMBER,inputPhoneNumber);
                startActivity(intent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;


                default:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!eventBus.isRegistered(this)){

            eventBus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);
    }




    class PmAdapater extends BaseAdapter{


        @Override
        public int getCount() {
            return pmItemInfos.size();
        }

        @Override
        public PmItemInfo getItem(int position) {
            return pmItemInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final CheckableLinearLayout checkable_layout =new CheckableLinearLayout(PhoneReplenishingActivity.this,null);
            checkable_layout.setFaceSurfaceMoney("￥"+getItem(position).getSurfaceMoney());
            checkable_layout.setFaceActualMoney("售价："+getItem(position).getActualMoney());

            return checkable_layout;
        }
    }



    class PmItemInfo{

        String replenishingCode ;
        String surfaceMoney;
        String actualMoney;


        public PmItemInfo(String replenishingCode, String surfaceMoney, String actualMoney) {
            this.replenishingCode = replenishingCode;
            this.surfaceMoney = surfaceMoney;
            this.actualMoney = actualMoney;
        }

        public String getReplenishingCode() {
            return replenishingCode;
        }

        public void setReplenishingCode(String replenishingCode) {
            this.replenishingCode = replenishingCode;
        }

        public String getSurfaceMoney() {
            return surfaceMoney;
        }

        public void setSurfaceMoney(String surfaceMoney) {
            this.surfaceMoney = surfaceMoney;
        }

        public String getActualMoney() {
            return actualMoney;
        }

        public void setActualMoney(String actualMoney) {
            this.actualMoney = actualMoney;
        }
    }




    /*
    * 跳转联系人列表的回调函数
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(data==null)
                {
                    return;
                }
                //处理返回的data,获取选择的联系人信息
                Uri uri=data.getData();
                String[] contacts=getPhoneContacts(uri);
                phoneNumberEt.setText(contacts[1]);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String[] getPhoneContacts(Uri uri){
        String[] contact=new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor=cr.query(uri,null,null,null,null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0]=cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if(phone != null){
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phone.close();
            cursor.close();
        }
        else
        {
            return null;
        }
        return contact;
    }


}
