package com.example.personalbudgetingapp;

import static java.util.Objects.requireNonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView budgetTv,todayTv,weekTv,monthTv,savingsTv;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef,expensesRef,PersonalRef;
    private String onlineUserId = "";
   //String onlineUserId;

    private int totalAmountMonth =0;
    private int totalAmountBudget =0;
    private int totalAmountBudgetD =0;
    private int totalAmountBudgetC =0;

    private CardView budgetCardView,todayCardView,historyCardView;
    private ImageView weekBtnImageView,todayBtnImageView,budgetBtnImageView,monthBtnImageView,analyticsImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      // getSupportActionBar().hide();

       /* toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Budgeting App");*/

        mAuth=FirebaseAuth.getInstance();
        onlineUserId= mAuth.getCurrentUser().getUid();


       budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(onlineUserId);
        expensesRef= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
       PersonalRef= FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        budgetTv=findViewById(R.id.budgetTv);
        todayTv=findViewById(R.id.todayTv);
        weekTv=findViewById(R.id.weekTv);
        monthTv=findViewById(R.id.monthTv);
        savingsTv=findViewById(R.id.savingsTv);


     //   budgetCardView=findViewById(R.id.budgetCardView);
        todayCardView=findViewById(R.id.todayCardView);
        weekBtnImageView=findViewById(R.id.weekBtnImageView);
        todayBtnImageView=findViewById(R.id.todayBtnImageView);
        budgetBtnImageView=findViewById(R.id.budgetBtnImageView);
        monthBtnImageView=findViewById(R.id.monthBtnImageView);
        analyticsImageView=findViewById(R.id.analyticsImageView);

        toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personal Budgeting App");


        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TodaySpendingAvtivity.class);
                startActivity(intent);
            }
        });

        budgetBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,BudgetActivity.class);
                startActivity(intent);
            }
        });
        todayBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TodaySpendingAvtivity.class);
                startActivity(intent);
            }
        });
        weekBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,WeekSpending.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });
        monthBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,WeekSpending.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });
        analyticsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ChooseAnalyticActivity.class);
                startActivity(intent);
            }
        });
        historyCardView=findViewById(R.id.historyCardView);
        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetD += pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetD;
                    PersonalRef.child("budget").setValue(totalAmountBudgetC);
                }else {
                    PersonalRef.child("budget").setValue(0);
                    Toast.makeText(MainActivity.this, "Please Set a BUDGET", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getBudgetAmount();
        getTodaySpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        getSavings();

    }
    private void getBudgetAmount(){
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmountBudget += pTotal;
                        budgetTv.setText("Rs "+String.valueOf(totalAmountBudget));
                    }

                }else {
                   totalAmountBudget=0;
                   budgetTv.setText("Rs "+String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getTodaySpentAmount(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map = (Map<String,Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal=Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    todayTv.setText("Rs "+totalAmount);
                }
                PersonalRef.child("today").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void  getWeekSpentAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks =Weeks.weeksBetween(epoch,now);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query=reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    weekTv.setText("Rs "+totalAmount);
                }
                PersonalRef.child("week").setValue(totalAmount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthSpentAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query=reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    monthTv.setText("Rs "+totalAmount);
                }
                PersonalRef.child("month").setValue(totalAmount);
                totalAmountMonth=totalAmount;
               // totalAmountRemaining=totalAmountBudgetC-totalAmountMonth;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getSavings(){
        PersonalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int budget;
                    if (snapshot.hasChild("budget")){
                        budget = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    }else {
                        budget = 0;
                    }
                    int monthSpending;
                    if (snapshot.hasChild("month")){
                        monthSpending = Integer.parseInt(requireNonNull(snapshot.child("month").getValue().toString()));
                    }else {
                        monthSpending = 0;
                    }

                    int savings = budget - monthSpending;
                    savingsTv.setText("Rs "+savings);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.account){
            Intent intent=new Intent(MainActivity.this,AcccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}