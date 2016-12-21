package com.peixing.baidumapdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.peixing.baidumapdemo.overlayutils.PoiOverlay;

import java.util.List;

public class PoiSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_poi_search);

        search();


    }

    private void search() {
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new MyOnGetPoiSearchResultListener());

      /*  PoiBoundSearchOption option = new PoiBoundSearchOption();
     LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(40.049233, 116.302675))// 左下
                .include(new LatLng(40.050645, 116.303695))// 左下
                .build();*/
//        option.bound(bounds);
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.location(hmPos);
        option.radius(1000);
        option.keyword("公共厕所");
//        poiSearch.searchInBound(option);
        poiSearch.searchNearby(option);

    }

    class MyOnGetPoiSearchResultListener implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == poiResult.error) {
                Toast.makeText(getApplicationContext(), "not found!", Toast.LENGTH_LONG).show();
                return;
            }
            PoiOverlay poiOverlay = new MyPoiOverlay(mbaiduMap);
            mbaiduMap.setOnMarkerClickListener(poiOverlay);
            poiOverlay.setData(poiResult);
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();    //缩放地图
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    }


    class MyPoiOverlay extends PoiOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {

            PoiResult poiResult = getPoiResult();
            List<PoiInfo> allInfo = poiResult.getAllPoi();
            PoiInfo poiInfo = allInfo.get(i);
            String text = poiInfo.city + ":" + poiInfo.address;
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            return super.onPoiClick(i);
        }
    }
}
