package com.example.finaldesigntest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    //记住账号和密码function
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;

    private EditText edAccount;
    private EditText edPassword;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        edAccount = findViewById(R.id.edAccount);
        edPassword =  findViewById(R.id.edPassword);
        tvResult =  findViewById(R.id.view_login_result);

        rememberPass =  findViewById(R.id.remember_pass);
        findViewById(R.id.login).setOnClickListener(this);
        boolean isRemember = pref.getBoolean("remember_password",false);
        if(isRemember){
            //将账号密码设置到文本框
            String account = pref.getString("account","");
            String password = pref.getString("password","");
            edAccount.setText(account);
            edPassword.setText(password);
            rememberPass.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                //TODO 登陆逻辑
                    String account = edAccount.getText().toString();
                String password = edPassword.getText().toString();

                editor = pref.edit();
                if(rememberPass.isChecked()){ //检查checkbox是否选中
                    //将account, password存入手机SharedPreferences
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",account);
                    editor.putString("password",password);
                }else{
                    editor.clear();
                }
                editor.apply();

                RequestBody body = new FormBody.Builder()
                        .add("account",account)
                        .add("password",password)
                        .build();

                new loginTask(tvResult).execute(body);

                if(checkLogin.isLoginOk){
                    Intent intent = new Intent(this,requestActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }


}
