package com.example.word_magnifier.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


/**
 * author jacksgong
 */
public class WordView extends EditText {
    private final static String TAG = "WordView";
    private SpannableString mSpannableString;

    public WordView(Context context) {
        super(context);
        initialize();
    }

    public WordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        //不做任何处理，为了阻止长按的时候弹出上下文菜单
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    private float mLastX = -1f;
    private float mLastY = -1f;

    private final float MIN_VALID_MOVE = 3f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Layout layout = getLayout();
        int line = 0;
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.MAGENTA);

        do {
            if (mWords == null) {
                break;
            }

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = -1f;
                    mLastY = -1f;

                    break;
                case MotionEvent.ACTION_MOVE:
                    clearSpan();
                    if (Math.abs(event.getX() - mLastX) > MIN_VALID_MOVE || Math.abs(event.getY() - mLastY) > MIN_VALID_MOVE) {
                        mLastX = event.getX();
                        mLastY = event.getY();

                        Log.d(TAG, "x: " + event.getX() + ", y: " + event.getY());
                        line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                        final int index = layout.getOffsetForHorizontal(line, (int) event.getX());
                        Log.d(TAG, "line: " + line + " string index: " + index);
                        Word selectedWord = getWord(index);

                        if (selectedWord != null) {
                            Log.d(TAG, "selected word: " + selectedWord);
                            //Selection.setSelection(getEditableText(), selectedWord.getStart(), selectedWord.getEnd());
                            mSpannableString.setSpan(foregroundColorSpan,
                                    selectedWord.getStart(), selectedWord.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            setText(mSpannableString);
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    mLastX = -1f;
                    mLastY = -1f;
                    //Selection.removeSelection(getEditableText());
                    mSpannableString.removeSpan(foregroundColorSpan);
                    break;
            }

        } while (false);

        return false;
    }

    private void clearSpan() {
        ForegroundColorSpan[] spans = mSpannableString.getSpans(0, getText().length(), ForegroundColorSpan.class);
        for(int i=0; i<spans.length; i++){
            mSpannableString.removeSpan(spans[i]);
        }
    }

    private Word getWord(final int index) {
        if (mWords == null) {
            return null;
        }

        for (Word w : mWords) {
            if (w.isIn(index)) {
                return w;
            }
        }

        return null;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mSpannableString = SpannableString.valueOf(getEditableText());
        mWords = getWords(text);
    }


    private List<Word> mWords;

    public List<Word> getWords(CharSequence s) {

        if (s == null) {
            return null;
        }

        List<Word> result = new ArrayList<Word>();

        int start = -1;

        int i = 0;

        for (; i < s.length(); i++) {
            char c = s.charAt(i);


            if (c == ' ' || !Character.isLetter(c)) {
                if (start != -1) {
                    result.add(new Word(start, i));// From ( 0, 4 )
                }
                start = -1;
            } else {
                if (start == -1) {
                    start = i;
                }
            }

        }

        if (start != -1) {
            result.add(new Word(start, i));
        }

        Log.d(TAG, result.toString());

        return result;

    }

    private class Word {
        public Word(final int start, final int end) {
            this.mStart = start;
            this.mEnd = end;
        }

        private int mStart;
        private int mEnd;

        public int getStart() {
            return this.mStart;
        }

        public int getEnd() {
            return this.mEnd;
        }

        public boolean isIn(final int index) {
            if (index >= getStart() && index <= getEnd()) {
                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            return "( " + getStart() + ", " + getEnd() + " )";
        }
    }
}

