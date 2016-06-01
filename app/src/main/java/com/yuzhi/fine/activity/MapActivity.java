package com.yuzhi.fine.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yuzhi.fine.R;

/**
 * Created by lemon on 2016/5/30.
 */
public class MapActivity extends BaseActivity {
    private MapView mMapView = null;
    private BaiduMap map;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
        if (getIntent() != null) {
            address = getIntent().getStringExtra("address");
        }
        init();
    }

    public void init() {
        final BaiduMap map = mMapView.getMap();
        final GeoCodeOption option = new GeoCodeOption();
        option.address(address);
        option.city(" ");
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.geocode(option);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                LatLng latLng = geoCodeResult.getLocation();
                Resources resources = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.maker);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(bitmapDescriptor);
                Marker marker = (Marker) map.addOverlay(markerOptions);
//                Bundle bundle = new Bundle();
//                bundle.putString("info", address);
//                marker.setExtraInfo(bundle);
                map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        InfoWindow infoWindow;
                        TextView location = new TextView(getApplicationContext());
                        location.setBackgroundResource(R.drawable.location_tips);
                        location.setPadding(30,20,30,50);
                        location.setText(address);
                        final LatLng ll = marker.getPosition();
                        Point p = map.getProjection().toScreenLocation(ll);
                        p.y -= 47;
                        LatLng llInfo = map.getProjection().fromScreenLocation(p);
                        BitmapDescriptor loc_descriptor = BitmapDescriptorFactory.fromView(location);
                        infoWindow = new InfoWindow(loc_descriptor, llInfo, 0, new InfoWindow.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick() {
                                map.hideInfoWindow();
                            }
                        });
                        map.showInfoWindow(infoWindow);
                        return true;
                    }
                });
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng,18);
                map.animateMapStatus(mapStatusUpdate);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });

        map.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
