package com.example.mrxu.myviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * 
 * 签名控件
 * 
 */
public class SignatureView extends View {
	
	private float preX;
	private float preY;
	private Path path;
	private Paint paint = null;
	private Bitmap cacheBitmap = null;
	private Canvas cacheCanvas = null;

	/**
	 * @param context
	 */
	public SignatureView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SignatureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * <li>初始化缓存数�?/li>
	 * 
	 * @param width
	 * @param height
	 */
	public void init(final int width, final int height) {
		cacheCanvas = new Canvas();
		path = new Path();
		cacheCanvas.setBitmap(cacheBitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888));
		cacheCanvas.drawColor(Color.WHITE);
		paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
	}

	@Override
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);
		init(getWidth(), getHeight());
	}

	/**
	 * <li>清空签名</li>
	 */
	public void clear() {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		cacheCanvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(x, y);
			preX = x;
			preY = y;
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(preX, preY, x, y);
			preX = x;
			preY = y;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			cacheCanvas.drawPath(path, paint);
			path.reset();
			invalidate();
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(cacheBitmap, 0, 0, paint);
		canvas.drawPath(path, paint);
	}

	/**
	 * <li>获得预览图片</li>
	 * 
	 * @return
	 */
	public Bitmap getPreviewImage() {
		return cacheBitmap;
	}
}
