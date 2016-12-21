package com.peixing.baidumapdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private RelativeLayout activityMain;
    private MapView baiduMap;
    private TextView textInfo;
    private ListView listview;
    private Button searchDrive;
    private Button locate;
    private Button btNormal;
    private Button searchArea;
    //地图属性
    MapStatusUpdate mapStatusUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏颜色随着activity设置颜色渐变
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);

        btNormal = (Button) findViewById(R.id.bt_normal);
        searchArea = (Button) findViewById(R.id.search_area);


        locate = (Button) findViewById(R.id.locate);

        searchDrive = (Button) findViewById(R.id.search_drive);

//        activityMain = (RelativeLayout) findViewById(R.id.activity_main);
        baiduMap = (MapView) findViewById(R.id.baidu_Map);
//        textInfo = (TextView) findViewById(R.id.text_Info);
//        listview = (ListView) findViewById(R.id.listview);
        btNormal.setOnClickListener(this);
        searchArea.setOnClickListener(this);
        searchDrive.setOnClickListener(this);
        locate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_normal:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
            case R.id.search_area:
                startActivity(new Intent(MainActivity.this, PoiSearchActivity.class));
                break;
            case R.id.search_drive:
                startActivity(new Intent(MainActivity.this, DrivingSearchActivity.class));
                break;
            case R.id.locate:
                startActivity(new Intent(MainActivity.this, MyLocationActivity.class));
                break;
        }
    }
}
