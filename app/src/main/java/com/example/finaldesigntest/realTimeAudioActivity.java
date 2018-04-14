package com.example.finaldesigntest;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.net.sip.SipProfile;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.easydarwin.push.MediaStream;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;


public class realTimeAudioActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = "realTimeAudioActivity";

    private MediaStream mediaStream;
    private Single<MediaStream> getMediaStream() {
        Single<MediaStream> single = RxHelper.single(MediaStream.
                getBindedMediaStream(this, (LifecycleOwner) this), mediaStream);
        if (mediaStream == null) {
            return single.doOnSuccess(new Consumer<MediaStream>() {
                @Override
                public void accept(MediaStream ms) throws Exception {
                    mediaStream = ms;
                }
            });
        } else {
            return single;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_audio);
        //启动service
        Intent intent = new Intent(this, MediaStream.class);
        startService(intent);

        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(final MediaStream mediaStream) throws Exception {
                MediaStream.PushingState state = mediaStream.getPushingState();
                if (state != null && state.state > 0) {
                    Toast.makeText(MyApplication.getContext(), "在推送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyApplication.getContext(), "NOT推送", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(realTimeAudioActivity.this,
                        "创建服务出错!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.record_realTime).setOnClickListener(this);
        findViewById(R.id.stop_realTime).setOnClickListener(this);
        findViewById(R.id.set_rtsp).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toPush(){
        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(MediaStream mediaStream) throws Exception {
                MediaStream.PushingState state = mediaStream.getPushingState();

                String id = PreferenceManager.getDefaultSharedPreferences(
                        realTimeAudioActivity.this).getString("hello", null);
                if (id == null) {
                    id = "hello";
                    PreferenceManager.getDefaultSharedPreferences(
                            realTimeAudioActivity.this).edit().putString("hello", id).apply();
                }
                mediaStream.startStream("192.168.191.1","554",id);
            }
        });
    }

    private void toStop(){
        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(MediaStream mediaStream) throws Exception {
                MediaStream.PushingState state = mediaStream.getPushingState();
                if(state != null && state.state > 0){
                    mediaStream.stopStream();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_realTime:
                toPush();
                break;
            case R.id.stop_realTime:
                toStop();
                break;

            case R.id.set_rtsp:
                startActivity(new Intent(this,setRtspActivity.class));
                break;
        }
    }
}
