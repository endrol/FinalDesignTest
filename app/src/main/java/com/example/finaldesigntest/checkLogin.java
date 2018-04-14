package com.example.finaldesigntest;

/**
 * Created by 达明 on 2018/3/12.
 */

public class checkLogin {

    public static boolean isLoginOk = false;

    public static void loginFailed(){
        isLoginOk = false;
    }

    public static void loginSuccess(){
        isLoginOk = true;
    }
}
