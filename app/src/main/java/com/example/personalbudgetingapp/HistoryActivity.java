package com.example.personalbudgetingapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
            private RecyclerView recyclerView;
            private TodayitemsAdapter todayitemsAdapter;
            private List<Data> myDataList;
            private FirebaseAuth mAuth;
             private String onlineUserId="";
             private DatabaseReference expensesRef,PersonalRef;

             private Toolbar settingsToolbar;

             private Button search;
             private TextView historyTotalAmountSpent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().hide();

        search=findViewById(R.id.search);
        historyTotalAmountSpent=findViewById(R.id.historyTotalAmountSpent);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        recyclerView=findViewById(R.id.recycler_View_Id_feed);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        myDataList = new ArrayList<>();
        todayitemsAdapter=new TodayitemsAdapter(HistoryActivity.this,myDataList);
        recyclerView.setAdapter(todayitemsAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int months = month+1;
        String padded = String.format("%02d",dayOfMonth);
        String date=padded+"-"+"0"+months+"-"+year;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Data data = snapshot1.getValue(Data.class);
                    myDataList.add(data);
                }
                todayitemsAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                int totalAmount=0;
                for (DataSnapshot ds :snapshot.getChildren()){
                    Map<String,Object> map =(Map<String,Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    if (totalAmount>0){
                        historyTotalAmountSpent.setVisibility(View.VISIBLE);
                        historyTotalAmountSpent.setText("This day you spent Rs "+totalAmount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}