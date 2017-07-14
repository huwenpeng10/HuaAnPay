package com.example.mrxu.mutils;

/**
 * Created by MrXu on 2017/5/14.
 */


import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * xml文件生成器类
 *
 * @author acer
 *
 */
public class XmlBuilder {



    public static final String TAG = "XML_FACTORY";

    public static final class ATTRIBUTE {
        public static final String PHOTO = "photo";
    }

    private int[] field;
    private static final String[] COMMON_HEAD = { "TxCode", "TxDate", "TxTime",
            "TxAction", "Opeartor", "Version" };
    private String xml;
    private static final String ROOT = "root";
    private static final String HEAD = "head";
    private static final String BODY = "body";
    private static final String FIELD = "Field";
    private static final String PLACE_HOLDER = "%s";

    public String getXml() {
        return xml;
    }

    public XmlBuilder(int[] field) {
        for (int i = 0; i < field.length - 1; i++) {
            boolean flag = false;
            for (int j = 0; j < field.length - i - 1; j++) {
                if (field[j] > field[j + 1]) {
                    int temp = field[j + 1];
                    field[j + 1] = field[j];
                    field[j] = temp;
                    flag = true;
                }
            }
            if (!flag) {
                break;
            }
        }
        this.field = field;
    }

    /**
     * <li>创建XML报文模板</li>
     *
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public void createModule() throws IllegalArgumentException,
            IllegalStateException, IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        serializer.setOutput(stringWriter);
        serializer.startDocument("utf-8", null);
        serializer.startTag(null, ROOT);
        serializer.startTag(null, HEAD);
        for (String TAG : COMMON_HEAD) {
            serializer.startTag(null, TAG);
            serializer.text(PLACE_HOLDER);
            serializer.endTag(null, TAG);
        }
        serializer.endTag(null, HEAD);
        serializer.startTag(null, BODY);
        for (int TAG : field) {
            serializer.startTag(null, fieldName(TAG));
            serializer.text(PLACE_HOLDER);
            serializer.endTag(null, fieldName(TAG));
        }
        serializer.endTag(null, BODY);
        serializer.endTag(null, ROOT);
        serializer.endDocument();
        xml = stringWriter.toString();
        Log.i(TAG, xml);
    }

    /**
     * <li>创建XML报文模板</li>
     *
     * @param attrs
     *            key 需要添加属性的标签编号 value 需要添加的属性
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public void createModule(SparseArray<?>... attrs)
            throws IllegalArgumentException, IllegalStateException, IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        serializer.setOutput(stringWriter);
        serializer.startDocument("utf-8", null);
        serializer.startTag(null, ROOT);
        serializer.startTag(null, HEAD);
        for (String TAG : COMMON_HEAD) {
            serializer.startTag(null, TAG);
            serializer.text(PLACE_HOLDER);
            serializer.endTag(null, TAG);
        }
        serializer.endTag(null, HEAD);
        serializer.startTag(null, BODY);
        for (int TAG : field) {
            serializer.startTag(null, fieldName(TAG));
            for (SparseArray<?> attr : attrs) {
                if (!(attr.indexOfKey(TAG) < 0)) {
                    serializer.attribute(null, "attr",
                            String.valueOf(attr.get(TAG)));
                }
            }
            serializer.text(PLACE_HOLDER);
            serializer.endTag(null, fieldName(TAG));
        }
        serializer.endTag(null, BODY);
        serializer.endTag(null, ROOT);
        serializer.endDocument();
        xml = stringWriter.toString();
    }

    public static final String build(int code, String xml) {
        // 中文上送时必须使用字节数组长度以避免长度计算错误
        String message = String.format("%08d", xml.getBytes().length
                + TagUtils.TxCode(code).getBytes().length)
                + TagUtils.TxCode(code) + xml;
        Log.i(TAG, message);
        return message;
    }

    public static final String fieldName(int code) {
        return FIELD + String.format("%03d", code);
    }

    /**
     * <li>填充数据，其中Head部分已填充，data仅传入Body部分即可</li>
     *
     * @param context
     * @param txCode
     * @param module
     * @param data
     * @return
     * @throws
     */
    public static final String inject(Context context, int txCode,
                                      String module, Object... data) throws Exception {
        String str = module;
        str = str.replaceFirst(PLACE_HOLDER, TagUtils.TxCode(txCode));
        str = str.replaceFirst(PLACE_HOLDER, TagUtils.TxDate());
        str = str.replaceFirst(PLACE_HOLDER, TagUtils.TxTime());
        str = str.replaceFirst(PLACE_HOLDER, TagUtils.TxAction());
        str = str.replaceFirst(PLACE_HOLDER, TagUtils.Operator());
        str = str.replaceFirst(PLACE_HOLDER, TagUtils.versionCode(context));
        str = String.format(str, data);
        Log.i(TAG, str);
        return str;
    }

    public static final SparseArray<String> newAttrs(int[] codes, String attr) {
        SparseArray<String> attrs = new SparseArray<String>(codes.length);
        for (int i : codes) {
            attrs.put(i, attr);
        }
        return attrs;
    }



}
