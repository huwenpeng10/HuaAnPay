package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.allinfo.UserInfo.AuthState;
import com.example.mrxu.assemble_xml.Authentication;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.CacheFile;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static android.widget.Toast.makeText;

public class AuthenticationPhotoActivity extends BaseActivity implements ImageLoadingListener {


    @BindView(R.id.left_Imagemage)
    ImageView leftImageage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.card_frontphoto)
    ImageView cardFrontphoto;
    @BindView(R.id.card_negativephoto)
    ImageView cardNegativephoto;
    @BindView(R.id.card_hold_photo)
    ImageView cardHoldPhoto;
    @BindView(R.id.commit_but)
    Button commitBut;
    @BindView(R.id.progress_dialog)
    ProgressBar progressDialog;
    @BindView(R.id.card_frontphoto_tv)
    TextView cardFrontphotoTv;
    @BindView(R.id.card_negativephoto_tv)
    TextView cardNegativephotoTv;
    @BindView(R.id.card_hold_photo_tv)
    TextView cardHoldPhotoTv;
    @BindView(R.id.card_frontphoto_layout)
    AutoRelativeLayout cardFrontphotoLayout;
    @BindView(R.id.card_negativephoto_layout)
    AutoRelativeLayout cardNegativephotoLayout;
    @BindView(R.id.card_hold_photo_layout)
    AutoRelativeLayout cardHoldPhotoLayout;

    private EventBus eventBus;


    public static final int REQUEST_FRONT = 0;
    public static final int REQUEST_REVERSE = 1;
    public static final int REQUEST_SENCE = 2;

    private String[] photoPath = new String[3];


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_authentication_photo;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        Circle chasingDots = new Circle();
        chasingDots.setColor(0xFF3798f4);
        progressDialog.setIndeterminateDrawable(chasingDots);

        titleBarText.setText("实名认证");
        leftImageage.setOnClickListener(this);
        cardFrontphoto.setOnClickListener(this);
        cardNegativephoto.setOnClickListener(this);
        cardHoldPhoto.setOnClickListener(this);
        commitBut.setOnClickListener(this);
        cardFrontphotoLayout.setOnClickListener(this);
        cardNegativephotoLayout.setOnClickListener(this);
        cardHoldPhotoLayout.setOnClickListener(this);

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    public void onEvent(TcpRequestMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressDialog.setVisibility(View.GONE);
            }
        });
        if (message.getCode() == 1) {
            TagUtils.Field011Add(this);
            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));
                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            MainUtils.showToast(AuthenticationPhotoActivity.this, "认证照片上传成功");
                            UserInfo.setAuthState(AuthState.AUTHERING);
                            UserInfo.setAuthTips("认证中");
                            UserInfo.setAuthTipsNumber(2);

                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            makeText(
                                    AuthenticationPhotoActivity.this,
                                    data.get(XmlBuilder.fieldName(124)),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Intent intent = new Intent(
                        AuthenticationPhotoActivity.this,
                        MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            if (message.getCode() == -2) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(AuthenticationPhotoActivity.this,
                                "网络连接超时...");
                    }
                });
            }
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
            case R.id.card_frontphoto:
            case R.id.card_frontphoto_layout:
                photoPath[0] = takePicture(CacheFile.AUTH1, REQUEST_FRONT);
                break;
            case R.id.card_negativephoto:
            case R.id.card_negativephoto_layout:

                photoPath[1] = takePicture(CacheFile.AUTH2, REQUEST_REVERSE);

                break;
            case R.id.card_hold_photo:
            case R.id.card_hold_photo_layout:

                photoPath[2] = takePicture(CacheFile.AUTH3, REQUEST_SENCE);

                break;
            case R.id.commit_but:
                for (String path : photoPath) {
                    if (TextUtils.isEmpty(path)) {
                        MainUtils.showToast(this, "请提供完整认证照片");
                        return;
                    }
                }

                final Intent data = getIntent();
                try {
                    progressDialog.setVisibility(View.VISIBLE);
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        public void run() {
                            try {
                                String _data = new Authentication(
                                        AuthenticationPhotoActivity.this,
                                        data.getStringExtra(AuthenticationActivity.KEY_NAME),
                                        data.getStringExtra(AuthenticationActivity.KEY_USERID),
                                        data.getStringExtra(AuthenticationActivity.KEY_CARDNUMBER),
                                        data.getStringExtra(AuthenticationActivity.KEY_OPPENNAME),
                                        data.getStringExtra(AuthenticationActivity.KEY_NUMBER),
                                        photoPath).toString();

                                new TcpRequest(Construction.HOST, Construction.PORT)
                                        .request(_data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
                    progressDialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                break;


            default:
                break;

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FRONT:
                    ImageLoader.getInstance().displayImage(
                            Uri.fromFile(new File(photoPath[0])).toString(),
                            cardFrontphoto, AuthenticationPhotoActivity.this);
                    cardFrontphotoTv.setVisibility(View.GONE);
                    break;
                case REQUEST_REVERSE:
                    ImageLoader.getInstance().displayImage(
                            Uri.fromFile(new File(photoPath[1])).toString(),
                            cardNegativephoto, AuthenticationPhotoActivity.this);
                    cardNegativephotoTv.setVisibility(View.GONE);
                    break;
                case REQUEST_SENCE:
                    ImageLoader.getInstance().displayImage(
                            Uri.fromFile(new File(photoPath[2])).toString(),
                            cardHoldPhoto, AuthenticationPhotoActivity.this);
                    cardHoldPhotoTv.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String takePicture(String fileName, int requestCode) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File file = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    fileName);
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                            MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)),
                    requestCode);
            return file.getPath();
        } else {
            makeText(this, "请挂载SD卡", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

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
}
