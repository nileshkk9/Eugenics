package com.nilesh.eugenics;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class splash extends AppCompatActivity {

    ImageView img;
    TextView eugenics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


       // Typeface font = Typeface.createFromAsset(getAssets(), "fonts/chip.ttf");

        img = (ImageView) findViewById(R.id.imageView2);

        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        img.startAnimation(animationFadeIn);

        //eugenics.setTypeface(font);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(splash.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();


    }

    @Override
    protected void onPause() {

        super.onPause();
        img.clearAnimation();
    }
}


