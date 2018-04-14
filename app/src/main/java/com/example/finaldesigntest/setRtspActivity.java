package com.example.finaldesigntest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.easydarwin.config.Config;
import org.easydarwin.easypusher.EasyApplication;

public class setRtspActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_rtsp);

        final EditText txtIp = (EditText) findViewById(R.id.edit_RtspIp);
        final EditText txtPort = (EditText) findViewById(R.id.edit_RtspPort);
        final EditText txtId = (EditText) findViewById(R.id.edit_RtspId);

        String ip = EasyApplication.getEasyApplication().getIp();
        String port = EasyApplication.getEasyApplication().getPort();
        String id = EasyApplication.getEasyApplication().getId();


        txtIp.setText(ip);
        txtPort.setText(port);
        txtId.setText(id);

        Button btnSave = (Button) findViewById(R.id.save_rtsp);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipValue = txtIp.getText().toString().trim();
                String portValue = txtPort.getText().toString().trim();
                String idValue = txtId.getText().toString().trim();

                if (TextUtils.isEmpty(ipValue)) {
                    ipValue = Config.DEFAULT_SERVER_IP;
                }
                if (TextUtils.isEmpty(portValue)) {
                    portValue = Config.DEFAULT_SERVER_PORT;
                }
                if (TextUtils.isEmpty(idValue)) {
                    idValue = Config.DEFAULT_STREAM_ID;
                }
                EasyApplication.getEasyApplication().saveStringIntoPref(Config.SERVER_IP, ipValue);
                EasyApplication.getEasyApplication().saveStringIntoPref(Config.SERVER_PORT, portValue);
                EasyApplication.getEasyApplication().saveStringIntoPref(Config.STREAM_ID, idValue);

                finish();
            }
        });

    }
}
