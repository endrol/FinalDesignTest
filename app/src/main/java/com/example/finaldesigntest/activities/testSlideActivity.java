package com.example.finaldesigntest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.finaldesigntest.R;

public class testSlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_slide);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(testSlideActivity.this,
                        "Go in file Method",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(testSlideActivity.this,requestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(testSlideActivity.this,
                        "Go in Stream Method",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(testSlideActivity.this,RTSPActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
