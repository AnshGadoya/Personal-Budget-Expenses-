package com.example.personalbudgetingapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpendingAvtivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView totalAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expenseRef;

    private TodayitemsAdapter todayitemsAdapter;
    private List<Data> myDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending_avtivity);

        getSupportActionBar().hide();

        toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today Spending");
        totalAmountSpentOn=findViewById(R.id.totalAmountSpentOn);
        progressBar=findViewById(R.id.progressBar);

        fab=findViewById(R.id.fab);
        loader=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        expenseRef= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);

        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList=new ArrayList<>();
        todayitemsAdapter=new TodayitemsAdapter(TodaySpendingAvtivity.this,myDataList);
        recyclerView.setAdapter(todayitemsAdapter);

        readItem();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemSpentOn();
            }
        });
    }

    private void readItem() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data =dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                todayitemsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount =0;
                for (DataSnapshot ds :snapshot.getChildren()){
                    Map<String,Object> map = (Map<String,Object>)ds.getValue();
                    Object total = map.get("amount");
                  int pTotal=Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalAmountSpentOn.setText("Total day's Spending: Rs."+totalAmount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addItemSpentOn()
        {
            AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View myView = inflater.inflate(R.layout.input_layout, null);
            myDialog.setView(myView);

            final AlertDialog dialog = myDialog.create();
            dialog.setCancelable(false);

            final Spinner itemSpinner = myView.findViewById(R.id.itemsspinner);
            final EditText amount = myView.findViewById(R.id.amount);
            final EditText note = myView.findViewById(R.id.note);
            final Button cancel = myView.findViewById(R.id.cancel);
            final Button save = myView.findViewById(R.id.save);

            note.setVisibility(View.VISIBLE);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String Amount = amount.getText().toString();
                    String Item = itemSpinner.getSelectedItem().toString();
                    String notes =note.getText().toString();

                    if (TextUtils.isEmpty(Amount)) {
                        amount.setError("Amount is required");
                        return;
                    }
                    if (Item.equals("Select Item!!")) {
                        Toast.makeText(TodaySpendingAvtivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(notes)){
                        note.setError("Note is Requried");
                        return;
                    }

                    else {
                        loader.setMessage("Adding a budget item");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();

                        String id = expenseRef.push().getKey();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar cal = Calendar.getInstance();
                        String date = dateFormat.format(cal.getTime());

                        MutableDateTime epoch = new MutableDateTime();
                        epoch.setDate(0);
                        DateTime now = new DateTime();
                        Weeks weeks =Weeks.weeksBetween(epoch,now);
                        Months months = Months.monthsBetween(epoch, now);

                        String itemNday =Item+date;
                        String itemNweek=Item+weeks.getWeeks();
                        String itemNmonth=Item+months.getMonths();


                        Data data = new Data(Item, date, id, itemNday,itemNweek,itemNmonth, Integer.parseInt(Amount), weeks.getWeeks(), months.getMonths(),notes);
                        expenseRef.child(id).setValue(data).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(TodaySpendingAvtivity.this, "Budget item added Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TodaySpendingAvtivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        });
                    }
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }

}