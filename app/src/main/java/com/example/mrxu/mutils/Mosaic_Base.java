package com.example.mrxu.mutils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;

/**
 * Created by MrXu on 2017/5/14.
 */


/*
 * <hn>所有接口均需要继承此类已完成拼接，使用 {@link Base#Base(Context, int, int[])}传入交易代码，以及位元表或使用
 * {@link #Base(Context, int, int[], SparseArray...)}
 * 传入交易代码、位元表以及需要而外添加的属性完成此类的构造方法，并使用
 * {@link XmlBuilder#inject(Context, int, String, Object...)}填充数据即可，例证请参见
 * {@link Login}</hn>
 *
 * @author Angleline
 *
 */

public class Mosaic_Base {


    protected int TxCode;
    protected int[] bits;
    protected String xml;

    /**
     * @param context
     * @param TxCode
     * @param bits
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public Mosaic_Base(Context context, final int TxCode, final int[] bits)
            throws IllegalArgumentException, IllegalStateException, IOException {
        this.TxCode = TxCode;
        this.bits = bits;
        XmlBuilder xmlFactory = new XmlBuilder(bits);// 实例化XML报文模板构造器
        xmlFactory.createModule();// 构造XML报文模板
        xml = xmlFactory.getXml();// 获得XML报文模板
    }

    /*
     * 可参照{@link Authentication}完成
     *
     * @param context
     * @param TxCode
     * @param bits
     * @param attrs
     *            请使用{@link XmlBuilder#newAttrs(int[], String)}数组填充此参数
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public Mosaic_Base(Context context, final int TxCode, final int[] bits,
                final SparseArray<?>... attrs) throws IllegalArgumentException,
            IllegalStateException, IOException {
        this.TxCode = TxCode;
        this.bits = bits;
        XmlBuilder xmlFactory = new XmlBuilder(bits);// 实例化XML报文模板构造器
        xmlFactory.createModule(attrs);// 构造XML报文模板
        xml = xmlFactory.getXml();// 获得XML报文模板
    }

    @Override
    public String toString() {
        final String message = XmlBuilder.build(TxCode, xml);
        Log.d("Final_Message", message);
        return message;
    }

}
