package com.yuzhi.fine.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by lemon on 2016/4/2.
 */
public class LogUtils {
    static long startTime;
    private LogUtils() {
    }

    public static void e(String tag, String msg) {
        if (GlobalResources.debug && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)){
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (GlobalResources.debug)
            Log.e(tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        if (GlobalResources.debug)
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (GlobalResources.debug)
            Log.i(tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        if (GlobalResources.debug)
            Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (GlobalResources.debug)
            Log.d(tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        if (GlobalResources.debug)
            Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (GlobalResources.debug)
            Log.w(tag, msg, tr);
    }


    public static void setStartTime(){
        if (GlobalResources.debug){
            startTime = System.currentTimeMillis();
        }
    }
    public static void getTakeTime(String str){
        if(startTime != 0&&GlobalResources.debug){
            String takeTime = String.valueOf(System.currentTimeMillis() - startTime);
            if(str!=null){
                e("takeTime:",str+" = "+ takeTime);
            }else{
                e("takeTime:",takeTime);
            }
        }
    }
    public static void getTakeTimeAndStart(String str){
        getTakeTime(str);
        setStartTime();
    }
}
