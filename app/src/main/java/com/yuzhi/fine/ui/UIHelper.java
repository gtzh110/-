package com.yuzhi.fine.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.yuzhi.fine.activity.LoginActivity;
import com.yuzhi.fine.activity.MainActivity;
import com.yuzhi.fine.activity.MapActivity;
import com.yuzhi.fine.activity.RegisterActivity;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 */
public class UIHelper {

	public final static String TAG = "UIHelper";

	public final static int RESULT_OK = 0x00;
	public final static int REQUEST_CODE = 0x01;

	public static void ToastMessage(Context cont, String msg) {
        if(cont == null || msg == null) {
            return;
        }
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
        if(cont == null || msg <= 0) {
            return;
        }
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
        if(cont == null || msg == null) {
            return;
        }
		Toast.makeText(cont, msg, time).show();
	}

    public static void showHome(Activity context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void showLogin(Activity context,String identity){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("TAG",identity);
        context.startActivity(intent);
    }
    public static void showRegister(Activity context){
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
    public static void showMap(Activity context){
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }




}
