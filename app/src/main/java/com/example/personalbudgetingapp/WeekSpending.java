package com.example.personalbudgetingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekSpending extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView totalWeekAmountTextView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expenseRef;

    private String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        getSupportActionBar().hide();

        toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Week Spending");
        totalWeekAmountTextView=findViewById(R.id.totalWeekAmountTextView);
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();


        myDataList = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(WeekSpending.this,myDataList);
        recyclerView.setAdapter(weekSpendingAdapter);

        if (getIntent().getExtras()!=null){
            type= getIntent().getStringExtra("type");
            if (type.equals("week")){
                readWeekSpendingItem();
            }else if (type.equals("month")){
                readMonthSpendingItem();
            }
        }



    }

    private void readMonthSpendingItem() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months =Months.monthsBetween(epoch,now);

        expenseRef= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query=expenseRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount =0;
                for (DataSnapshot ds :snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountTextView.setText("Total Month's Spending: Rs." + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void readWeekSpendingItem() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks =Weeks.weeksBetween(epoch,now);

        expenseRef= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query=expenseRef.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount =0;
                for (DataSnapshot ds :snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountTextView.setText("Total Week's Spending: Rs." + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}