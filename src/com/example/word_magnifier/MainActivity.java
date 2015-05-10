package com.example.word_magnifier;

import android.app.Activity;
import android.os.Bundle;
import com.example.word_magnifier.utils.DisplayUtils;

public class MainActivity extends Activity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayUtils.init(getBaseContext());
    }
}
