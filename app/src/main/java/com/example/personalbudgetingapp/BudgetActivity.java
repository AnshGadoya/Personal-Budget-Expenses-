package com.example.personalbudgetingapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Calendar;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private TextView totalBudgetAmountTextView;
    private RecyclerView recyclerView;
  //  Animation animation;
    private FloatingActionButton fab;

    private DatabaseReference budgetRef,PersonalRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;


    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        getSupportActionBar().hide();

       // animation= AnimationUtils.loadAnimation(this,R.anim.animation);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        PersonalRef = FirebaseDatabase.getInstance().getReference().child("personal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalamt = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    totalamt += data.getAmount();
                    String sTotal = String.valueOf("Month Budget: Rs" + totalamt);
                    totalBudgetAmountTextView.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> additem());

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    int totalAmount = 0;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Data data =snap.getValue(Data.class);
                        totalAmount+= data.getAmount();
                        String stTotal=String.valueOf("Month Budget "+totalAmount);
                        totalBudgetAmountTextView.setText(stTotal);
                    }
                    int weeklyBudget = totalAmount/4;
                    int dailtyBudget = totalAmount/30;
                    PersonalRef.child("budget").setValue(totalAmount);
                    PersonalRef.child("weeklyBudget").setValue(weeklyBudget);
                    PersonalRef.child("dailtyBudget").setValue(dailtyBudget);

                }else {
                    PersonalRef.child("budget").setValue(0);
                    PersonalRef.child("weeklyBudget").setValue(0);
                    PersonalRef.child("dailtyBudget").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        getMonthTransportBudgetRatio();
        getMonthFoodBudgetRatio();
        getMonthHouseBudgetRatio();
        getMonthEntertainmentBudgetRatio();
        getMonthEducationBudgetRatio();
        getMonthCharityBudgetRatio();
        getMonthApparelBudgetRatio();
        getMonthHealthBudgetRatio();
        getMonthPersonalBudgetRatio();
        getMonthOtherBudgetRatio();

    }


    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemsspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(budgetAmount)) {
                    amount.setError("Amount is required");
                    return;
                }
                if (budgetItem.equals("Select Item!!")) {
                    Toast.makeText(BudgetActivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks =Weeks.weeksBetween(epoch,now);
                    Months months = Months.monthsBetween(epoch, now);

                    String itemNday =budgetItem+date;
                    String itemNweek=budgetItem+weeks.getWeeks();
                    String itemNmonth=budgetItem+months.getMonths();

                    Data data = new Data(budgetItem, date, id, itemNday,itemNweek,itemNmonth, Integer.parseInt(budgetAmount), weeks.getWeeks(), months.getMonths(),null);
                    budgetRef.child(id).setValue(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Budget item added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef, Data.class).build();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {

                holder.setItemAmount("Allocated Amount: Rs" + model.getAmount());
                holder.setItemDate("On: " + model.getDate());
                holder.setItemName("BudgetItem: " + model.getItem());

                holder.notes.setVisibility(View.GONE);

                switch (model.getItem()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;

                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;

                    case "House":
                        holder.imageView.setImageResource(R.drawable.ic_house);
                        break;

                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;

                    case "Education":
                        holder.imageView.setImageResource(R.drawable.ic_education);
                        break;

                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.ic_consultancy);
                        break;

                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.ic_shirt);
                        break;

                    case "Health":
                        holder.imageView.setImageResource(R.drawable.ic_health);
                        break;

                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.ic_personalcare);
                        break;

                    case "Other":
                        holder.imageView.setImageResource(R.drawable.ic_other);
                        break;
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=getRef(holder.getAdapterPosition()).getKey();
                        item= model.getItem();
                        amount= model.getAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
               public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
              }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView notes,date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageview);
            notes = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);

        }

        public void setItemName(String itemName) {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount) {

            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setItemDate(String itemDate) {
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }
    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout,null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);
        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button delBut=mView.findViewById(R.id.btnDelete);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                amount=Integer.parseInt(mAmount.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks =Weeks.weeksBetween(epoch,now);
                Months months = Months.monthsBetween(epoch, now);


                String itemNday =item+date;
                String itemNweek=item+weeks.getWeeks();
                String itemNmonth=item+months.getMonths();

                Data data = new Data(item, date, post_key, itemNday,itemNweek,itemNmonth, amount, weeks.getWeeks(), months.getMonths(),null);
                budgetRef.child(post_key).setValue(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BudgetActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
                dialog.dismiss();
            }
        });

        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BudgetActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getMonthTransportBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Transport");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                         pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayTransRatio = pTotal/30;
                    int weekTransRatio = pTotal/4;
                    int monthTransRatio = pTotal;

                    PersonalRef.child("dayTransRatio").setValue(dayTransRatio);
                    PersonalRef.child("weekTransRatio").setValue(weekTransRatio);
                    PersonalRef.child("monthTransRatio").setValue(monthTransRatio);
                }else {
                    PersonalRef.child("dayTransRatio").setValue(0);
                    PersonalRef.child("weekTransRatio").setValue(0);
                    PersonalRef.child("monthTransRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthFoodBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayFoodRatio = pTotal/30;
                    int weekFoodRatio = pTotal/4;
                    int monthFoodRatio = pTotal;

                    PersonalRef.child("dayFoodRatio").setValue(dayFoodRatio);
                    PersonalRef.child("weekFoodRatio").setValue(weekFoodRatio);
                    PersonalRef.child("monthFoodRatio").setValue(monthFoodRatio);
                }else {
                    PersonalRef.child("dayFoodRatio").setValue(0);
                    PersonalRef.child("weekFoodRatio").setValue(0);
                    PersonalRef.child("monthFoodRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getMonthHouseBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("House");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayHouseRatio = pTotal/30;
                    int weekHouseRatio = pTotal/4;
                    int monthHouseRatio = pTotal;

                    PersonalRef.child("dayHouseRatio").setValue(dayHouseRatio);
                    PersonalRef.child("weekHouseRatio").setValue(weekHouseRatio);
                    PersonalRef.child("monthHouseRatio").setValue(monthHouseRatio);
                }else {
                    PersonalRef.child("dayHouseRatio").setValue(0);
                    PersonalRef.child("weekHouseRatio").setValue(0);
                    PersonalRef.child("monthHouseRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEntertainmentBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Entertainment");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayEntRatio = pTotal/30;
                    int weekEntRatio = pTotal/4;
                    int monthEntRatio = pTotal;

                    PersonalRef.child("dayEntRatio").setValue(dayEntRatio);
                    PersonalRef.child("weekEntRatio").setValue(weekEntRatio);
                    PersonalRef.child("monthEntRatio").setValue(monthEntRatio);
                }else {
                    PersonalRef.child("dayEntRatio").setValue(0);
                    PersonalRef.child("weekEntRatio").setValue(0);
                    PersonalRef.child("monthEntRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEducationBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Education");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayEduRatio = pTotal/30;
                    int weekEduRatio = pTotal/4;
                    int monthEduRatio = pTotal;

                    PersonalRef.child("dayEduRatio").setValue(dayEduRatio);
                    PersonalRef.child("weekEduRatio").setValue(weekEduRatio);
                    PersonalRef.child("monthEduRatio").setValue(monthEduRatio);
                }else {
                    PersonalRef.child("dayEduRatio").setValue(0);
                    PersonalRef.child("weekEduRatio").setValue(0);
                    PersonalRef.child("monthEduRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthCharityBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Charity");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayChaRatio = pTotal/30;
                    int weekChaRatio = pTotal/4;
                    int monthChaRatio = pTotal;

                    PersonalRef.child("dayChaRatio").setValue(dayChaRatio);
                    PersonalRef.child("weekChaRatio").setValue(weekChaRatio);
                    PersonalRef.child("monthChaRatio").setValue(monthChaRatio);
                }else {
                    PersonalRef.child("dayChaRatio").setValue(0);
                    PersonalRef.child("weekChaRatio").setValue(0);
                    PersonalRef.child("monthChaRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthApparelBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Apparel");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayAppRatio = pTotal/30;
                    int weekAppRatio = pTotal/4;
                    int monthAppRatio = pTotal;

                    PersonalRef.child("dayAppRatio").setValue(dayAppRatio);
                    PersonalRef.child("weekAppRatio").setValue(weekAppRatio);
                    PersonalRef.child("monthAppRatio").setValue(monthAppRatio);
                }else {
                    PersonalRef.child("dayAppRatio").setValue(0);
                    PersonalRef.child("weekAppRatio").setValue(0);
                    PersonalRef.child("monthAppRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHealthBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Health");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayHealthRatio = pTotal/30;
                    int weekHealthRatio = pTotal/4;
                    int monthHealthRatio = pTotal;

                    PersonalRef.child("dayHealthRatio").setValue(dayHealthRatio);
                    PersonalRef.child("weekHealthRatio").setValue(weekHealthRatio);
                    PersonalRef.child("monthHealthRatio").setValue(monthHealthRatio);
                }else {
                    PersonalRef.child("dayHealthRatio").setValue(0);
                    PersonalRef.child("weekHealthRatio").setValue(0);
                    PersonalRef.child("monthHealthRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthPersonalBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Personal");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayPerRatio = pTotal/30;
                    int weekPerRatio = pTotal/4;
                    int monthPerRatio = pTotal;

                    PersonalRef.child("dayPerRatio").setValue(dayPerRatio);
                    PersonalRef.child("weekPerRatio").setValue(weekPerRatio);
                    PersonalRef.child("monthPerRatio").setValue(monthPerRatio);
                }else {
                    PersonalRef.child("dayPerRatio").setValue(0);
                    PersonalRef.child("weekPerRatio").setValue(0);
                    PersonalRef.child("monthPerRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthOtherBudgetRatio() {
        Query query =budgetRef.orderByChild("item").equalTo("Other");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map = (Map<String,Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal=Integer.parseInt(String.valueOf(total));
                    }

                    int dayOtherRatio = pTotal/30;
                    int weekOtherRatio = pTotal/4;
                    int monthOtherRatio = pTotal;

                    PersonalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    PersonalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    PersonalRef.child("monthOtherRatio").setValue(monthOtherRatio);
                }else {
                    PersonalRef.child("dayOtherRatio").setValue(0);
                    PersonalRef.child("weekOtherRatio").setValue(0);
                    PersonalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
