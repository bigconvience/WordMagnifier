package com.example.word_magnifier.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2015/5/10.
 */
public class DisplayUtils {
    private static DisplayMetrics sDisplayMetrics = null;

    /**
     * 初始化操作
     */
    public static void init(Context context) {
        sDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度 单位：像素
     * @return 屏幕宽度
     */
    public static int getWidthPixels() {
        return sDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度 单位：像素
     * @return 屏幕高度
     */
    public static int getHeightPixels() {
        return sDisplayMetrics.heightPixels;
    }
}
