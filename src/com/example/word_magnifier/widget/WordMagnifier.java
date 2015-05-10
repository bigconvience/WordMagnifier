package com.example.word_magnifier.widget;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.example.word_magnifier.utils.DisplayUtils;

public class WordMagnifier extends ImageView {
    private static WindowManager sWindowManager;
    private static WindowManager.LayoutParams mLayoutParams;

    private static int paramX;
    private static int paramY;

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 200;
    private static final float SCALE_FACTOR = 2.0f;
    private static final String TAG = "WordMagnifier";
    private Context mContext;

    public WordMagnifier(Context context) {
        super(context);
        init(context);
    }

    public WordMagnifier(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordMagnifier(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        initMagWindowManager(context);
        initMagWindowParams();
    }

    private void initMagWindowManager(Context context) {
        sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    private void initMagWindowParams() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = WINDOW_WIDTH;
        mLayoutParams.height = WINDOW_HEIGHT;
    }

    /**
     * 注意，在显示前调用
     */
    public void prepareForShow() {
        sWindowManager.addView(this, mLayoutParams);
    }

    private void updateParam(int x, int y) {
        Log.d(TAG, "x: " + x + " y:" + y);
        mLayoutParams.x = getMagnifierLeft(x);
        mLayoutParams.y = getMagnifierTop(y);
        paramX = mLayoutParams.x;
        paramY = mLayoutParams.y;
        Log.d(TAG, "paramX: " + paramX + " paramY:" + paramY);
    }

    private void moveMagnifier(int x, int y) {
        updateParam(x, y);
        sWindowManager.updateViewLayout(this, mLayoutParams);
    }

    public void hideMagnifier() {
        sWindowManager.removeView(this);
    }

    public void showTouchRegion(View touchedView, int x, int y) {
        moveMagnifier(x, y);
        setBackgroundDrawable(getCurrentImage(touchedView, x, y));
    }

    private BitmapDrawable getCurrentImage(View touchedView, int x, int y) {
        Log.d(TAG, "get image at x: " + x + " y:" + y);
        Bitmap magnifierBitmap = Bitmap.createBitmap(WINDOW_WIDTH, WINDOW_HEIGHT, Config.ARGB_8888);

        touchedView.setDrawingCacheEnabled(true);
        Bitmap currentScreen = touchedView.getDrawingCache();

        Paint paint = new Paint();
        Canvas canvas = new Canvas(magnifierBitmap);
        canvas.scale(SCALE_FACTOR, SCALE_FACTOR,
                WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        canvas.drawBitmap(currentScreen,
                getDisplayRegionLeft(x),
                getDisplayRegionTop(y),
                paint);

        BitmapDrawable outputDrawable = new BitmapDrawable(mContext.getResources(), magnifierBitmap);
        return outputDrawable;
    }

    private int getMagnifierLeft(int touchedX) {
        return (touchedX - DisplayUtils.getWidthPixels() / 2);
    }

    private int getMagnifierTop(int touchedY) {
        return (touchedY - DisplayUtils.getHeightPixels() / 2 - WINDOW_HEIGHT);
    }

    private int getDisplayRegionLeft(int touchedX) {
        return -(touchedX - WINDOW_WIDTH / 2);
    }

    private int getDisplayRegionTop(int touchedY) {
        return -(touchedY - WINDOW_HEIGHT / 2);
    }

    private int getDisplayViewWidth() {
        return DisplayUtils.getWidthPixels();
    }

    private int getDisplayViewHeight() {
        return DisplayUtils.getHeightPixels();
    }
}
