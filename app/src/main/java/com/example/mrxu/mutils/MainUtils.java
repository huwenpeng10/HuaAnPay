package com.example.mrxu.mutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Toast;

import com.example.mrxu.allinfo.UserInfo;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by MrXu on 2017/5/14.
 */

public class MainUtils {


    private static Toast toast = null;


    public static boolean isLogin() {
        if (UserInfo.getPhoneNumber() == null
                || UserInfo.getSessionCode() == null) {
            return true;
        } else {
            return false;
        }
    }

    //对指定的view进行绘图
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    public static void showToast(Context context, String content) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.show();
    }


    //按ascii码顺序从大到小顺序排列
    private String getSign(Map<String, String> params) {
        Map<String, String> sortMap = new TreeMap<String, String>();
        sortMap.putAll(params);
        // 以k1=v1&k2=v2...方式拼接参数
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> s : sortMap.entrySet()) {
            String k = s.getKey();
            String v = s.getValue();
            if (v == null || v.equals("")) {// 过滤空值
                continue;
            }
            builder.append(k).append("=").append(v).append("&");
        }
        if (!sortMap.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return MD5utils.getMD5String(builder.toString()).toUpperCase();
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "???";
        }
    }


    /**
     * 获取code  异常  -1
     *
     * @return 当前应用的code
     */
    public static int getAppCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int code = info.versionCode;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
