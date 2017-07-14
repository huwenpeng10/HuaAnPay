package com.example.mrxu.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.BluetoothAlreadyDialog;
import com.example.mrxu.alldialog.BluetoothAvailableDialog;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.github.ybq.android.spinkit.style.Circle;
import com.icardpay.zxbbluetooth.api.ZxbListener;
import com.icardpay.zxbbluetooth.api.ZxbManager;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.merxu.mypractice.R.id.already_matched_image_layout;

;

public class BlueToothConnectActivity extends BaseActivity implements ZxbListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;

    @BindView(R.id.bluetooth_switchview)
    SwitchButton bluetoothSwitchview;
    @BindView(R.id.already_matched_text)
    TextView alreadyMatchedText;
    @BindView(R.id.available_text)
    TextView availableText;
    @BindView(R.id.bluetooth_pro)
    ProgressBar bluetoothPro;
    @BindView(R.id.already_matched_list)
    AutoLinearLayout alreadyMatchedList;    //已配对列表
    @BindView(R.id.available_list)
    AutoLinearLayout availableList;         //未配对列表
    @BindView(R.id.search_but)
    Button searchbut;
    @BindView(R.id.shebei_layout)
    AutoLinearLayout shebeilayout;
    //    没有内容时显示的布局
    @BindView(already_matched_image_layout)
    AutoRelativeLayout alreadymatchedimage_layout;
    @BindView(R.id.available_matched_image_layout)
    AutoRelativeLayout availablematchedimagelayout;
    @BindView(R.id.already_matched_listScroll)
    ScrollView alreadyMatchedListScroll;
    @BindView(R.id.available_listScroll)
    ScrollView availableListScroll;

    private ZxbManager manager;
    //金额
    private Double Money;
    // 搜索到的设备列表
    private ArrayList<BluetoothDevice> mFoundList = new ArrayList<BluetoothDevice>();
    // 已配对设备列表
    private ArrayList<BluetoothDevice> mPairedList = new ArrayList<BluetoothDevice>();
    //已配对dialog
    private BluetoothAlreadyDialog bluetoothAlreadyDialog;
    //未配对dialog
    private BluetoothAvailableDialog bluetoothAvailableDialog;


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_blue_tooth_connect;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        titlebartext.setText("蓝牙连接");
        leftImagemage.setOnClickListener(this);
        searchbut.setOnClickListener(this);
        bluetoothSwitchview.setOnCheckedChangeListener(this);

        //初始化蓝牙搜索的加载条
        Circle chasingDots = new Circle();
        chasingDots.setColor(0xFF74b3fc);
        bluetoothPro.setIndeterminateDrawable(chasingDots);
        //注册网络监听的popupwindows
        initPupupwindows();


    }

    @Override
    public void doBusiness(Context mContext) {
        manager = ZxbManager.getInstance(this);
        manager.setZxbListener(this);
        manager.setDebug(true);
        bluetoothSwitchview.setOnCheckedChangeListener(null);
        if (manager.isEnabled()) {
            bluetoothSwitchview.setChecked(true);
            shebeilayout.setVisibility(View.VISIBLE);
            getBondedDevices();
        } else {
            bluetoothSwitchview.setChecked(false);
            shebeilayout.setVisibility(View.GONE);
            MainUtils.showToast(getApplicationContext(), "请打开蓝牙");
        }

        bluetoothSwitchview.setOnCheckedChangeListener(this);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        // 重新设置掌芯宝的监听,防止多个连续Activity使用掌芯宝时造成找不到回调的错误
        manager.setZxbListener(this);
        // 设置蓝牙状态0
        if (manager.isEnabled())
            getBondedDevices();// 刷新已绑定的设备列表
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放掌芯宝-蓝牙SDK,APP需在最后一个Activity销毁时释放SDK
        manager.destroy();
        manager.disconnect();
    }

    /**
     * 获取已绑定的设备列表,并添加到View中
     */
    private void getBondedDevices() {
        String bluetoothName;
        for (BluetoothDevice device : manager.getBondedDevices()) {
            if (!mPairedList.contains(device)) {
                bluetoothName = device.getName();
                if ((!(bluetoothName.length() < 4))
                        && bluetoothName.substring(0, 4).equalsIgnoreCase("upos")) {

                    mPairedList.add(device);
                    addAlready_matched_list(device);
                }
            }

        }

        Log.d(TAG, "getBondedDevices: " + mPairedList.isEmpty());
        if (mPairedList.isEmpty()) {
            alreadyMatchedListScroll.setVisibility(View.GONE);
            alreadymatchedimage_layout.setVisibility(View.VISIBLE);
        } else {
            alreadyMatchedListScroll.setVisibility(View.VISIBLE);
            alreadymatchedimage_layout.setVisibility(View.GONE);
        }
    }


    /**
     * 添加一个蓝牙设备（view）加入一配对列表
     */
    private void addAlready_matched_list(final BluetoothDevice bluetoothDevice) {

        View view = getLayoutInflater().inflate(R.layout.layout_bluedevice,
                null);
        TextView textView = (TextView) view.findViewById(R.id.tv_device_name);
        textView.setText(bluetoothDevice.getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 断开之前连接的设备，保证用户能看见当前连接的设备
                if (manager.isEnabled()) {
                    initBluetoothAlreadyDialog(bluetoothDevice);
                } else {
                    MainUtils.showToast(getApplicationContext(), "请打开蓝牙");
                }
            }
        });

        view.setTag(bluetoothDevice);
        String bluetoothName = bluetoothDevice.getName();
        if ((!(bluetoothName.length() < 4))
                && bluetoothName.substring(0, 4).equalsIgnoreCase("upos")) {
            alreadyMatchedList.addView(view);
        }

    }


    /**
     * 添加一个蓝牙设备（view）加入搜索到的列表
     */
    private void addAvailable_list(final BluetoothDevice bluetoothDevice) {
        View view = getLayoutInflater().inflate(R.layout.layout_bluedevice,
                null);
        final TextView nameTv = (TextView) view
                .findViewById(R.id.tv_device_name);
        nameTv.setText(bluetoothDevice.getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.isEnabled()) {
                    initBluetoothAvailableDialog(bluetoothDevice);
                } else {
                    MainUtils.showToast(getApplicationContext(), "请打开蓝牙");
                }
            }
        });

        view.setTag(bluetoothDevice);

        availableList.addView(view);
    }

    /**
     * 移除一个蓝牙设备（view）搜索到的列表
     */
    private void removeAvailable_list(BluetoothDevice bluetoothDevice) {
        availableList.removeView(availableList
                .findViewWithTag(bluetoothDevice));
    }

    /**
     * 移除一个蓝牙设备（view）配对列表
     */
    private void removeAlready_matched_list(BluetoothDevice bluetoothDevice) {
        alreadyMatchedList.removeView(alreadyMatchedList
                .findViewWithTag(bluetoothDevice));
    }

    /**
     * 清除所有搜索到的设备的View
     */
    private void removeAllFoundDeviceViews() {
        mFoundList.clear();
        availableList.removeAllViews();
    }


    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:

                this.finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
            case R.id.search_but:
                if (manager.isEnabled()) {
                    removeAllFoundDeviceViews();
                    manager.startDiscovery();
                    bluetoothPro.setVisibility(View.VISIBLE);
                    availableList.setVisibility(View.VISIBLE);
                    availablematchedimagelayout.setVisibility(View.GONE);
                    availableListScroll.setVisibility(View.VISIBLE);
                } else {
                    MainUtils.showToast(getApplicationContext(), "请打开蓝牙");
                }

                break;

            default:
                break;

        }
    }


    //初始化已配对dialog
    private void initBluetoothAlreadyDialog(final BluetoothDevice bluetoothDevice) {

        bluetoothAlreadyDialog = new BluetoothAlreadyDialog(this);
        bluetoothAlreadyDialog.setTitleStr("连接设备");
        bluetoothAlreadyDialog.setmsgStr(bluetoothDevice.getName());
        bluetoothAlreadyDialog.setYesOnclickListener("连接", new BluetoothAlreadyDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {

                if(!isHaveNet(BlueToothConnectActivity.this,"请检查网络连接")){
                    return;
                }
                manager.cancelDiscovery();
                removeAvailable_list(bluetoothDevice);
                startDeviceView(bluetoothDevice);
                bluetoothAlreadyDialog.dismiss();
            }
        });
        bluetoothAlreadyDialog.setWellOnclickListener("删除设备", new BluetoothAlreadyDialog.onWellOnclickListener() {


            @Override
            public void onWellClick() {

                manager.removeBond(bluetoothDevice);

                bluetoothAlreadyDialog.dismiss();
            }
        });
        bluetoothAlreadyDialog.setNoOnclickListener("关闭", new BluetoothAlreadyDialog.onNoOnclickListener() {

            @Override
            public void onNoClick() {

                bluetoothAlreadyDialog.dismiss();
            }
        });
        bluetoothAlreadyDialog.show();
    }

    //初始化搜索dialog
    private void initBluetoothAvailableDialog(final BluetoothDevice bluetoothDevice) {

        bluetoothAvailableDialog = new BluetoothAvailableDialog(this);
        bluetoothAvailableDialog.setTitleStr("连接设备");
        bluetoothAvailableDialog.setmsgStr(bluetoothDevice.getName());
        bluetoothAvailableDialog.setYesOnclickListener("连接", new BluetoothAvailableDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                if(!isHaveNet(BlueToothConnectActivity.this,"请检查网络连接")){
                    return;
                }
                manager.cancelDiscovery();
                removeAvailable_list(bluetoothDevice);
                startDeviceView(bluetoothDevice);
                bluetoothAvailableDialog.dismiss();
            }
        });
        bluetoothAvailableDialog.setNoOnclickListener("关闭", new BluetoothAvailableDialog.onNoOnclickListener() {

            @Override
            public void onNoClick() {

                bluetoothAvailableDialog.dismiss();
            }
        });
        bluetoothAvailableDialog.show();
    }


    private void startDeviceView(BluetoothDevice bluetoothDevice) {

        Intent intent = new Intent(this, QPOSAutographActivity.class);

        intent.putExtra("device", bluetoothDevice);
        if (!mPairedList.contains(bluetoothDevice)) {
            intent.putExtra("isfristbluetooth", true);
        } else
            intent.putExtra("isfristbluetooth", false);
        startActivity(intent);

        overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);
    }


    /**
     * 恢复指定的搜索到的设备的View
     *
     * @param device 蓝牙设备
     */
    private void recoverFoundDeviceView(BluetoothDevice device, View foundView) {
        if (foundView != null) {
            TextView nameTv = (TextView) foundView
                    .findViewById(R.id.tv_device_name);

            nameTv.setText(device.getName());
        }
    }

    @Override
    public void onGetDeviceInfo(String s, String s1, String s2, int i) {

    }

    @Override
    public void onUpgradeProgress(int i) {

    }

    @Override
    public void onUpgradeFirmware(boolean b, String s) {

    }

    @Override
    public void onUpdateKey() {

    }

    @Override
    public void onGetCheckcode(String s) {

    }

    @Override
    public void onSetFactor() {

    }

    @Override
    public void onOpenCardReader(String s) {

    }

    @Override
    public void onCloseCardReader() {

    }

    @Override
    public void onReadCardNumber(String s, String s1) {

    }

    @Override
    public void onReadTrackData(String s, String s1, String s2, String s3, String s4, String s5) {

    }

    @Override
    public void onGetPbocTradeData(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7) {

    }

    @Override
    public void onExecutePbocSecondAuth(String s, String s1, String s2, String s3, String s4) {

    }

    @Override
    public void onEncryptPin(String s, String s1) {

    }

    @Override
    public void onCalculateMac(String s, String s1) {

    }

    @Override
    public void onChangeToUpgradeMode() {

    }

    @Override
    public void onWriteDeviceId() {

    }

    @Override
    public void onWriteMainKey() {

    }

    @Override
    public void onTestCommunicate() {

    }

    @Override
    public void onStateChanged(int i) {

        if (i == BluetoothAdapter.STATE_ON) {
            shebeilayout.setVisibility(View.VISIBLE);
            getBondedDevices();
        } else if (i == BluetoothAdapter.STATE_OFF) {
            shebeilayout.setVisibility(View.GONE);
            removeAllFoundDeviceViews();
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice bluetoothDevice, short i) {

        // TODO Auto-generated method stub
        String bluetoothName = bluetoothDevice.getName();
        if ((!(bluetoothName.length() < 4))
                && bluetoothName.substring(0, 4).equalsIgnoreCase("upos")) {

            if (!mFoundList.contains(bluetoothDevice) && !mPairedList.contains(bluetoothDevice)) {
                mFoundList.add(bluetoothDevice);
                addAvailable_list(bluetoothDevice);
            }


        }
    }

    @Override
    public void onDiscoveryStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDiscoveryFinished() {
        if (mFoundList.isEmpty()) {
            MainUtils.showToast(getApplicationContext(), "未搜索到设备，请重新搜索....");
            //                显示没有设备的imagelayout
            availableList.setVisibility(View.GONE);
            availablematchedimagelayout.setVisibility(View.VISIBLE);
            availableListScroll.setVisibility(View.GONE);
        }
        bluetoothPro.setVisibility(View.GONE);
    }


    @Override
    public void onBondStateChanged(BluetoothDevice bluetoothDevice, int i) {

        // TODO Auto-generated method stub
        switch (i) {
            case BluetoothDevice.BOND_NONE:// 解除绑定
                mPairedList.remove(bluetoothDevice);
                mFoundList.add(bluetoothDevice);
                removeAlready_matched_list(bluetoothDevice);

//                显示没有设备的imagelayout
                if (mPairedList.isEmpty()) {
                    alreadyMatchedListScroll.setVisibility(View.GONE);
                    alreadymatchedimage_layout.setVisibility(View.VISIBLE);
                }

                //移除配对列表添加到搜索列表
                View foundView = availableList.findViewWithTag(bluetoothDevice);
                if (foundView == null) {
                    addAvailable_list(bluetoothDevice);

                    foundView = availableList.findViewWithTag(bluetoothDevice);
                }
                recoverFoundDeviceView(bluetoothDevice, foundView);

                break;
            case BluetoothDevice.BOND_BONDING:// 正在绑定
                break;
            case BluetoothDevice.BOND_BONDED:// 已绑定
                mFoundList.remove(bluetoothDevice);
                mPairedList.add(bluetoothDevice);
                removeAvailable_list(bluetoothDevice);
                addAlready_matched_list(bluetoothDevice);



                alreadyMatchedListScroll.setVisibility(View.VISIBLE);
                alreadymatchedimage_layout.setVisibility(View.GONE);


                break;
        }

    }

    @Override
    public void onConnectStateChanged(BluetoothDevice bluetoothDevice, int i) {

    }

    @Override
    public void onError(int i) {
        MainUtils
                .showToast(this, "掌芯宝返回错误码：" + i);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: ");
        if (isChecked) {
            manager.openBluetooth();
        } else {

            /**关闭蓝牙之前先关闭搜索条的显示*/

            if( bluetoothPro.getVisibility()==View.VISIBLE){
                bluetoothPro.setVisibility(View.GONE);
            }
            manager.closeBluetooth();
        }
    }
}
