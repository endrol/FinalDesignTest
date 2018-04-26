package com.example.finaldesigntest.activities;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finaldesigntest.helper.MyApplication;
import com.example.finaldesigntest.R;
import com.example.finaldesigntest.helper.RxHelper;
import com.example.finaldesigntest.helper.isServiceRunning;

import org.easydarwin.push.MediaStream;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;


public class realTimeAudioActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = "realTimeAudioActivity";

    private EditText edIP;
    private EditText edPORT;
    private EditText edID;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

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

    isServiceRunning running = new isServiceRunning();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_audio);
        //启动service
//        if(!running.serviceRuning(this,"org.easydarwin.push.MediaStream")){
//
//        }
        Intent intent = new Intent(this, MediaStream.class);
        startService(intent);

        edIP = findViewById(R.id.edRtspip);
        edPORT = findViewById(R.id.edRtspport);
        edID = findViewById(R.id.edRtspId);

        //暂时保存 ip 与 端口号
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String rtspIp = pref.getString("RTSP_IP","");
        String rtspPort = pref.getString("RTSP_PORT","");
        String rtspId = pref.getString("RTSP_ID","");
        edIP.setText(rtspIp);
        edPORT.setText(rtspPort);
        edID.setText(rtspId);


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
//        findViewById(R.id.save_rtsp_address).setOnClickListener(this);
//
//        findViewById(R.id.button1).setOnClickListener(this);
//        findViewById(R.id.button2).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toPush(final String ip, final String port,final String id){
        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(MediaStream mediaStream) throws Exception {
                MediaStream.PushingState state = mediaStream.getPushingState();
//
//                String id = PreferenceManager.getDefaultSharedPreferences(
//                        realTimeAudioActivity.this).getString("hello", null);
//                if (id == null) {
//                    id = "hello";
//                    PreferenceManager.getDefaultSharedPreferences(
//                            realTimeAudioActivity.this).edit().putString("hello", id).apply();
//                }
                mediaStream.startStream(ip,port,id);
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

                editor = pref.edit();
                String rtspIp = edIP.getText().toString();
                String rtspPort = edPORT.getText().toString();
                String rtspId = edID.getText().toString();
                editor.putString("RTSP_IP",rtspIp);
                editor.putString("RTSP_PORT",rtspPort);
                editor.putString("RTSP_ID",rtspId);
                editor.apply();

                toPush(rtspIp,rtspPort,rtspId);
                break;
            case R.id.stop_realTime:
                toStop();
                break;
            /************************/
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
}
