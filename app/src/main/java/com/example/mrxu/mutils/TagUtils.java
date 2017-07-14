package com.example.mrxu.mutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by MrXu on 2017/5/14.
 */

public class TagUtils {


    /**
     * <li>Log标签</li>
     */
    public static String TAG = "TAG_FACTORY";

    /**
     * <li>位元表{@link TagUtils#Field001(int[])} 长度</li>
     */
    private static int LENGTH = 128;

    /**
     * <li>交易代码(6)，自动补全</li>
     *
     * @param code
     * @return
     */
    public static final String TxCode(int code) {
        String str = String.format("F%05d", code);
        Log.i(TAG, "TxCode=" + str);
        return str;
    }

    /**
     * <li>当前操作日期(8)——>yyyyMMdd</li>
     *
     * @return
     */
    public static final String TxDate() {
        String str = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        Log.i(TAG, "TxDate=" + str);
        return str;
    }

    /**
     * <li>当前操作时间(6)——>HHmmss</li>
     *
     * @return
     */
    public static final String TxTime() {
        String str = new SimpleDateFormat("HHmmss").format(new java.util.Date());
        Log.i(TAG, "TxTime=" + str);
        return str;
    }

    /**
     * <li>交易描述</li>
     *
     * @return
     */
    public static final String TxDesc(String description) {
        String str = description;
        Log.i(TAG, "TxDesc=" + str);
        return str;
    }

    /**
     * <li>交易类型(3)，定值{@code TxAction=000}</li>
     *
     * @return
     */
    public static final String TxAction() {
        String str = "000";
        Log.i(TAG, "TxAction=" + str);
        return str;
    }

    /**
     * <li>操作员(8)，定值{@code Operator=00000000}</li>
     *
     * @return
     */
    public static final String Operator() {
        String str = "00000000";
        Log.i(TAG, "Operator=" + str);
        return str;
    }

    /**
     * <li>位元表(128)</li>
     *
     * @param field
     *            请不要出现重复位
     * @return
     */
    public static final String Field001(int[] field) {
        StringBuffer buffer = new StringBuffer();
        Arrays.sort(field);
        int index = 0;
        for (int i = 0; i < LENGTH; i++) {
            if (index < field.length) {
                if ((i + 1) == field[index]) {
                    buffer.append(1);
                    index = index + 1;
                } else {
                    buffer.append(0);
                }
            } else {
                buffer.append(0);
            }
        }
        String str = buffer.toString();
        Log.i(TAG, "Field001=" + str);
        return str;
    }

    /**
     * <li>交易处(6)，定值{@code Field003=100000}</li>
     *
     * @return
     */
    public static final String Field003() {
        String str = "100000";
        Log.i(TAG, "Field003=" + str);
        return str;
    }

    /**
     * <li>受卡方系统跟踪号(6)，每次交易需自增</li>
     * <p>
     * 暂未实现，测试值1自动补全
     * </p>
     *
     * @deprecated
     * @return
     */
    public static final String Field011() {
        String str = String.format("%06d", 1);
        Log.i(TAG, "Field011=" + str);
        return str;
    }

    public static final String Field011(Context context) {

        String str = context.getSharedPreferences("serial_number",
                Context.MODE_PRIVATE).getString("sn", String.format("%06d", 1));
        Log.d("SN", str);
        return str;
    }

    public static final void Field011Add(Context context) {
        int sn = Integer.parseInt(Field011(context));
        if (sn + 1 > 999999)
            context.getSharedPreferences("serial_number", Context.MODE_PRIVATE)
                    .edit().putString("sn", String.format("%06d", 1)).apply();
        else
            context.getSharedPreferences("serial_number", Context.MODE_PRIVATE)
                    .edit().putString("sn", String.format("%06d", sn + 1))
                    .apply();
        Log.d("SN", "交易流水号自增");
    }

    /**
     * <li>应答码(2)</li>
     * <p>
     * 内容未知，测试值为1自动补全
     * </p>
     *
     * @return
     */
    public static final String Field039() {
        String str = String.format("%02d", 1);
        Log.i(TAG, "Field039=" + str);
        return str;
    }

    public static final String versionCode(Context context)
            throws Exception {
        String str = context.getPackageManager().getPackageInfo(
                context.getPackageName(), 0).versionName;
        Log.i(TAG, "TxAction=" + str);
        return str;
    }

    /**
     * <li>MAC计算</li>
     *
     * @param TxCode
     * @param TxDate
     * @param TxTime
     * @param Field011
     * @param phoneNumber
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final String MAC(String TxCode, String TxDate, String TxTime,
                                   String Field011, String phoneNumber)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return md5Encode(TxCode + TxDate + TxTime + Field011 + phoneNumber)
                .substring(0, 16).toUpperCase();
    }

    /**
     * <li>MD5加密</li>
     *
     * @param resourse
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final String md5Encode(String resourse)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (!TextUtils.isEmpty(resourse)) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f' };
            byte[] md5Byte = md5.digest(resourse.getBytes("UTF8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md5Byte.length; i++) {
                sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
                sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
            }
            resourse = sb.toString();
        }
        return resourse;
    }

    public static final String newPhoto(Context context, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        final int width = options.outWidth;
        final int height = options.outHeight;
        final float ratio = width > height ? width > 400 ? width / 400 : 1
                : height > 400 ? height / 400 : 1;
        options.inSampleSize = new BigDecimal(ratio).intValueExact();
        return newPhoto(context, BitmapFactory.decodeFile(path, options));
    }

    public static final String newPhoto(Context context, final Bitmap bm) {
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ratio = 100;
            do {
                baos.reset();
                bm.compress(Bitmap.CompressFormat.JPEG, ratio, baos);
                ratio -= 5;
            } while (baos.toByteArray().length >= 60 * 1024);
            String str = Base64.encodeToString(baos.toByteArray(),
                    Base64.NO_WRAP).trim();
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("Test",
                    "Photo大小" + Formatter.formatFileSize(context, str.length()));
            return str;
        }
        return null;
    }

    public static final String fillSpaceAtLeft(String resourse, int length) {
        return String.format("%1$" + length + "s", resourse);
    }

    public static final String fillSpaceAtRight(String resourse, int length) {
        return String.format("%1$-" + length + "s", resourse);
    }

    public static final String fill0AtLeft(int number, int length) {
        return String.format("%0" + length + "d", number);
    }

    public static final String AuthenticationInformation(String userName,
                                                         String id, String cardNo, String account, String bankNo)
            throws UnsupportedEncodingException {
        String str = GenerateParameter(userName, id, cardNo, account, bankNo);
        Log.i(TAG, "AuthenticationInformation=" + str);
        return str;
    }

    public static final String AddressInformation(String name, String phone,
                                                  String address) throws UnsupportedEncodingException {
        String str = GenerateParameter(name, phone, address);
        Log.i(TAG, "AddressInformation=" + str);
        return str;
    }

    public static final String ModifyPassword(String oldPassword,
                                              String password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String str = GenerateParameter(md5Encode(oldPassword),
                md5Encode(password));
        Log.i(TAG, "ChangePassword=" + str);
        return str;
    }

    public static final String ResetPassword(String code, String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String str = GenerateParameter(code, md5Encode(password));
        Log.i(TAG, "ChangePassword=" + str);
        return str;
    }

    private static final String GenerateParameter(String... data)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(data[i]);
            if (i != data.length - 1) {
                result.append("|");
            }
        }
        return result.toString();
    }
}
