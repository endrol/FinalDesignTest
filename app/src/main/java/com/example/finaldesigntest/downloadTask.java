package com.example.finaldesigntest;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 达明 on 2018/3/29.
 */

public class downloadTask extends AsyncTask<String, Integer, String>{

    private String TAG = "downloadTask";
    private File file;
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG,"now in downloadTask: do in background");

        InputStream inputStream = null;

        try{  //download
            String fileName = strings[0].substring(strings[0].indexOf("&")+1);
            String startTime = strings[0].substring(0,strings[0].indexOf("&")-1);
            String directory = Environment.getExternalStorageDirectory().getAbsolutePath()
                                                + "/savedRecord/";
            file = new File(directory+fileName);
            file.getParentFile().mkdirs();   //创建father文件夹
            if(file.exists()){
                file.delete();
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(serverConstant.downloadUrl+fileName)
                    .build();
            Response response = client.newCall(request).execute();

            if(response != null){
                inputStream = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buff = new byte[1024];
                int len;
                while((len = inputStream.read(buff)) > 0) {
                    fos.write(buff, 0, len);
                }
                fos.close();
                inputStream.close();
                return "download success";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(MyApplication.getContext(),s,Toast.LENGTH_SHORT).show();
        super.onPostExecute(s);
    }
}
