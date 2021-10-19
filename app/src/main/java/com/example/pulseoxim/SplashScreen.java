package com.example.pulseoxim;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashfile);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {


// Using handler with postDelayed called runnable run method

            @Override

            public void run() {

                Intent i = new Intent(SplashScreen.this, Login.class);

                startActivity(i);

                // close this activity

                finish();

            }

        }, 3000);

    }

}
