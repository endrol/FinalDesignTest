package com.example.finaldesigntest.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.example.finaldesigntest.helper.locationHelper;

import java.util.HashMap;

public class checkLocationService extends Service {

    private LocationClient locationClient;
    public checkLocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        helper = new locationHelper();

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MylocationListener());
        SDKInitializer.initialize(getApplicationContext());
        requestLocation();
        //开启自检位置线程
        checkLocationThread locationThread = new checkLocationThread();
        locationThread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    private void requestLocation(){
        initLocation();
        locationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }
    public static locationHelper helper;


    private class checkLocationThread extends Thread{
        @Override
        public void run() {
            while(true){

                helper.setCity(nowLocation.get("city"));
                Log.e("checkLocationService","helper中的city = "+helper.getCity());
                helper.setLatitude(nowLocation.get("Latitude"));
                Log.e("checkLocationService","helper中的纬度 = "+helper.getLatitude());
                helper.setLongitude(nowLocation.get("Longitude"));
                Log.e("checkLocationService","helper中的经度 = "+helper.getLongitude());
                Log.e("checkLocationService","i am listening the city"+nowLocation);

                if(helper.getCity() != null){
                    helper.setCheckLocation(true);
                }else {
                    helper.setCheckLocation(false);
                }
                Log.e("checkLocationService","helper中的bool = "+helper.isCheckLocation());
                try {//每隔30秒获取当前位置信息
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private HashMap<String,String> nowLocation = new HashMap<String, String>();

    public class MylocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            nowLocation.put("Latitude",":"+bdLocation.getLatitude());
            nowLocation.put("Longitude",":"+bdLocation.getLongitude());
            nowLocation.put("city",bdLocation.getCity());

//            nowLocation = "纬度:"+bdLocation.getLatitude()+"&经度:"
//                    +bdLocation.getLongitude()+"&城市:"+bdLocation.getCity();


//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("纬度: ").append(bdLocation.getLatitude()).append("\n");
//            currentPosition.append("经度: ").append(bdLocation.getLongitude()).append("\n");
//            currentPosition.append("country: ").append(bdLocation.getCountry()).append("\n");
        }
    }

}
