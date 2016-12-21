package com.peixing.baidumapdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.peixing.baidumapdemo.overlayutils.DrivingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

public class DrivingSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search();
    }

    private void search() {
        RoutePlanSearch planSearch = RoutePlanSearch.newInstance();
        planSearch.setOnGetRoutePlanResultListener(new MyOnGetRoutePlanResultListener());
        DrivingRoutePlanOption option = new DrivingRoutePlanOption();
        PlanNode from = PlanNode.withLocation(hmPos);
        PlanNode to = PlanNode.withLocation(new LatLng(40.0634, 116.3498));
        option.from(from);
        option.to(to);
        List<PlanNode> nodes = new ArrayList<>();
        nodes.add(PlanNode.withCityNameAndPlaceName("北京", "龙泽"));
        option.passBy(nodes);
        planSearch.drivingSearch(option);

    }

    class MyOnGetRoutePlanResultListener implements OnGetRoutePlanResultListener {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == drivingRouteResult.error) {
                Toast.makeText(getApplicationContext(), "not found!", Toast.LENGTH_LONG).show();
            }
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mbaiduMap);
            mbaiduMap.setOnMarkerClickListener(overlay);
            DrivingRouteLine line = drivingRouteResult.getRouteLines().get(0);
            overlay.setData(line);
            overlay.addToMap();
            overlay.zoomToSpan();
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    }

    class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
         */
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }
}
