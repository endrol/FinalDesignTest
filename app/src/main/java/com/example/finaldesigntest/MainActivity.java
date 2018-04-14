package com.example.finaldesigntest;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


//主活动 登陆之后的界面
//主要操作
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText edMessage;
    private TextView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edMessage =  findViewById(R.id.ed_message);
        messageView = findViewById(R.id.show_message);

        findViewById(R.id.send_message).setOnClickListener(this);
        findViewById(R.id.connect).setOnClickListener(this);
    }
    //-------------------------------------------

    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;

    private void damn(LifecycleOwner owner){
        Log.d("MainActivity","ojbk");
    }



    public void connect(){
            Toast.makeText(this,"链接成功",Toast.LENGTH_SHORT).show();
            AsyncTask<Void,String,Void> read = new AsyncTask<Void, String, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        socket = new Socket("192.168.191.1",12345);
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this,"无法建立链接",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        publishProgress("@success");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String line;
                    try {
                        while((line = reader.readLine())!= null){
                            publishProgress(line);
                        }
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this,"无法建立链接",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(String... values) {

                    if(values[0].equals("@success")){
                        Toast.makeText(MainActivity.this,"建立链接",
                                Toast.LENGTH_SHORT).show();
                    }

                    messageView.append("they said: "+values[0]+"\n");
                    super.onProgressUpdate(values);
                }
            };
            read.execute();
        }


    public void send(){
        try {
            messageView.append("I said: "+edMessage.getText().toString()+"\n");
            writer.write(edMessage.getText().toString()+"\n");
            writer.flush();
            edMessage.setText("");//设置为空
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
                connect();
                damn(this);
                break;

            case R.id.send_message:
                send();
                break;
        }
    }
}
