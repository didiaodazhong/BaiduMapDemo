package com.peixing.baidumapdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class BaseActivity extends Activity {
    protected MapView baiduMap;
    protected BaiduMap mbaiduMap;
    protected MapStatusUpdate mapstatus;
    protected double latitude = 40.050966;// 纬度
    protected double longitude = 116.303128;// 经度
    protected LatLng hmPos = new LatLng(latitude, longitude);// 黑马

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common);
        init();
    }

    /**
     * 初始化地图
     */
    private void init() {


        baiduMap = (MapView) findViewById(R.id.baidu_Map);
        mbaiduMap = baiduMap.getMap();
        //设置默认缩放级别
        mapstatus = MapStatusUpdateFactory.zoomTo(16);
        mbaiduMap.setMapStatus(mapstatus);


    }



    @Override
    protected void onResume() {
        baiduMap.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        baiduMap.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        baiduMap.onDestroy();
        super.onDestroy();
    }
}
