package com.yuzhi.fine.activity;

import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.yuzhi.fine.R;

/**
 * Created by lemon on 2016/5/30.
 */
public class MapActivity extends BaseActivity {
    private MapView mMapView = null;
    private BaiduMap map ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
    }

}
