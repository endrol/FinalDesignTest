package com.example.finaldesigntest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.RtpStream;
import android.nfc.Tag;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class playTaskService extends Service {

    private toPlayReceiver receiver;
    private String TAG = "playTaskService";

    private  List<toPlayTaskList> toPlayTaskLists = new ArrayList<>();

    public playTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.finaldesigntest.DOWNLOAD");
        receiver = new toPlayReceiver();
        registerReceiver(receiver,intentFilter);
        toPlayTaskLists.add(new toPlayTaskList(99,99,"null"));

        checkTaskListThread listThread = new checkTaskListThread();
        listThread.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if(thread!=null){
            thread=null;
        }
        super.onDestroy();
    }


    private MediaPlayer mediaPlayer = null;
    private Thread thread;

    private class checkTaskListThread extends Thread{
        @Override
        public void run() {
            try {
                Log.d(TAG,"now in the thread to check");
                while(true){
                    if(toPlayTaskLists.get(0) != null){
                        if(toPlayTaskLists.get(0).getHour() ==
                                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)){
                            if(toPlayTaskLists.get(0).getMinute() ==
                                    Calendar.getInstance().get(Calendar.MINUTE)){
                                Log.d(TAG,"now time is correct, I should play");
                                playRecordThread recordThread = new playRecordThread();
                                recordThread.start();
                                sleep(1000);
                                toPlayTaskLists.remove(toPlayTaskLists.get(0));
                            }
                        }
                    }
                    sleep(1000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    String directory = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/savedRecord/";
    private class playRecordThread extends Thread{
        @Override
        public void run() {
            mediaPlayer = MediaPlayer.create(MyApplication.getContext(), Uri.fromFile(new File(
                    directory+toPlayTaskLists.get(0).getPath())));
            mediaPlayer.start();
        }
    }
//
//    private void initMediaPlayer(String filePath){
//        String directory = Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/savedRecord/";
//        File playFile = new File(directory + filePath);
//        try {
//            mediaPlayer.setDataSource(playFile.getPath());
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    class toPlayReceiver extends BroadcastReceiver{
        @Override       //接收广播传来的信息
        public void onReceive(Context context, Intent intent) {
            final String content = intent.getStringExtra("toShow");
            String hour = content.substring(content.indexOf(":")+1,content.indexOf("."));
            String minute = content.substring(content.lastIndexOf(":")+1,content.indexOf("&")-1);
            String path = content.substring(content.indexOf("&")+1);
            int playHour = Integer.valueOf(hour);
            int playMinute = Integer.valueOf(minute);
            Log.d(TAG,"TAG I received the broadcast in toPlayReceievr" + content);
            Log.d(TAG,"string hour is "+hour);
            Log.d(TAG,"string minute is "+ minute);
            Log.d(TAG,"int hour is "+playHour);
            Log.d(TAG,"int minute is "+ playMinute);
            Log.d(TAG,"path is "+path);
            toPlayTaskLists.add(new toPlayTaskList(playHour,playMinute,path));
            Collections.sort(toPlayTaskLists);
        }
    }
}
