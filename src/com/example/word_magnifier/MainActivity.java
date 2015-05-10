package com.example.word_magnifier;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import com.example.word_magnifier.utils.DisplayUtils;
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
        DisplayUtils.init(getBaseContext());
    }

    @Override
    public boolean onLongClick(View v) {
        isMagnifierAdded = true;
        mWordMagnifier.prepareForShow();
        tryShowMagnifier(mCurrentMotionEvent);
        return true;
    }

    OnTouchListener mExercisePanelTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mCurrentMotionEvent = MotionEvent.obtain(event);
                    break;
                case MotionEvent.ACTION_MOVE:
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
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            mWordMagnifier.showTouchRegion(getWindow().getDecorView(), x, y);
        }
    }

    private void tryHideMagnifier() {
        if (isMagnifierAdded) {
            mWordMagnifier.hideMagnifier();
            isMagnifierAdded = false;
        }
    }
}
