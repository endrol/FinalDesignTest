package com.example.finaldesigntest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class requestActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "requestActivity.this";

    private MediaRecorder mediaRecorder;
    private File recordFile;
    private String recordFilePath = Environment.getExternalStorageDirectory()
                                         .getAbsolutePath()+"/audio/";

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static String nowRecordPath;
    private boolean isRecording = false;

 //   private int[] setTime={};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        //链接socket
        Intent intent = new Intent(this,socketLinkService.class);
        startService(intent);
        //启动播放任务service
        Intent intent1 = new Intent(this,playTaskService.class);
        startService(intent1);


        findViewById(R.id.start_record).setOnClickListener(this);
        findViewById(R.id.stop_record).setOnClickListener(this);
        findViewById(R.id.listen_record).setOnClickListener(this);
        findViewById(R.id.stop_listen).setOnClickListener(this);
        findViewById(R.id.choose_location).setOnClickListener(this);
        findViewById(R.id.set_time).setOnClickListener(this);
        findViewById(R.id.choose_realTime).setOnClickListener(this);

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

    private void startRecording(){
        mediaRecorder = new MediaRecorder();
        recordFile = new File(recordFilePath+System.currentTimeMillis()+".amr");
        recordFile.getParentFile().mkdirs();   //创建father 文件夹

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
        isRecording = true;
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
} catch (IOException e) {
        e.printStackTrace();
        }

        }

    private void stopRecording(){
        if(recordFile != null){
            mediaRecorder.stop();
            mediaRecorder.reset();
            nowRecordPath = recordFile.getAbsolutePath();
            Log.d(TAG,"stoped recording");
            Log.d(TAG,"nowRecordPath is "+nowRecordPath);
            isRecording = false;
            initMediaPlayer();
            //弹出选择窗口
            Toast.makeText(this,"already stoped ",Toast.LENGTH_SHORT).show();
        }
    }
//
//    private void showChooseDialog(){
//        final AlertDialog.Builder choiceDialog = new AlertDialog.Builder(this);
//        choiceDialog.setIcon(android.R.drawable.ic_dialog_info);
//        choiceDialog.setTitle("Choosing").setMessage("choices");
//        choiceDialog.setPositiveButton("Send request", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MyApplication.getContext(),"this int which is "+ which,
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        choiceDialog.setNeutralButton("Listen Record", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MyApplication.getContext(),"this which is "+ which,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        choiceDialog.setNegativeButton("Cancel Record", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MyApplication.getContext(),"this which is "+ which,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        choiceDialog.setCancelable(false);
//        choiceDialog.show();
//    }

    private void initMediaPlayer(){
        Log.d(TAG,"In initMediaPlayer");
        try {
            File playFile = new File(nowRecordPath);
            mediaPlayer.setDataSource(playFile.getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.d(TAG,"can't find the file");
            e.printStackTrace();
        }
    }
    int hour, minute;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_record:
                if(isRecording == false){
                    startRecording();
                }
                break;

            case R.id.stop_record:
                if(isRecording == true){
                    stopRecording();
                }
                break;

            case R.id.listen_record:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                break;
            case R.id.stop_listen:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                    initMediaPlayer();
                }
                break;
            case R.id.set_time:
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
//
//                                Message message = new Message();
//                                message.what = 1;
//                                int[] ok ={};
//                                ok[0] = hour; ok[1] = minute;
//                                message.obj = ok;
//                                handler.sendMessage(message);
                                Log.d(TAG,"hour is "+hour);
                                Log.d(TAG,"minute is "+minute);
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
                break;
            case R.id.choose_location:
                Intent intent = new Intent(this,locationActivity.class);
                intent.putExtra("filePath",nowRecordPath);
//                setTime[0] = hour;
////                setTime[1] = minute;
//                Log.d(TAG,"now at last setTime is "+setTime[0]+setTime[1]);
                intent.putExtra("setTimeHour",hour);
                intent.putExtra("setTimeMinute",minute);
                startActivity(intent);
                break;

            case R.id.choose_realTime:
                Intent intent1 = new Intent(this,realTimeAudioActivity.class);
                startActivity(intent1);
                break;
        }
    }

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    setTime = (int[]) msg.obj;
//                    break;
//            }
//        }
//    };
    private void setTimeTo(final int hour, final int minute){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requestActivity.this,hour+" "+minute,
                        Toast.LENGTH_SHORT).show();
//                setTime[0] = hour;
//                setTime[1] = minute;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
        }
    }
}
