package com.example.personalbudgetingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AcccountActivity extends AppCompatActivity {

    private Toolbar settingstoolbar;
    private TextView userEmail;
    private Button logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acccount);

        getSupportActionBar().hide();

        userEmail=findViewById(R.id.userEmail);
        logoutbtn=findViewById(R.id.logoutbtn);

        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AcccountActivity.this)
                        .setTitle("Personal Budgeting App")
                        .setMessage("Are you sure want to exit???")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(AcccountActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });
    }
}