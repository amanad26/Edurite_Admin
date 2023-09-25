package com.abmtech.eduriteadmin.activities;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.Session.Session;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    Session session;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = this;

        new Thread(() -> {
            try {
                sleep(2000);
                if (session.isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                }
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}