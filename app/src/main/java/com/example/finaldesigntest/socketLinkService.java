package com.example.finaldesigntest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.Buffer;

public class socketLinkService extends Service {
    public socketLinkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    //有mainActivity启动的service
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开启线程
        ClientThread clientThread = new ClientThread();
        clientThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    BufferedReader reader = null;
    BufferedWriter writer = null;

    private class ClientThread extends Thread{
        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket("192.168.191.1",12345);
                dataInputStream = new java.io.DataInputStream(socket.getInputStream());
                while (true){ //不停地监听服务器
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line = null;

                    if((line = reader.readLine())!= null){
                        Intent intent = new Intent("com.example.finaldesigntest.DOWNLOAD");
                        intent.putExtra("toShow",line);
                        sendBroadcast(intent);
                    }

                   Thread.sleep(2000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

}
