package com.yuzhi.fine.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.activity.BaseActivity;
import com.yuzhi.fine.common.AppContext;


/**
 * Created by tiansj on 15/7/29.
 */
public class Utils {

    private static final String TAG = "Utils";

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }


    /**
     * 设置手机网络类型，wifi，cmwap，ctwap，用于联网参数选择
     *
     * @return
     */
    static String getNetworkType() {
        String networkType = "wifi";
        ConnectivityManager manager = (ConnectivityManager) AppContext.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            return ""; // 当前网络不可用
        }

        String info = netWrokInfo.getExtraInfo();
        if ((info != null)
                && ((info.trim().toLowerCase().equals("cmwap"))
                || (info.trim().toLowerCase().equals("uniwap"))
                || (info.trim().toLowerCase().equals("3gwap")) || (info
                .trim().toLowerCase().equals("ctwap")))) {
            // 上网方式为wap
            if (info.trim().toLowerCase().equals("ctwap")) {
                // 电信
                networkType = "ctwap";
            } else {
                networkType = "cmwap";
            }
        }
        return networkType;
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static Dialog dialogWithActions(Context context, String title, String message, String confirmText, String cancelText, final View.OnClickListener cancelListener, final View.OnClickListener confirmListener) {
        try {
            final Dialog dialog = new Dialog(context, R.style.dialogNoBack);

            dialog.setContentView(R.layout.ios_7_dialog_two_actinos);
            View dialogView = dialog.findViewById(R.id.dialog);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialogView.getLayoutParams();
            int width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            layoutParams.width = width - width / 10;
            dialogView.setLayoutParams(layoutParams);
            ((TextView) dialog.findViewById(R.id.message)).setText(message);
            ((TextView) dialog.findViewById(R.id.title)).setText(title);
            final TextView confirm = (TextView) dialog.findViewById(R.id.confirm);
            confirm.setText(confirmText);
            final TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
            cancel.setText(cancelText);
            cancel.setTextColor(Color.parseColor("#FF007AFF"));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (cancelListener != null)
                        cancelListener.onClick(null);
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (confirmListener != null)
                        confirmListener.onClick(null);
                }
            });
            dialog.show();
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }
    public static Dialog dialogWithConfirmAction(Context context, String title, String message, String confirmText, final View.OnClickListener listener) {
        try {
            final Dialog dialog = new Dialog(context, R.style.dialogNoBack);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.ios_7_dialog);
            View dialogView = dialog.findViewById(R.id.dialog);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialogView.getLayoutParams();
            int width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            layoutParams.width = width - width / 10;
            dialogView.setLayoutParams(layoutParams);
            ((TextView) dialog.findViewById(R.id.message)).setText(message);
            ((TextView) dialog.findViewById(R.id.title)).setText(title);
            final TextView confirm = (TextView) dialog.findViewById(R.id.confirm);
            confirm.setText(confirmText);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (listener != null)
                        listener.onClick(null);
                }
            });
            dialog.show();
            return dialog;
        } catch (Exception exception) {
            return null;
        }
    }
}
