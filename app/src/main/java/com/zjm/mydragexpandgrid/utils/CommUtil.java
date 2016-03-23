package com.zjm.mydragexpandgrid.utils;

import android.content.Context;
import android.util.Log;

public class CommUtil {

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void Log(String msg) {
        Log.i("abc", msg == null ? "null" : msg);
    }

}
