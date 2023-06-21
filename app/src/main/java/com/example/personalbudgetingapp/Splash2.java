package com.example.personalbudgetingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash2 extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    TextView TV1,TV2,TV3;
    Button loginbtn,signupbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        getSupportActionBar().hide();

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        TV1=findViewById(R.id.TV1);
        TV2=findViewById(R.id.TV2);
        TV3=findViewById(R.id.TV3);
        loginbtn=findViewById(R.id.loginbtn);
        signupbtn=findViewById(R.id.signupbtn);


        TV1.setAnimation(topAnim);
        TV2.setAnimation(topAnim);
        TV3.setAnimation(topAnim);

        loginbtn.setAnimation(bottomAnim);
        signupbtn.setAnimation(bottomAnim);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Splash2.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

      signupbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent= new Intent(Splash2.this,RegistrationActivity.class);
              startActivity(intent);
              finish();
          }
      });

    }
}