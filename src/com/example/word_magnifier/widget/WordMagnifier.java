package com.example.word_magnifier.widget;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.example.word_magnifier.R;

public class WordMagnifier extends ImageView {
    private static WindowManager sWindowManager;
    private static WindowManager.LayoutParams mLayoutParams;
    private PopupWindow mPopupWindow;

    private static int lastX;
    private static int lastY;
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
        initFloatingWindow();
    }

    private void initMagWindowManager(Context context) {
        sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    private void initMagWindowParams() {
        mLayoutParams = getLayoutParams();
        mLayoutParams.width = WINDOW_WIDTH;
        mLayoutParams.height = WINDOW_HEIGHT;
    }

    private void initFloatingWindow() {
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(null);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setContentView(this);
        mPopupWindow.setWidth(WINDOW_WIDTH);
        mPopupWindow.setHeight(WINDOW_HEIGHT);
    }

    public void setAnchorView(View anchorView) {
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0] + WINDOW_HEIGHT,
                location[1] + WINDOW_HEIGHT);
        mPopupWindow.showAtLocation(anchorView, Gravity.TOP, paramX, paramY);

    }

    /**
     * 注意，在显示前调用
     */
    public void prepareForShow() {
        sWindowManager.addView(this, mLayoutParams);
    }

    public void saveTouchPoint(int x, int y) {
        updateParam(x, y);
    }

    private void updateParam(int x, int y) {
        Log.d(TAG, "x: " + x + " y:" + y);
        lastX = x;
        lastY = y;

        mLayoutParams.x = x - WINDOW_WIDTH / 2;
        mLayoutParams.y = y - WINDOW_HEIGHT  - 20;
        paramX = mLayoutParams.x;
        paramY = mLayoutParams.y;
        Log.d(TAG, "paramX: " + paramX + " paramY:" + paramY);
    }

    private void updatePosition(int x, int y) {
        setX(x);
        setY(y);
    }

    private void moveMagnifier(int x, int y) {
        //updateParam(x, y);
        updatePosition(x, y);
        //sWindowManager.updateViewLayout(this, mLayoutParams);
    }

    public void hideMagnifier() {
       // sWindowManager.removeView(this);
        mPopupWindow.dismiss();
    }

    public void showTouchRegion(View touchedView, int x, int y) {
        moveMagnifier(x, y);
        setBackground(getCurrentImage(touchedView, x, y));
    }

    private BitmapDrawable getCurrentImage(View touchedView, int x, int y) {
        Log.d(TAG, "get image at x: " + x + " y:" + y);
        Bitmap magnifierBitmap = Bitmap.createBitmap(WINDOW_WIDTH, WINDOW_HEIGHT, Config.ARGB_8888);

        touchedView.setDrawingCacheEnabled(true);
        Bitmap currentScreen = touchedView.getDrawingCache();

        Paint paint = new Paint();
        Canvas canvas = new Canvas(magnifierBitmap);
        canvas.scale(SCALE_FACTOR, SCALE_FACTOR);
        canvas.drawBitmap(currentScreen,
                0, -80, paint);

        BitmapDrawable outputDrawable = new BitmapDrawable(mContext.getResources(), magnifierBitmap);
        return outputDrawable;
    }

    private int getLeftForDisplayRegion(int touchedX) {
        return -(touchedX - WINDOW_WIDTH / 2);
    }

    private int getTopForDisplayRegion(int touchedY) {
        return -(touchedY + getResources().getDimensionPixelOffset(R.dimen.height_below_touch_point) - WINDOW_HEIGHT);
    }
}
