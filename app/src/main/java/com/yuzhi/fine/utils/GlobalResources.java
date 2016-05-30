package com.yuzhi.fine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by lemon on 2016/4/2.
 */
public class GlobalResources {
    private static final GlobalResources INSTANCE = new GlobalResources();

    private static final String PREFERENCE = "com.ingageapp.preference";
    private static final String PREFERENCE_FOR_EVER = "com.ingageapp.preference.for.ever";

    private GlobalResources() {

    }

    public static GlobalResources getInstance() {
        return INSTANCE;
    }

    private SharedPreferences globalSettings;
    private SharedPreferences globalSettingsForEver;

    public static boolean debug = true;




    private String mPhoneId;

    private String mLocal;

    private ClassLoader mClassLoader;

    private float mScreenDensity;

    private boolean mRealDisplay;

    private Context mContext;

    /**
     * @param context must be the application context
     */
    public void load(Context context) {
        mContext = context;
        globalSettings = context.getSharedPreferences(PREFERENCE, Context.MODE_MULTI_PROCESS);
        try {
            mPhoneId = SecurityUtils.MD5.encrypt(android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(mPhoneId)) {
            mPhoneId = "androidEmulator";
        }

        mLocal = context.getResources().getConfiguration().locale.getCountry();

        mClassLoader = context.getClassLoader();

        SensorManager mManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> mList = mManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        mRealDisplay = mList == null || mList.size() == 0;

        mScreenDensity = context.getResources().getDisplayMetrics().density;
    }

    public SharedPreferences globalPreferences() {
        globalSettings = mContext.getSharedPreferences(PREFERENCE, Context.MODE_MULTI_PROCESS);
        return globalSettings;
    }

    public SharedPreferences preferencesForEver() {
        globalSettingsForEver = mContext.getSharedPreferences(PREFERENCE_FOR_EVER, Context.MODE_PRIVATE);
        return globalSettingsForEver;
    }

//    DBSharedPreference globalDBSettings;
//
//    public DBSharedPreference globalDBPreferences() {
//        globalDBSettings = new DBSharedPreference(mContext);
//        return globalDBSettings;
//    }

    public String phoneId() {
        return mPhoneId;
    }

    public String local() {
        return mLocal;
    }

    public ClassLoader classLoader() {
        return mClassLoader;
    }

    public float screenDensity() {
        return mScreenDensity;
    }

    public boolean realDisplay() {
        return mRealDisplay;
    }
}
