package com.peixing.baidumapdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.peixing.baidumapdemo.overlayutils.DrivingRouteOverlay;
import com.peixing.baidumapdemo.overlayutils.PoiOverlay;
import com.peixing.baidumapdemo.overlayutils.TransitRouteOverlay;
import com.peixing.baidumapdemo.overlayutils.WalkingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

public class MyLocationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MyLocationActivity";

    private Button btTraffic;
    private Button btSatellite;
    private Button btMyloc;
    private LinearLayout llRoute;
    private Button btDrive;
    private Button btTransit;
    private Button btWalk;
    private MapView mapView;
    //用户指定位置
    LatLng mPoiPosition;

    BaiduMap mbaiduMap;

    boolean isFirstLoc = true; // 是否首次定位

    /**
     * 路线规划搜索器
     */
    private RoutePlanSearch mRoutePlanSearch;
    /**
     * POI搜索器
     */
    private PoiSearch mPoiSearch;

    private final int SDK_PERMISSION_REQUEST = 127;
    //定位服务
    public LocationClient mLocationClient;

    //定义我的位置
    LatLng myLatlng;
    //位置精确范围
    float LocRadius;
    //POI查询
    PoiSearch poiSearch;

    @TargetApi(23)
    private void getPermission() {
        //判断当前系统版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            /**
             * 定位权限必须给予，如果用户禁止，则每次进入都会申请
             */
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},SDK_PERMISSION_REQUEST);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},SDK_PERMISSION_REQUEST);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (permissions.size() > 0) {
                Log.i(TAG, "getPermission: 获取权限");
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            } else {
                //定位方法执行 6.0以上
                Log.i(TAG, "getPermission: 6.0以上");
                initLocation();
            }
        } else {
            //低于6.0,开始定位
            Log.i(TAG, "getPermission: 6.0以下");
            initLocation();
        }
    }

/*
    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        Log.i(TAG, "addPermission: 弹出获取权限窗口");
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }*/

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //权限允许后，开始定位
        initLocation();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_location);


//         BaiduMap mBaiduMap = baiduMap.getMap();
//        //设置地图类型
//        //普通地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(MyLocationListener);

        initView();

        //针对android M的动态获取权限进行完善
        getPermission();

//        initLocation();

        initSearch();

    }

    private void initSearch() {
//        poiSearch = PoiSearch.newInstance();
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(mOnGetPoiSearchResultListener);
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(mOnGetRoutePlanResultListener);
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        btTraffic = (Button) findViewById(R.id.bt_traffic);
        btSatellite = (Button) findViewById(R.id.bt_satellite);
        btMyloc = (Button) findViewById(R.id.bt_myloc);
        llRoute = (LinearLayout) findViewById(R.id.ll_route);
        btDrive = (Button) findViewById(R.id.bt_drive);
        btTransit = (Button) findViewById(R.id.bt_transit);
        btWalk = (Button) findViewById(R.id.bt_walk);
        //地图形式切换
        btTraffic.setOnClickListener(this);
        btSatellite.setOnClickListener(this);
        btMyloc.setOnClickListener(this);

        //线路规划界面
        btDrive.setOnClickListener(this);
        btWalk.setOnClickListener(this);
        btTransit.setOnClickListener(this);

        mbaiduMap = mapView.getMap();
        //  设置地图的点击事件
        mbaiduMap.setOnMapClickListener(onMapClickListener);

        mbaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {

        //配置定位模式编码等方式
        LocationClientOption options = new LocationClientOption();
        options.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        options.setCoorType("bd0911");// 可选，默认gcj02，设置返回的定位结果坐标系 bd0911
        options.setScanSpan(6000);  //刷新频率
        mLocationClient.setLocOption(options);
//        Log.i(TAG, "initLocation: 开启定位");
        //定位图层点击监听事件
        mbaiduMap.setOnMyLocationClickListener(myLocationListener);


        //打开定位图层
        mbaiduMap.setMyLocationEnabled(true);
        //设置显示的定位图层配置
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mbaiduMap.setMyLocationConfigeration(config);

        MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(16);
        mbaiduMap.setMapStatus(status);
        //开启定位功能
        mLocationClient.start();
    }

    /**
     * 定位监听
     */
    public BDLocationListener MyLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mbaiduMap == null) {
                return;
            }
            //构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            //我的位置
            myLatlng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            //位置精确度
            LocRadius = bdLocation.getRadius();
            Log.i(TAG, "onReceiveLocation: 纬度:" + bdLocation.getLatitude() + ",经度是:" + bdLocation.getLongitude() + ",精度:" + LocRadius);
