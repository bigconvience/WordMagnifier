package com.example.word_magnifier.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.word_magnifier.R;

/**
 * Created by Administrator on 2015/5/9.
 */
public class ExercisePanel extends RelativeLayout {
    private TextView mEnglishTextView;

    public ExercisePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExercisePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.exercise_panel_item, this, true);
        mEnglishTextView = (TextView) findViewById(R.id.english_sentence);
        init();
    }

    private void init() {

    }
}
