package com.example.finaldesigntest;

import android.app.Service;
import android.content.Intent;
import android.net.rtp.AudioGroup;
import android.os.IBinder;

public class listenRealTimeRecordService extends Service {
    public listenRealTimeRecordService() {
    }

    AudioGroup group;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
}