/*
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
            //构建MarkerOption,用于在地图上添加标记
            MarkerOptions options = new MarkerOptions().position(myLatlng).icon(bitmap)
                    .zIndex(8)      //图片层级
                    .period(100);    //刷新时间
            options.animateType(MarkerOptions.MarkerAnimateType.none);
            //添加标记在地图上
            mbaiduMap.addOverlay(options);*/
            mbaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(myLatlng).zoom(16);
                mbaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                //定位设置模式修改
//            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
//            mbaiduMap.setMyLocationConfigeration(config);
            }
        }
    };


    private BaiduMap.OnMyLocationClickListener myLocationListener = new BaiduMap.OnMyLocationClickListener() {

        @Override
        public boolean onMyLocationClick() {
            Toast.makeText(getApplicationContext(), "精确到：" + LocRadius + "米",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
    };


    /**
     * 地图图层点击事件
     */
    private BaiduMap.OnMapClickListener onMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            mbaiduMap.clear();

            //路线规划
            llRoute.setVisibility(View.GONE);

            //构建图标

            //绘制原型覆盖物
            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(500).fillColor(0x300dc3b0).stroke(new Stroke(5, 0x600000ff));
//                circleOptions.center(latLng).radius(1000)
//                        .fillColor(0xc3d0be)
//                        .stroke(new Stroke(5, 0x600000ff));
            mbaiduMap.addOverlay(circleOptions);

            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
            //构建MarkerOption,用于在地图上添加标记
//                OverlayOptions options = new MarkerOptions().position(latLng).icon(bitmap).zIndex(9).draggable(true);
            MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmap).zIndex(0).period(10);
            options.animateType(MarkerOptions.MarkerAnimateType.grow);
            //添加标记在地图上
            mbaiduMap.addOverlay(options);
            Log.i(TAG, "onMapClick: 经度是:" + latLng.longitude + ",纬度是:" + latLng.latitude);
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            mbaiduMap.clear();
            mPoiPosition = mapPoi.getPosition();
            //构建图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
            //构建MarkerOption,用于在地图上添加标记
            OverlayOptions options = new MarkerOptions().position(mapPoi.getPosition()).icon(bitmap).zIndex(9).draggable(true);
            //添加标记在地图上
            mbaiduMap.addOverlay(options);

            //绘制原型覆盖物
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(mapPoi.getPosition()).radius(500)
                    .fillColor(0x300dc3b0)
                    .stroke(new Stroke(5, 0x600000ff));
            mbaiduMap.addOverlay(circleOptions);
            //文字覆盖物
            TextOptions textOptions = new TextOptions()
                    .text(mapPoi.getName())
                    .position(mapPoi.getPosition())
                    .fontSize(35)
                    .typeface(Typeface.SERIF)
                    .fontColor(0x60ff0000).visible(true);
            mbaiduMap.addOverlay(textOptions);

            //路线规划
            llRoute.setVisibility(View.VISIBLE);

            Log.i(TAG, "onMapPoiClick: 名字是:" + mapPoi.getName() + ",经度是:" + mapPoi.getPosition().longitude + ",纬度是:" + mapPoi.getPosition().latitude);
            return true;
        }
    };
    /**
     * 某一点具体搜索监听
     */
    private OnGetPoiSearchResultListener mOnGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(final PoiResult poiResult) {
            if (poiResult == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == poiResult.error) {
                Toast.makeText(getApplicationContext(), "未查询到结果", Toast.LENGTH_SHORT).show();
                return;
            }
            //清楚之前的标记
            mbaiduMap.clear();

            //重新创建覆盖物
            PoiOverlay poiOverlay = new PoiOverlay(mbaiduMap) {
                @Override
                public boolean onPoiClick(int i) {
                    List<PoiInfo> allPoi = poiResult.getAllPoi();
                    PoiInfo poiInfo = allPoi.get(i);
//                    poiSearch.searchPoiDetail(new　PoiDetailSearchOption()
//                            .poiUid(poiInfo.uid));
                    poiSearch.searchPoiDetail(new PoiDetailSearchOption()
                            .poiUid(poiInfo.uid));
                    Toast.makeText(getApplicationContext(),
                            "查询“" + poiInfo.name + "”详情信息", Toast.LENGTH_SHORT)
                            .show();
                    return true;
                }
            };
            //设置数据
            poiOverlay.setData(poiResult);
            //添加到地图
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();
            mbaiduMap.setOnMarkerClickListener(poiOverlay);


        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == poiDetailResult.error) {
                Toast.makeText(getApplicationContext(), "未查询到结果", Toast.LENGTH_SHORT).show();
                return;
            }
            String text = "DetailUri：" + poiDetailResult.getDetailUrl();
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };


    /**
     * 路线规划监听回调
     */
    private OnGetRoutePlanResultListener mOnGetRoutePlanResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MyLocationActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(mbaiduMap);
                for (WalkingRouteLine walkingRouteLine : walkingRouteResult.getRouteLines()) {
                    for (WalkingRouteLine.WalkingStep walkingStep : walkingRouteLine.getAllStep()) {
                        Log.i(TAG, "onGetWalkingRouteResult: " + walkingStep.getInstructions());
                    }
                    Log.i(TAG, "onGetWalkingRouteResult: ---------");
                }


                overlay.setData(walkingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mbaiduMap.setOnMarkerClickListener(overlay);

            }
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MyLocationActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                TransitRouteOverlay overlay = new TransitRouteOverlay(mbaiduMap);
                for (TransitRouteLine transitRouteLine : transitRouteResult.getRouteLines()) {

                    for (TransitRouteLine.TransitStep step : transitRouteLine.getAllStep()) {

                        Log.i(TAG, "onGetTransitRouteResult: " + step.getInstructions());
                    }
                    Log.i(TAG, "onGetTransitRouteResult: --------------");
                }
                overlay.setData(transitRouteResult.getRouteLines().get(0));
