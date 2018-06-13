package com.example.finaldesigntest.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.finaldesigntest.R;

import java.io.IOException;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

public class listenRealRtspActivity extends AppCompatActivity implements View.OnClickListener, NodePlayerDelegate {

    private String rtspPath;

    NodePlayer nodePlayer;

    private int maxVolume = 50;
    private AudioManager manager = null;

    private TextView textView;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_LISTENBRA = 2;

    private int currentState = STATE_NORMAL;
    private void changeState(int State){
        if(currentState != State){
            currentState = State;
            switch (currentState){
                case STATE_NORMAL:
                    textView.setText("点击监听");
                    break;
                case STATE_LISTENBRA:
                    textView.setText("点击停止");
                    break;

            }
        }
    }

    private String RTSP_IP;
    private String RTSP_PORT;
    private String RTSP_ID;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_real_rtsp);

        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        RTSP_IP = "192.168.191.1";
        RTSP_PORT = "554";
        RTSP_ID = "hello";

        textView = findViewById(R.id.view_Txt2);

        nodePlayer = new NodePlayer(this);
        NodePlayerView playerView = findViewById(R.id.player_view);
        playerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
//        playerView.setVisibility();
        nodePlayer.setPlayerView(playerView);
        nodePlayer.setNodePlayerDelegate(this);
        nodePlayer.setHWEnable(true);
        nodePlayer.setBufferTime(500);
        nodePlayer.setMaxBufferTime(1000);
        setRtspPath(getRTSP_IP(), getRTSP_PORT(), getRTSP_ID());


        findViewById(R.id.img_LisBra).setOnClickListener(this);
        findViewById(R.id.img_setting2).setOnClickListener(this);

    }

    private void setRtspPath(String ip, String port, String id) {
        rtspPath = "rtsp://" + ip + ":" + port + "/" + id + ".sdp";

        nodePlayer.setInputUrl(rtspPath);
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
    }

    private void setting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");

        LayoutInflater factory = LayoutInflater.from(listenRealRtspActivity.this);
        final View view = factory.inflate(R.layout.setting_layout,null);

        builder.setView(view);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText rtspip = view.findViewById(R.id.ed_ip);
                EditText rtspport = view.findViewById(R.id.ed_port);
                EditText  rtspid = view.findViewById(R.id.ed_id);

                Toast.makeText(listenRealRtspActivity.this, "你输入的是: " +
                                rtspip.getText().toString() + "and "+rtspport.getText().toString()
                                + " " + rtspid.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                setRTSP(rtspip.getText().toString(),rtspport.getText().toString(),rtspid.getText().toString());
                setRtspPath(getRTSP_IP(),getRTSP_PORT(),getRTSP_ID());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(listenRealRtspActivity.this,
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
        switch (v.getId()) {

            case R.id.img_LisBra:
                if(currentState == STATE_NORMAL){
                    //点击监听
                    changeState(STATE_LISTENBRA);

                    manager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
                            AudioManager.FLAG_PLAY_SOUND);

                    nodePlayer.start();
                }else {
                    changeState(STATE_NORMAL);
                    nodePlayer.stop();
                }
                break;

            case R.id.img_setting2:
                setting();
                break;
                /***********************/
        }
    }

    @Override
    public void onEventCallback(NodePlayer nodePlayer, int event, String msg) {
        Log.i("NodeMedia.NodePlayer", "onEventCallback:" + event + " msg:" + msg);

        switch (event) {
            case 1000:
                // 正在连接视频
                break;
            case 1001:
                // 视频连接成功
                break;
            case 1002:
                // 视频连接失败 流地址不存在，或者本地网络无法和服务端通信，回调这里。5秒后重连， 可停止
//                nodePlayer.stopPlay();
                break;
            case 1003:
                // 视频开始重连,自动重连总开关
//                nodePlayer.stopPlay();
                break;
            case 1004:
                // 视频播放结束
                break;
            case 1005:
                // 网络异常,播放中断,播放中途网络异常，回调这里。1秒后重连，如不需要，可停止
//                nodePlayer.stopPlay();
                break;
            case 1006:
                //RTMP连接播放超时
                break;
            case 1100:
                // 播放缓冲区为空
//				System.out.println("NetStream.Buffer.Empty");
                break;
            case 1101:
                // 播放缓冲区正在缓冲数据,但还没达到设定的bufferTime时长
//				System.out.println("NetStream.Buffer.Buffering");
                break;
            case 1102:
                // 播放缓冲区达到bufferTime时长,开始播放.
                // 如果视频关键帧间隔比bufferTime长,并且服务端没有在缓冲区间内返回视频关键帧,会先开始播放音频.直到视频关键帧到来开始显示画面.
//				System.out.println("NetStream.Buffer.Full");
                break;
            case 1103:
//				System.out.println("Stream EOF");
                // 客户端明确收到服务端发送来的 StreamEOF 和 NetStream.Play.UnpublishNotify时回调这里
                // 注意:不是所有云cdn会发送该指令,使用前请先测试
                // 收到本事件，说明：此流的发布者明确停止了发布，或者因其网络异常，被服务端明确关闭了流
                // 本sdk仍然会继续在1秒后重连，如不需要，可停止
//                nodePlayer.stopPlay();
                break;
            case 1104:
                //解码后得到的视频高宽值 格式为:{width}x{height}
                break;
            default:
                break;
        }
    }
}
