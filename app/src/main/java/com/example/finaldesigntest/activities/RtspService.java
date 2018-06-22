package com.example.finaldesigntest.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;

import com.example.finaldesigntest.R;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

public class RtspService extends Service implements NodePlayerDelegate {

    private String rtspPath;

    NodePlayer nodePlayer;

    private int maxVolume = 50;
    private AudioManager manager = null;

    private String RTSP_IP;
    private String RTSP_PORT;
    private String RTSP_ID;

    private void setRTSP(final String ip,final String port,final String id){
        RTSP_IP = ip;
        RTSP_PORT = port;
        RTSP_ID = id;
    }

    public RtspService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Intent intent = new Intent(this,requestActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentIntent(pi)
                .setContentTitle("RTSP Connecting")
                .setContentText("RTSP is listening")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .build();

        startForeground(1,notification);

        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        RTSP_IP = "192.168.191.1";
        RTSP_PORT = "554";
        RTSP_ID = "hello";

        nodePlayer = new NodePlayer(this);

        nodePlayer.setNodePlayerDelegate(this);
        nodePlayer.setHWEnable(true);
        nodePlayer.setBufferTime(500);
        nodePlayer.setMaxBufferTime(1000);
//        setRtspPath(getRTSP_IP(), getRTSP_PORT(), getRTSP_ID());

    }

    @Override
    public void onEventCallback(NodePlayer nodePlayer, int i, String s) {

    }
}
