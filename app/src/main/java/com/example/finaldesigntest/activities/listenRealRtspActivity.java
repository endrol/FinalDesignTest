package com.example.finaldesigntest.activities;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.finaldesigntest.R;

import java.io.IOException;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

public class listenRealRtspActivity extends AppCompatActivity implements View.OnClickListener, NodePlayerDelegate {

    private String rtspPath;

    private EditText edIP;
    private EditText edPORT;
    private EditText edID;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    NodePlayer nodePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_real_rtsp);

        edIP = findViewById(R.id.edIP);
        edPORT = findViewById(R.id.edPort);
        edID = findViewById(R.id.edId);

        nodePlayer = new NodePlayer(this);
        NodePlayerView playerView = findViewById(R.id.player_view);
        playerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
//        playerView.setVisibility();
        nodePlayer.setPlayerView(playerView);
        nodePlayer.setNodePlayerDelegate(this);
        nodePlayer.setHWEnable(true);
        nodePlayer.setBufferTime(500);
        nodePlayer.setMaxBufferTime(1000);


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String rtspIp = pref.getString("RTSP_IP", "");
        String rtspPort = pref.getString("RTSP_PORT", "");
        String rtspId = pref.getString("RTSP_ID", "");
        edIP.setText(rtspIp);
        edPORT.setText(rtspPort);
        edID.setText(rtspId);

        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.play).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    private void setRtspPath(String ip, String port, String id) {
        rtspPath = "rtsp://" + ip + ":" + port + "/" + id + ".sdp";


        nodePlayer.setInputUrl(rtspPath);
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:

                editor = pref.edit();
                String rtspIp = edIP.getText().toString();
                String rtspPort = edPORT.getText().toString();
                String rtspId = edID.getText().toString();
                editor.putString("RTSP_IP", rtspIp);
                editor.putString("RTSP_PORT", rtspPort);
                editor.putString("RTSP_ID", rtspId);
                editor.apply();

                setRtspPath(rtspIp, rtspPort, rtspId);
                break;
            case R.id.play:
                nodePlayer.start();
                break;

            case R.id.stop:
                nodePlayer.stop();
                break;

            case R.id.back_button:
                nodePlayer.release();
                finish();
                break;
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