//                Log.i(TAG, "onGetTransitRouteResult: " + transitRouteResult.getRouteLines().get(0).getDistance());

                List<TransitRouteLine.TransitStep> steps = transitRouteResult.getRouteLines().get(0).getAllStep();
                for (TransitRouteLine.TransitStep step : steps) {

                    Log.i(TAG, "onGetTransitRouteResult: " + step.getInstructions());
                }
                overlay.addToMap();
                overlay.zoomToSpan();
                mbaiduMap.setOnMarkerClickListener(overlay);
            }
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            //跨城公交
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //  处理驾车路线
            if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MyLocationActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mbaiduMap);
                for (DrivingRouteLine drivingRouteLine : drivingRouteResult.getRouteLines()) {
                    for (DrivingRouteLine.DrivingStep drivingStep : drivingRouteLine.getAllStep()) {
                        Log.i(TAG, "onGetDrivingRouteResult: " + drivingStep.getInstructions());
                    }
                    Log.i(TAG, "onGetDrivingRouteResult: -------------");
                }

                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mbaiduMap.setOnMarkerClickListener(overlay);
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_satellite:
                mbaiduMap.setMapType(mbaiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE
                        ? BaiduMap.MAP_TYPE_NORMAL
                        : BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.bt_traffic:
//                mbaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mbaiduMap.setTrafficEnabled(!mbaiduMap.isTrafficEnabled());
                break;
            case R.id.bt_myloc:
                //获取位置
//                getPermission();
                break;
            case R.id.bt_walk:
                mbaiduMap.clear();
                mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(
                        PlanNode.withLocation(myLatlng)).to(
                        PlanNode.withLocation(mPoiPosition)));
                Toast.makeText(getApplicationContext(), "正在规划步行路线。。。。",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_transit:
                mbaiduMap.clear();
                mRoutePlanSearch.transitSearch(new TransitRoutePlanOption()
                        .city("北京")
                        .from(PlanNode.withLocation(myLatlng))
                        .to(PlanNode.withLocation(mPoiPosition)).policy(TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST));
                Toast.makeText(getApplicationContext(), "正在规划公交路线。。。。",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_drive:
                mbaiduMap.clear();
                DrivingRoutePlanOption option = new DrivingRoutePlanOption();
                PlanNode stNode = PlanNode.withLocation(myLatlng);
                PlanNode enNode = PlanNode.withLocation(mPoiPosition);
                option.from(stNode).to(enNode);
                option.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
                mRoutePlanSearch.drivingSearch(option);
                Toast.makeText(getApplicationContext(), "正在规划驾车路线。。。。",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onResume() {
        //  MapView 声明周期
        mapView.onResume();
        //  LocationClient 开启
        mLocationClient.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //  MapView 声明周期
        mapView.onPause();
        //  LocationClient 停止
        mLocationClient.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //  MapView 声明周期
        mapView.onDestroy();
        //  注销SDK初始化监听器和我的位置监听
//        unregisterReceiver(mSDKInitReceiver);
        mLocationClient.unRegisterLocationListener(MyLocationListener);
        //  释放搜索器
        mPoiSearch.destroy();
//        mRoutePlanSearch.destroy();
        super.onDestroy();
    }
}
