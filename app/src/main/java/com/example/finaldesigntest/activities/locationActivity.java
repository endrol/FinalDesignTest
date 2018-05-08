package com.example.finaldesigntest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.finaldesigntest.R;
import com.example.finaldesigntest.helper.serverConstant;
import com.example.finaldesigntest.helper.socketHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class locationActivity extends BaseActivity implements View.OnClickListener {

    private String recordFilePath;
    private String TAG = "locationActivity.this";

    public LocationClient mLocationClient;
    private MapView mapView;

    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;

    private String timeSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);
        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        Intent intent = getIntent();
        String filePath = intent.getStringExtra("filePath");
//        setTime = intent.getIntArrayExtra("setTime");
        int setTimeHour = intent.getIntExtra("setTimeHour",0);
        int setTimeMinute = intent.getIntExtra("setTimeMinute",0);
        timeSet = "hour:"+setTimeHour+".minute:"+setTimeMinute+".";

        recordFilePath = filePath;
        findViewById(R.id.send_file).setOnClickListener(this);
//
//        findViewById(R.id.button1).setOnClickListener(this);
//        findViewById(R.id.button2).setOnClickListener(this);

        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(locationActivity.this, Manifest.
        permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(locationActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(locationActivity.this,
                    permissions,1);
        }else{
            requestLocation();
        }


    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("纬度: ").append(bdLocation.getLatitude()).append("\n");
//            currentPosition.append("经线: ").append(bdLocation.getLongitude()).append("\n");
//            currentPosition.append("country: ").append(bdLocation.getCountry()).append("\n");
            nowCity = bdLocation.getCity();
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
                    bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
        }
    }

    /************ojbk****/
    private String nowCity = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_file:
                socketHelper.getSocketHelper().setThisSocket(true);
                uploadFile();
                break;

//            case R.id.button1:
//                Intent intent1 = new Intent(this,requestActivity.class);
//                startActivity(intent1);
//                finish();
//                break;
//
//            case R.id.button2:
//                Intent intent2 = new Intent(this,RTSPActivity.class);
//                startActivity(intent2);
//                finish();
//                break;
        }
    }



    private void uploadFile(){
        new Thread(){
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    String requestUrl = serverConstant.uploadUrl;
                    File uploadFile = new File(recordFilePath);
                    RequestBody fileBody = RequestBody.create(
                            MediaType.parse("application/octet-stream"),uploadFile);
                    //带参数传输
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
            //        builder.addFormDataPart("fileName",recordFilePath);
                    builder.addFormDataPart("setTime",timeSet);
                    /**************/
                    builder.addFormDataPart("city",nowCity);
                    /**************/
                    builder.addFormDataPart("file",uploadFile.getName(),fileBody);
                    builder.build();
                    //timeMills为文件名
                    Request request = new Request.Builder()
                            .url(requestUrl)
                            .post(builder.build())
                            .build();
//
//                    client.newCall(request).execute();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG,"this is response ok"+responseData);
                    showResponse(responseData);
//
//                    Toast.makeText(MyApplication.getContext(),responseData,
//                            Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {//进行ui操作
                Toast.makeText(locationActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
