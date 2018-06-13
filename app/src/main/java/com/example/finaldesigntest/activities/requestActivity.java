package com.example.finaldesigntest.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.finaldesigntest.R;
import com.example.finaldesigntest.helper.AudioRecordUtil;
import com.example.finaldesigntest.helper.PopupWindow;
import com.example.finaldesigntest.helper.TimeUtil;
import com.example.finaldesigntest.services.playTaskService;
import com.example.finaldesigntest.services.socketLinkService;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class requestActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "requestActivity";


    private MediaPlayer mediaPlayer;
    private AudioManager manager = null;
    private int maxVolume = 50;

    private Button mButton;
    private ImageView mImageView;
    private TextView mTextView;
    private AudioRecordUtil audioRecordUtil;
    private Context context;
    private PopupWindow popupWindow;
    private FlowingDrawer layout;
    private ImageView imgPlay;
    private ImageView imgCheck;
 //   private ImageView imgSetTime;

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    boolean isFileThere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        context = this;
        isFileThere = false;

        //链接socket
        Intent intent = new Intent(this,socketLinkService.class);
        startService(intent);

        //启动播放任务service
        Intent intent1 = new Intent(this,playTaskService.class);
        startService(intent1);

        layout = findViewById(R.id.r1);
        mButton = findViewById(R.id.btn_Record);
        imgCheck = findViewById(R.id.img_Check);
        imgCheck.setVisibility(View.GONE);
        imgPlay = findViewById(R.id.img_Listen);
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying()){

                    manager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
                            AudioManager.FLAG_PLAY_SOUND);
                    mediaPlayer.start();
                    imgCheck.setVisibility(View.INVISIBLE);
                }
            }
        });

        final View view = View.inflate(this,R.layout.microphone_layout,null);
        popupWindow = new PopupWindow(this,view);
        //popupWindow布局文件的控件
        mImageView = view.findViewById(R.id.iv_recording_icon);
        mTextView = view.findViewById(R.id.tv_recording_time);
        audioRecordUtil = new AudioRecordUtil();  //默认地址
        audioRecordUtil.setOnAudioStatusUpdateListener(new AudioRecordUtil.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int)(3000 + 6000 * db / 100));
                mTextView.setText(TimeUtil.long2String(time));
            }

            @Override
            public void onStop(String filePath) {
                Toast.makeText(requestActivity.this,
                        "录音保存在："+filePath, Toast.LENGTH_SHORT).show();
                initMediaPlayer(filePath);

                isFileThere = true;
                setTime();

                mTextView.setText(TimeUtil.long2String(0));
                imgCheck.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel(String message) {
                Toast.makeText(requestActivity.this,
                        "接收信息"+message, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.img_Locate).setOnClickListener(this);
        findViewById(R.id.img_SetTime).setOnClickListener(this);
        findViewById(R.id.choose_realTime).setOnClickListener(this);
        findViewById(R.id.listen_realRtsp).setOnClickListener(this);

        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(requestActivity.this, Manifest
                .permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if(ContextCompat.checkSelfPermission(requestActivity.this, Manifest
                .permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(requestActivity.this,permissions,
                    1);
        }

        StartListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for(int result:grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                }else{
                    Toast.makeText(this,"发生未知错误",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    //    private void startRecording(){
//        Log.e(TAG,"in startrecording");
//        recordFile = new File(recordFilePath+System.currentTimeMillis()+".amr");
//
//        recordFile.getParentFile().mkdirs();   //创建father 文件夹
//
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
//        Log.e(TAG,"file path is"+recordFile.getAbsolutePath());
//        isRecording = true;
//        try {
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//            Log.e(TAG,"i'm recording");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void stopRecording(){
//        if(recordFile != null){
//            mediaRecorder.stop();
//            Log.e(TAG,"the file"+recordFile.getAbsolutePath()+"is record stoped");
//            mediaRecorder.reset();
//            nowRecordPath = recordFile.getAbsolutePath();
//            Log.e(TAG,"stoped recording");
//            Log.e(TAG,"nowRecordPath is "+nowRecordPath);
//            isRecording = false;
//            initMediaPlayer();
//            //弹出选择窗口
//            Toast.makeText(this,"already stoped ",Toast.LENGTH_SHORT).show();
//        }
//    }

    private int currentState = STATE_NORMAL;
    private void changeState(int state){
        if(currentState != state){
            currentState = state;
            switch (currentState){
                case STATE_NORMAL:
                    mButton.setText("长按录音");
                    break;
                case STATE_RECORDING:
                    mButton.setText("松开保存");
                    break;
                case STATE_WANT_TO_CANCEL:
                    mButton.setText("松开取消");
                    break;
            }
        }
    }
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > mButton.getWidth()) {// 判断是否在左边，右边，上边，下边
            return true;
        }
        if (y < -50 || y > mButton.getHeight() + 50) {
            return true;
        }

        return false;
    }

    public void StartListener(){
        //Button的touch监听
        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

                        audioRecordUtil.startRecord();
                        changeState(STATE_RECORDING);

                        break;

                    case MotionEvent.ACTION_UP:
                        if(currentState == STATE_RECORDING){
                            audioRecordUtil.stopRecord();        //结束录音（保存录音文件）
                        }
                        if(currentState == STATE_WANT_TO_CANCEL){
                            audioRecordUtil.cancelRecord();    //取消录音（不保存录音文件）
                        }
                        changeState(STATE_NORMAL);
                        popupWindow.dismiss();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(wantToCancel(x,y)){
                            changeState(STATE_WANT_TO_CANCEL);
                        }else {
                            changeState(STATE_RECORDING);
                        }
                        break;
                }
                return true;
            }
        });
    }

    String nowFilePath;

    private void initMediaPlayer(String nowRecordPath){
        Log.d(TAG,"In initMediaPlayer");
        nowFilePath = nowRecordPath;
        try {
            if (nowRecordPath != null){
                File playFile = new File(nowRecordPath);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(playFile.getPath());
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            Log.d(TAG,"can't find the file");
            e.printStackTrace();
        }
    }
    int hour, minute;

    private void setTime(){
        TimePickerView timePickerView = new TimePickerBuilder(this,
                new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        Toast.makeText(getApplicationContext(),date.toString(),
                                Toast.LENGTH_SHORT).show();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        minute = calendar.get(Calendar.MINUTE);
                        Log.d(TAG,"hour is "+hour);
                        Log.d(TAG,"minute is "+minute);

                        if(isFileThere){
                            //已经有了录音文件 可以直接进入地址页面
                            Intent intent = new Intent(requestActivity.this,locationActivity.class);
                            intent.putExtra("filePath",nowFilePath);
                            intent.putExtra("setTimeHour",hour);
                            intent.putExtra("setTimeMinute",minute);
                            startActivity(intent);
                        }

                    }
                })
                .setLabel("year","month",
                        "day","hour",
                        "min","sec")
                .setType(new boolean[]{false, false, false, true, true, true})
                .setTitleText("选择启动时间")
//                        .setRangDate(Calendar.getInstance(),
//                                Calendar.getInstance().add(Calendar.HOUR_OF_DAY,5))
                .build();
        timePickerView.setDate(Calendar.getInstance());
        timePickerView.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_SetTime:
                setTime();
                break;
            case R.id.img_Locate:
                Intent intent = new Intent(this,locationActivity.class);
                intent.putExtra("filePath",nowFilePath);
                intent.putExtra("setTimeHour",hour);
                intent.putExtra("setTimeMinute",minute);
                startActivity(intent);
                break;

            case R.id.listen_realRtsp:
                Intent intent1 = new Intent(this,listenRealRtspActivity.class);
                startActivity(intent1);
                break;

            case R.id.choose_realTime:
                Intent intent2 = new Intent(this,realTimeAudioActivity.class);
                startActivity(intent2);
                break;
        }
    }

    /*********点击两次返回***********/
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && (event.getAction() == KeyEvent.ACTION_DOWN)){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
