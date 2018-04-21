package com.example.finaldesigntest.helper;

import android.app.Application;
import android.content.Context;

/**
 * Created by 达明 on 2018/3/12.
 */

public class MyApplication extends Application {

    private static Context context;
    //全局获取context
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
