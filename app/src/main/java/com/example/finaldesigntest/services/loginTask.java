package com.example.finaldesigntest.services;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaldesigntest.helper.MyApplication;
import com.example.finaldesigntest.helper.checkLogin;
import com.example.finaldesigntest.helper.serverConstant;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 达明 on 2018/3/12.
 */

public class loginTask extends AsyncTask<RequestBody,Integer,String>{

    private String TAG = "loginTask";
    private TextView tv;

    public loginTask(TextView tvResult) {
        tv = tvResult;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG,"task on preExecute");
    }

    @Override
    protected String doInBackground(RequestBody... requestBodies) {

        Log.d(TAG,"task doInBackground()");
        try{
            Log.d(TAG,"pot 0");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(serverConstant.loginUrl)
                    .post(requestBodies[0])
                    .build();

            Log.d(TAG,"pot 1");
            client.newCall(request).execute();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            Log.d(TAG,"pot4");
            Log.d(TAG,"this is"+responseData+"ok");
            return responseData;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        tv.setText(s);
        Log.d(TAG,"pot5");
        Toast.makeText(MyApplication.getContext(),s,Toast.LENGTH_SHORT).show();
        if(s.equals("SUCCESS")){
            checkLogin.loginSuccess();
        }else{
            checkLogin.loginFailed();
        }
    }
}
