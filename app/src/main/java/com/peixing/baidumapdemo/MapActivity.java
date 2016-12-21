package com.peixing.baidumapdemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends BaseActivity {
    private static final String TAG = "MapActivity";

    //地图属性
    MapStatusUpdate mapStatusUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);

        final BaiduMap mBaiduMap = baiduMap.getMap();
        //设置地图类型
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
       /* //卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //空白地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);*/
        //设置试试交通图
        mBaiduMap.setTrafficEnabled(true);
        //开启百度城市热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        //设置地图缩放级别

        mapStatusUpdate = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        //设置中心点
        double latitude = 40.050966;
        double longitude = 116.303128;
        LatLng latLng = new LatLng(latitude, longitude);
        mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);


      /*  //定义Malrker坐标点
        LatLng point = new LatLng(39.96, 116.40);
        //构建图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        //构建MarkerOption,用于在地图上添加标记
//        OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);
        //设置标记点可拖拽
        OverlayOptions options = new MarkerOptions().position(point).icon(bitmap).zIndex(9).draggable(true);
        //添加标记在地图上
        mBaiduMap.addOverlay(options);*/

        //地图双击监听事件
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            @Override
            public void onMapDoubleClick(LatLng latLng) {

            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.clear();
                //构建图标

                //绘制原型覆盖物
                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(500).fillColor(0x300dc3b0).stroke(new Stroke(5, 0x600000ff));
//                circleOptions.center(latLng).radius(1000)
//                        .fillColor(0xc3d0be)
//                        .stroke(new Stroke(5, 0x600000ff));
                mBaiduMap.addOverlay(circleOptions);

                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
                //构建MarkerOption,用于在地图上添加标记
//                OverlayOptions options = new MarkerOptions().position(latLng).icon(bitmap).zIndex(9).draggable(true);
                MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmap).zIndex(0).period(10);
                options.animateType(MarkerOptions.MarkerAnimateType.grow);
                //添加标记在地图上
                mBaiduMap.addOverlay(options);


                Log.i(TAG, "onMapClick: 经度是:" + latLng.longitude + ",纬度是:" + latLng.latitude);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                mBaiduMap.clear();
                //构建图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
                //构建MarkerOption,用于在地图上添加标记
                OverlayOptions options = new MarkerOptions().position(mapPoi.getPosition()).icon(bitmap).zIndex(9).draggable(true);
                //添加标记在地图上
                mBaiduMap.addOverlay(options);

                //绘制原型覆盖物
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(mapPoi.getPosition()).radius(500)
                        .fillColor(0x300dc3b0)
                        .stroke(new Stroke(5, 0x600000ff));
                mBaiduMap.addOverlay(circleOptions);
                //文字覆盖物
                TextOptions textOptions = new TextOptions()
                        .text(mapPoi.getName())
                        .position(mapPoi.getPosition())
                        .fontSize(35)
                        .typeface(Typeface.SERIF)
                        .fontColor(0x60ff0000).visible(true);
                mBaiduMap.addOverlay(textOptions);


                Log.i(TAG, "onMapPoiClick: 名字是:" + mapPoi.getName() + ",经度是:" + mapPoi.getPosition().longitude + ",纬度是:" + mapPoi.getPosition().latitude);
                return true;
            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                return true;
            }
        });

        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker marker) {
                //在拖拽过程中
//                float x = marker.getAnchorX();
//                float y = marker.getAnchorY();
//                Log.i(TAG, "onMarkerDragEnd: " + x + "," + y);
//                LatLng latlng = marker.getPosition();
//                Log.i(TAG, "onMarkerDrag: "+latlng.latitude+","+latlng.longitude);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
                LatLng latlng = marker.getPosition();
                Log.i(TAG, "onMarkerDragEnd: 经度:" + latlng.longitude + ",纬度:" + latlng.latitude);
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                //拖拽开始
//                float x = marker.getAnchorX();
//                float y = marker.getAnchorY();
//                Log.i(TAG, "onMarkerDragStart: " + x + "," + y);
                LatLng latlng = marker.getPosition();
                Log.i(TAG, "onMarkerDragStart: 经度:" + latlng.longitude + ",纬度:" + latlng.latitude);
            }
        });

    }
}
