package com.example.dine;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class LoadingActivity extends AppCompatActivity {
private static final int TIMEOUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivities(new Intent[]{intent});
            }
        }, TIMEOUT);
    }
}