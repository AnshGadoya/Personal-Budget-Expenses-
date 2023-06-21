package com.example.personalbudgetingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

   // private static int SPLASH = 3000;
    //Animation animation;
   Animation topAnim, bottomAnim;
    private ImageView imageView;
    private TextView appname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

       // animation= AnimationUtils.loadAnimation(this,R.anim.animation);


        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        imageView=findViewById(R.id.imageview);
        appname=findViewById(R.id.appname);

        imageView.setAnimation(topAnim);
        appname.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(SplashScreenActivity.this,Splash2.class);
                startActivity(intent);
                finish();
            }
        },3000);


    }
}