package com.example.word_magnifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.word_magnifier.widget.WordMagnifier;


public class MainActivity extends Activity implements OnLongClickListener {
    private static final String TAG = "MainActivity";
    private TextView contentText;
    private WordMagnifier mWordMagnifier;

    private boolean isMagnifierAdded = false;
    private volatile MotionEvent mCurrentMotionEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentText = (TextView) findViewById(R.id.english_sentence);
        contentText.setOnLongClickListener(this);
        contentText.setOnTouchListener(mExercisePanelTouchListener);
        mWordMagnifier = new WordMagnifier(getBaseContext());

        ((ViewGroup)getWindow().getDecorView()).addView(mWordMagnifier);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "on long click");
        isMagnifierAdded = true;
        //mWordMagnifier.prepareForShow();
        showMagnifier(mCurrentMotionEvent);
        return true;
    }

    OnTouchListener mExercisePanelTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "on action down");
                    mCurrentMotionEvent = MotionEvent.obtain(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "on action move");
                    mCurrentMotionEvent = MotionEvent.obtain(event);
                    showMagnifier(event);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isMagnifierAdded) {
                        mWordMagnifier.hideMagnifier();
                        isMagnifierAdded = false;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void showMagnifier(MotionEvent event) {
        if (isMagnifierAdded) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            mWordMagnifier.showTouchRegion(getWindow().getDecorView(), x, y);
        }
    }
}
