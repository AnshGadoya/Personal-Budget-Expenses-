package com.example.personalbudgetingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email,password;
    private Button registerbtn;
    private TextView registerOn;


    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        registerbtn=findViewById(R.id.registerbtn);
        registerOn=findViewById(R.id.registerOn);



        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);


       registerOn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
               startActivity(intent);

           }
       });
       registerbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String emailString=email.getText().toString();
               String passwordString=password.getText().toString();


               if (TextUtils.isEmpty(emailString)){
                   email.setError("Email is requried");
               }
               if (TextUtils.isEmpty(passwordString)){
                   password.setError("password is requried");
               }

               else {

                   progressDialog.setMessage("Registration in progress");
                   progressDialog.setCanceledOnTouchOutside(false);
                   progressDialog.show();


                       mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {

                               if (task.isSuccessful()) {
                                   Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                   startActivity(intent);
                                   finish();
                                   progressDialog.dismiss();

                               } else {
                                   Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                   //Toast.makeText(RegistrationActivity.this, "Password Does not Match", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                               }

                           }
                       });

               }
           }
       });
    }
}