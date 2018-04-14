package com.example.finaldesigntest;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.easydarwin.push.MediaStream;

import io.reactivex.Single;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;

public class testActivity extends AppCompatActivity {

    private MediaStream mediaStream;

    private Single<MediaStream> getMediaStream(){
        Single<MediaStream> single = RxHelper.single(MediaStream
        .getBindedMediaStream(this,this),mediaStream);
        if(mediaStream == null){
            return single.doOnSuccess(new Consumer<MediaStream>() {
                @Override
                public void accept(MediaStream ms) throws Exception {
                    mediaStream = ms;
                }
            });
        }else {
            return single;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        // 启动服务...
        Intent intent = new Intent(testActivity.this, MediaStream.class);
        startService(intent);

        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(final MediaStream ms) throws Exception {
                ms.observeCameraPreviewResolution(testActivity.this,
                        new Observer<int[]>() {
                            @Override
                            public void onChanged(@Nullable int[] size) {
                                Toast.makeText(testActivity.this,
                                        "当前摄像头分辨率为:" + size[0] + "*" + size[1],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                final TextView pushingStateText = findViewById(R.id.pushing_state);
                final TextView pushingBtn =  findViewById(R.id.pushing);
                ms.observePushingState(testActivity.this,
                        new Observer<MediaStream.PushingState>() {

                            @Override
                            public void onChanged(@Nullable MediaStream.PushingState pushingState) {

                                pushingStateText.setText("推送");

                                if (pushingState.state > 0) {
                                    pushingBtn.setText("停止");
                                } else {
                                    pushingBtn.setText("推送");
                                }


                                pushingStateText.append(":\t" + pushingState.msg);
                                if (pushingState.state > 0) {
                                    pushingStateText.append(pushingState.url);
                                }

                            }

                        });
                TextureView textureView = findViewById(R.id.texture_view);
                textureView.setSurfaceTextureListener(new SurfaceTextureListenerWrapper() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        ms.setSurfaceTexture(surface);
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        ms.setSurfaceTexture(null);
                        return true;
                    }
                });


                if (ActivityCompat.checkSelfPermission(testActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(testActivity.this,
                                android.Manifest.permission.RECORD_AUDIO) !=
                                PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(testActivity.this,
                            new String[]{android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.RECORD_AUDIO},
                            1);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(testActivity.this,
                        "创建服务出错!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPushing(View view){
        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(MediaStream mediaStream) throws Exception {
                MediaStream.PushingState state = mediaStream.getPushingState();
                if (state != null && state.state > 0) { // 终止推送和预览
                    mediaStream.stopStream();
                    mediaStream.closeCameraPreview();
                } else {                                // 启动预览和推送.
                    mediaStream.openCameraPreview();
                    String id = PreferenceManager.getDefaultSharedPreferences(
                            testActivity.this).getString("camera-id", null);
                    if (id == null) {
                        double v = Math.random() * 1000;
                        id = "c_" + (int) v;
                        PreferenceManager.getDefaultSharedPreferences(
                                testActivity.this).edit().putString("camera-id", id).apply();
                    }
                    mediaStream.startStream("192.168.191.1", "554", id);
                }
            }
        });
    }

    public void onSwitchCamera(View view) {
        getMediaStream().subscribe(new Consumer<MediaStream>() {
            @Override
            public void accept(MediaStream mediaStream) throws Exception {
                mediaStream.switchCamera();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getMediaStream().subscribe(new Consumer<MediaStream>() {
                        @Override
                        public void accept(MediaStream mediaStream) throws Exception {
                            mediaStream.notifyPermissionGranted();
                        }
                    });
                } else {
                    finish();
                }
                break;
            }
        }
    }
}
