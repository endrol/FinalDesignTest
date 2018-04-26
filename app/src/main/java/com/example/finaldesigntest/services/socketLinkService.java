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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class socketLinkService extends Service {

    private LocationClient locationClient;
    public socketLinkService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e("socketLinkService","zhe shi asdasd3");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    //有mainActivity启动的service
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("socketLinkService","zhe shi asdasd");
        Log.e("socketLinkService","zhe shi asdasd2");
        //开启线程
        ClientThread clientThread = new ClientThread();
        clientThread.start();
//
//        writeBuffer writeBuffer = new writeBuffer();
//        writeBuffer.start();

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MylocationListener());
        SDKInitializer.initialize(getApplicationContext());
        requestLocation();
        //开启自检位置线程
        checkLocationThread locationThread = new checkLocationThread();
        locationThread.start();
        //开启线程检查是否为本socket
        checkIsThisSocket thisSocket = new checkIsThisSocket();
        thisSocket.start();


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

    locationHelper helper = new locationHelper();

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
                    sendMeg(helper.getCity());
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

//    private class writeBuffer extends Thread{
//        @Override
//        public void run() {
//            try {
//                while (true){
//
//                    Log.e("socketLinkService","okasasaaaaaaaaaaaaaaaaaa");
////                    Log.e("socketLinkService","is checkloca" + checkLocationService.helper.isCheckLocation());
//                    if(checkLocationService.helper.isCheckLocation()){
//                        Log.e("socketLinkService","ischeckLocation is true, ready to write");
////                        writeMsg(checkLocationService.helper.getCity());
//                        setWriter(checkLocationService.helper.getCity());
//                        checkLocationService.helper.setCheckLocation(false);
//                    }
//
//                    sleep(2000);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void setWriter(String msg){
//        try {
//            writer.write(checkLocationService.helper.getCity());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void writeMsg(String msg){
////        if(msg.length() == 0 || outputStream == null)
////            return;
//        try {   //发送
//            outputStream.write(msg.getBytes());
//            outputStream.flush();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    InputStream inputStream = null;
    OutputStream outputStream = null;

    BufferedReader reader = null;
    BufferedWriter writer = null;
//

    private void sendMeg(final String msg){
        new Thread(){
            @Override
            public void run() {
                DataOutputStream outputStream = null;
                try {
                    outputStream = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                helper.setCheckLocation(false);
                Log.e("socketLinkService","sending location msg out to server");
            }
        }.start();
    }
//
    private Socket socket;

    private class ClientThread extends Thread{
        @Override
        public void run() {
            try {
                socket = new Socket("192.168.191.1",12345);
                Log.e("socketLinkService","链接上服务器socket");
                startReader(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            try {
//                socket = new Socket("192.168.191.1",12345);
//                Log.e("socketLinkService","connect to socket server");
//                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                dataInputStream = new java.io.DataInputStream(socket.getInputStream());
//                Log.e("socketLinkService","connect to socket server2");
//                while (true){ //不停地监听服务器
////                    Log.e("socketLinkService","hello");
//                    Log.e("socketLinkService","connect to socket server3");
//
//                    if(helper.isCheckLocation()){
//                        Log.e("socketLinkService","in send location socket");
//                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
//                        writer.println(helper.getCity());
//                        writer.flush();
//                    }
//                    Log.e("socketLinkService","connect to socket server4");
//                    String line = reader.readLine();
//                    if(line != null){
//                        Log.e("socketLinkService","reader is not null");
//                        Intent intent = new Intent("com.example.finaldesigntest.DOWNLOAD");
//                        intent.putExtra("toShow",line);
//                        sendBroadcast(intent);
//                        Log.e("socketLinkService",line);
//                    }
////
//
////                    if(checkSendLocation){
////                        Log.e("socketLinkService","in send location socket");
////                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
////                        printWriter.println(getNowLocation);
////                        Log.e("socketLinkService","the message is "+getNowLocation);
////                        printWriter.flush();
////                        checkSendLocation = false;
////                    }
//
//                    Log.e("socketLinkService","hello4");
//
//                   Thread.sleep(2000);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


        }
    }

    private void startReader(final Socket socket1){
        new Thread(){
            @Override
            public void run() {
                DataInputStream dataInputStream;
                try {
                    dataInputStream = new DataInputStream(socket1.getInputStream());
                    while (true){
                        String msg = dataInputStream.readUTF();
                        Log.e("socketLinkService","收到从服务器发来的信息"+msg);
                        Intent intent = new Intent("com.example.finaldesigntest.DOWNLOAD");
                        intent.putExtra("toShow",msg);
                        sendBroadcast(intent);
                        sleep(1000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private class checkIsThisSocket extends Thread{
        @Override
        public void run() {
            while(true){

                if(socketHelper.getSocketHelper().isThisSocket()){
                    sendMeg("chosen");

                    //还原
                    socketHelper.getSocketHelper().setThisSocket(false);
                }

                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
