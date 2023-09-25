package com.abmtech.eduriteadmin.activities;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.Session.Session;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(() -> {
            try {
                sleep(2000);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}