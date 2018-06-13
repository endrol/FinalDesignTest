package com.example.finaldesigntest.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaldesigntest.helper.MyApplication;
import com.example.finaldesigntest.R;
import com.example.finaldesigntest.helper.RxHelper;
import com.example.finaldesigntest.helper.isServiceRunning;

import org.easydarwin.push.MediaStream;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;


public class realTimeAudioActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "realTimeAudioActivity";

    private MediaStream mediaStream;
    private Single<MediaStream> getMediaStream() {
        Single<MediaStream> single = RxHelper.single(MediaStream.
                getBindedMediaStream(this, this), mediaStream);
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

    private TextView textView;

    private static final int STATE_NORMAL = 1;
    private static final int STATE_BROADCASTING = 2;

    private int currentState = STATE_NORMAL;
    private void changeState(int State){
        if(currentState != State){
            currentState = State;
            switch (currentState){
                case STATE_NORMAL:
                    textView.setText("点击直播");
                    break;
                case STATE_BROADCASTING:
                    textView.setText("点击停止");
                    break;

            }
        }
    }
    private String RTSP_IP;
    private String RTSP_PORT;
    private String RTSP_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_audio);

        Intent intent = new Intent(this, MediaStream.class);
        startService(intent);


        RTSP_IP = "192.168.191.1";
        RTSP_PORT = "554";
        RTSP_ID = "hello";

        setRTSP(RTSP_IP,RTSP_PORT,RTSP_ID);

//        edIP.setText(rtspIp);
//        edPORT.setText(rtspPort);
//        edID.setText(rtspId);

        textView = findViewById(R.id.view_Txt);


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

//        findViewById(R.id.record_realTime).setOnClickListener(this);
//        findViewById(R.id.stop_realTime).setOnClickListener(this);
        findViewById(R.id.img_Broadcast).setOnClickListener(this);
        findViewById(R.id.img_Setting).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void setRTSP(final String ip,final String port,final String id){
        RTSP_IP = ip;
        RTSP_PORT = port;
        RTSP_ID = id;
    }

    private String getRTSP_IP(){
        return RTSP_IP;
    }
    private String getRTSP_PORT(){
        return RTSP_PORT;
    }
    private String getRTSP_ID(){
        return RTSP_ID;
    }

    private void toPush(final String ip, final String port,final String id){
        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(MediaStream mediaStream) throws Exception {
                MediaStream.PushingState state = mediaStream.getPushingState();

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


    private void setting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");

        LayoutInflater factory = LayoutInflater.from(realTimeAudioActivity.this);
        final View view = factory.inflate(R.layout.setting_layout,null);

        builder.setView(view);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText rtspip = view.findViewById(R.id.ed_ip);
                EditText rtspport = view.findViewById(R.id.ed_port);
                EditText  rtspid = view.findViewById(R.id.ed_id);
//
//                rtspip.setText(RTSP_IP);
//                rtspport.setText(RTSP_PORT);
//                rtspid.setText(RTSP_ID);

                Toast.makeText(realTimeAudioActivity.this, "你输入的是: " +
                                rtspip.getText().toString() + "and "+rtspport.getText().toString()
                        + " " + rtspid.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                setRTSP(rtspip.getText().toString(),rtspport.getText().toString(),rtspid.getText().toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(realTimeAudioActivity.this,
                        "你点了取消", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.img_Setting:
                setting();
                break;

            /************************/

            case R.id.img_Broadcast:
                if(currentState == STATE_NORMAL){
                    //点击直播
                    changeState(STATE_BROADCASTING);
                    toPush(getRTSP_IP(),getRTSP_PORT(),getRTSP_ID());

                }else {
                    changeState(STATE_NORMAL);
                    toStop();
                }
                break;
        }
    }
}
