package com.example.finaldesigntest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.finaldesigntest.R;

public class RTSPActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsp);

        findViewById(R.id.push).setOnClickListener(this);
        findViewById(R.id.pull).setOnClickListener(this);

        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.push:
                Intent intent = new Intent(RTSPActivity.this,realTimeAudioActivity.class);
                startActivity(intent);
                break;

            case R.id.pull:
                Intent intent1 = new Intent(RTSPActivity.this,listenRealRtspActivity.class);
                startActivity(intent1);
                break;

            case R.id.button1:
                Intent intent3 = new Intent(this,requestActivity.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.button2:
                Intent intent4 = new Intent(this,RTSPActivity.class);
                startActivity(intent4);
                finish();
                break;
        }
    }
}
