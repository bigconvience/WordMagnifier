package com.example.word_magnifier.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.word_magnifier.R;

/**
 * Created by Administrator on 2015/5/9.
 */
public class ExercisePanel extends RelativeLayout implements View.OnLongClickListener{
    private static final String TAG = "ExercisePanel";
    private static final float SCALE_FACTOR = 2.0f;
    private TextView mEnglishTextView;

    private boolean isMagnifierAdded = false;
    private volatile MotionEvent mCurrentMotionEvent;
    private View mGlassView;
    private View mZoomView;
    private int mGlassWidth;
    private int mGlassHeight;
    private Bitmap mContentBitmap;

    public ExercisePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExercisePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.exercise_panel_item, this, true);
        mEnglishTextView = (TextView) findViewById(R.id.english_sentence);
        init(context);
    }

    private void init(Context context) {
        mGlassWidth = getResources().getDimensionPixelOffset(R.dimen.glass_view_width);
        mGlassHeight = getResources().getDimensionPixelOffset(R.dimen.glass_view_height);
        initMagnifierView(context);
        setOnLongClickListener(this);
        setOnTouchListener(mTouchListener);
    }

    private void initMagnifierView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.word_magnifier_view, this, true);
        mGlassView = findViewById(R.id.glass_view);
        mZoomView = findViewById(R.id.zoom_view);
        mGlassView.setVisibility(INVISIBLE);
    }

    @Override
    public boolean onLongClick(View v) {
        isMagnifierAdded = true;
        mContentBitmap = takeScreenShot(this);
        tryShowMagnifier(mCurrentMotionEvent);
        return true;
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.d(TAG, "touched x:" + event.getRawX() + " y:" + event.getRawY());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mCurrentMotionEvent = MotionEvent.obtain(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mGlassView.setVisibility(INVISIBLE);
                    mCurrentMotionEvent = MotionEvent.obtain(event);
                    tryShowMagnifier(event);
                    break;
                case MotionEvent.ACTION_UP:
                    tryHideMagnifier();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void tryShowMagnifier(MotionEvent event) {
        if (isMagnifierAdded) {
            mGlassView.setVisibility(INVISIBLE);

            updateGlassViewPosition(event);
            showTouchRegion(event);
        }
    }

    private int getMagnifierLeft(int touchedX) {
        return (touchedX - mGlassWidth / 2);
    }

    private int getMagnifierTop(int touchedY) {
        return (touchedY - mGlassHeight * 3);
    }

    private int getDisplayRegionLeft(int touchedX) {
        return (touchedX - mGlassWidth / 2);
    }

    private int getDisplayRegionTop(int touchedY) {
        return (touchedY - mGlassHeight + getResources().getDimensionPixelOffset(R.dimen.height_below_touch_point));
    }

    private void tryHideMagnifier() {
        if (isMagnifierAdded) {
            mGlassView.setVisibility(GONE);
            isMagnifierAdded = false;
        }
    }

    private Bitmap takeScreenShot(View view) {
        // configuramos para que la view almacene la cache en una imagen
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        view.buildDrawingCache();

        if(view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    private void updateGlassViewPosition(MotionEvent motionEvent) {
        int x = getMagnifierLeft((int) motionEvent.getRawX());
        int y = getMagnifierTop((int) motionEvent.getRawY());
        mGlassView.setVisibility(VISIBLE);
        mGlassView.setX(x);
        mGlassView.setY(y);
        Log.d(TAG, "glass view position x:" + x + " y:" + y);
    }

    private void showTouchRegion(MotionEvent event) {
        int x = getDisplayRegionLeft((int) event.getX());
        int y = getDisplayRegionTop((int) event.getY());
        mZoomView.setBackgroundDrawable(getCurrentImage(x, y));
    }

    private BitmapDrawable getCurrentImage(int x, int y) {
        Log.d(TAG, "get image at x: " + x + " y:" + y);
        Bitmap magnifierBitmap = Bitmap.createBitmap(mGlassWidth, mGlassHeight, mContentBitmap.getConfig());

        Paint paint = new Paint();
        Canvas canvas = new Canvas(magnifierBitmap);
        canvas.scale(SCALE_FACTOR, SCALE_FACTOR,
                mGlassWidth / 2, mGlassHeight / 2);
        canvas.drawBitmap(mContentBitmap,
                -x,
                -y,
                paint);

        BitmapDrawable outputDrawable = new BitmapDrawable(getResources(), magnifierBitmap);
        return outputDrawable;
    }
}
