package com.example.personalbudgetingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private Toolbar my_Feed_Toolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, PersonalRef;

    private TextView monthRatioSpending, monthSpentAmount;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount, analyticsFoodAmount, analyticsHouseExpensesAmount;
    private TextView analyticsEntertainmentAmount, analyticsEducationAmount, analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount, analyticsPersonalExpensesAmount, analyticsOtherAmount;

    private RelativeLayout linearLayoutTransport, linearLayoutFood, linearLayoutHouse, linearLayoutEntertainment, linearLayoutEducation, linearLayoutAnalytics;
    private RelativeLayout linearLayoutCharity, linearLayoutApparel, linearLayoutHealth, linearLayoutPersonalExp, linearLayoutOther;

    private AnyChartView anyChartView;
    private TextView progress_ratio_transport, progress_ratio_food, progress_ratio_house, progress_ratio_ent, progress_ratio_edu, progress_ratio_cha, progress_ratio_app, progress_ratio_hea, progress_ratio_per, progress_ratio_oth;
    private ImageView status_Image_transport, status_Image_food, status_Image_house, status_Image_ent, status_Image_edu, status_Image_cha, status_Image_app, status_Image_hea, status_Image_per, status_Image_oth, monthRatioSpending_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        PersonalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);

        //general analytics
        linearLayoutAnalytics = findViewById(R.id.linearLayoutAnalytics);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);
        monthSpentAmount = findViewById(R.id.monthSpentAmount);

        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount = findViewById(R.id.analyticsHouseExpensesAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalExpensesAmount = findViewById(R.id.analyticsPersonalExpensesAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);


        //RelativeLayout

        linearLayoutTransport = findViewById(R.id.linearLayoutTransport);
        linearLayoutFood = findViewById(R.id.linearLayoutFood);
        linearLayoutHouse = findViewById(R.id.linearLayoutHouse);
        linearLayoutEntertainment = findViewById(R.id.linearLayoutEntertainment);
        linearLayoutEducation = findViewById(R.id.linearLayoutEducation);
        linearLayoutCharity = findViewById(R.id.linearLayoutCharity);
        linearLayoutApparel = findViewById(R.id.linearLayoutApparel);
        linearLayoutHealth = findViewById(R.id.linearLayoutHealth);
        linearLayoutPersonalExp = findViewById(R.id.linearLayoutPersonalExp);
        linearLayoutOther = findViewById(R.id.linearLayoutOther);

        anyChartView = findViewById(R.id.anyChartView);

        //TextVieW
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_ent);
        progress_ratio_edu = findViewById(R.id.progress_ratio_edu);
        progress_ratio_cha = findViewById(R.id.progress_ratio_cha);
        progress_ratio_app = findViewById(R.id.progress_ratio_app);
        progress_ratio_hea = findViewById(R.id.progress_ratio_hea);
        progress_ratio_per = findViewById(R.id.progress_ratio_per);
        progress_ratio_oth = findViewById(R.id.progress_ratio_oth);


        //IMAGE VIEW
        status_Image_transport = findViewById(R.id.status_Image_transport);
        status_Image_food = findViewById(R.id.status_Image_food);
        status_Image_house = findViewById(R.id.status_Image_house);
        status_Image_ent = findViewById(R.id.status_Image_ent);
        status_Image_edu = findViewById(R.id.status_Image_edu);
        status_Image_cha = findViewById(R.id.status_Image_cha);
        status_Image_app = findViewById(R.id.status_Image_app);
        status_Image_hea = findViewById(R.id.status_Image_hea);
        status_Image_per = findViewById(R.id.status_Image_per);
        status_Image_oth = findViewById(R.id.status_Image_oth);

        getTotaldailyTransportExpenses();
        getTotaldailyFoodExpenses();
        getTotaldailyHouseExpenses();
        getTotaldailyEntertainmentExpenses();
        getTotaldailyEducationExpenses();
        getTotaldailyCharityExpenses();
        getTotaldailyApparelExpenses();
        getTotaldailyHealthExpenses();
        getTotaldailyPersonalExpenses();
        getTotaldailyOtherExpenses();
        getTotalDaySpending();
        

       new java.util.Timer().schedule(new TimerTask() {
           @Override
           public void run() {
               loadGraph();
               setStatusAndImageResource();
           }

       },2000
       );
       
    }

    private void getTotaldailyTransportExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Transport"+date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                       int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayTrans").setValue(totalAmount);
                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotaldailyFoodExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Food"+date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayFood").setValue(totalAmount);
                } else {
                    linearLayoutFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotaldailyHouseExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "House" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHouseExpensesAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayHouse").setValue(totalAmount);
                } else {
                    linearLayoutHouse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyEntertainmentExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEntertainmentAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayEnt").setValue(totalAmount);
                } else {
                    linearLayoutEntertainment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyEducationExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEducationAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayEdu").setValue(totalAmount);
                } else {
                    linearLayoutEducation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyCharityExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsCharityAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayCharity").setValue(totalAmount);
                } else {
                    linearLayoutCharity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyApparelExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsApparelAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayApp").setValue(totalAmount);
                } else {
                    linearLayoutApparel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyHealthExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHealthAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayHealth").setValue(totalAmount);
                } else {
                    linearLayoutHealth.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyPersonalExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Personal" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPersonalExpensesAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayPer").setValue(totalAmount);
                } else {
                    linearLayoutPersonalExp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotaldailyOtherExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsOtherAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("dayOther").setValue(totalAmount);
                } else {
                    linearLayoutOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalDaySpending() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    totalBudgetAmountTextView.setText("Total day's Spending: Rs " + totalAmount);
                    monthSpentAmount.setText("Total Spent: Rs " + totalAmount);
                } else {
                    totalBudgetAmountTextView.setText("You have not Spent Today");
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadGraph() {
        PersonalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }
                    int foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    int houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }
                    int entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }
                    int eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }
                    int chaTotal;
                    if (snapshot.hasChild("dayCharity")) {
                        chaTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }
                    int appTotal;
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }
                    int heaTotal;
                    if (snapshot.hasChild("dayHealth")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }
                    int perTotal;
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }
                    int othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("House", houseTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", chaTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Health", heaTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("Other", othTotal));

                    pie.data(data);
                    pie.title("Daily Analytics");
                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Item Spent On")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                } else {
                    Toast.makeText(DailyAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatusAndImageResource() {
        PersonalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }
                    float foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    float houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }
                    float entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }
                    float eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }
                    float chaTotal;
                    if (snapshot.hasChild("dayCharity")) {
                        chaTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }
                    float appTotal;
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }
                    float heaTotal;
                    if (snapshot.hasChild("dayHealth")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }
                    float perTotal;
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }
                    float othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("today")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }


                    //GEETING RATIO 
                    float traRatio;
                    if (snapshot.hasChild("dayTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }
                    float foodRatio;
                    if (snapshot.hasChild("dayFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }
                    float houseRatio;
                    if (snapshot.hasChild("dayHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }
                    float entRatio;
                    if (snapshot.hasChild("dayEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("dayEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }
                    float eduRatio;
                    if (snapshot.hasChild("dayEduRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("dayEduRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }
                    float chaRatio;
                    if (snapshot.hasChild("dayChaRatio")) {
                        chaRatio = Integer.parseInt(snapshot.child("dayChaRatio").getValue().toString());
                    } else {
                        chaRatio = 0;
                    }
                    float appRatio;
                    if (snapshot.hasChild("dayAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("dayAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }
                    float heaRatio;
                    if (snapshot.hasChild("dayHealthRatio")) {
                        heaRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                    } else {
                        heaRatio = 0;
                    }
                    float perRatio;
                    if (snapshot.hasChild("dayPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("dayPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }
                    float othRatio;
                    if (snapshot.hasChild("dayOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("dailyBudget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }


                    float monthPercent = (monthTotalSpentAmount / monthTotalSpentAmountRatio) * 100;
                    if (monthPercent < 50) {
                        monthRatioSpending.setText(monthPercent + " %" + " used of " + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    } else if (monthPercent >= 50 && monthPercent < 100) {
                        monthRatioSpending.setText(monthPercent + " %" + " used of " + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    } else {
                        monthRatioSpending.setText(monthPercent + " %" + " used of " + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);
                    }

                    float transportPercent = (traTotal / traRatio) * 100;
                    if (transportPercent < 50) {
                        progress_ratio_transport.setText(transportPercent + " %" + " used of " + traRatio + ". Status:");
                        status_Image_transport.setImageResource(R.drawable.green);
                    } else if (transportPercent >= 50 && transportPercent < 100) {
                        progress_ratio_transport.setText(transportPercent + " %" + " used of " + traRatio + ". Status:");
                        status_Image_transport.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_transport.setText(transportPercent + " %" + " used of " + traRatio + ". Status:");
                        status_Image_transport.setImageResource(R.drawable.red);
                    }

                    float FoodPercent = (foodTotal / foodRatio) * 100;
                    if (FoodPercent < 50) {
                        progress_ratio_food.setText(FoodPercent + " %" + " used of " + foodRatio + ". Status:");
                        status_Image_food.setImageResource(R.drawable.green);
                    } else if (FoodPercent >= 50 && FoodPercent < 100) {
                        progress_ratio_food.setText(FoodPercent + " %" + " used of " + foodRatio + ". Status:");
                        status_Image_food.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_food.setText(FoodPercent + " %" + " used of " + foodRatio + ". Status:");
                        status_Image_food.setImageResource(R.drawable.red);
                    }

                    float HousePercent = (houseTotal / houseRatio) * 100;
                    if (HousePercent < 50) {
                        progress_ratio_house.setText(HousePercent + " %" + " used of " + houseRatio + ". Status:");
                        status_Image_house.setImageResource(R.drawable.green);
                    } else if (HousePercent >= 50 && HousePercent < 100) {
                        progress_ratio_house.setText(HousePercent + " %" + " used of " + houseRatio + ". Status:");
                        status_Image_house.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_house.setText(HousePercent + " %" + " used of " + houseRatio + ". Status:");
                        status_Image_house.setImageResource(R.drawable.red);
                    }

                    float entPercent = (entTotal / entRatio) * 100;
                    if (entPercent < 50) {
                        progress_ratio_ent.setText(entPercent + " %" + " used of " + entRatio + ". Status:");
                        status_Image_ent.setImageResource(R.drawable.green);
                    } else if (entPercent >= 50 && entPercent < 100) {
                        progress_ratio_ent.setText(entPercent + " %" + " used of " + entRatio + ". Status:");
                        status_Image_ent.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_ent.setText(entPercent + " %" + " used of " + entRatio + ". Status:");
                        status_Image_ent.setImageResource(R.drawable.red);
                    }

                    float eduPercent = (eduTotal / eduRatio) * 100;
                    if (eduPercent < 50) {
                        progress_ratio_edu.setText(eduPercent + " %" + " used of " + eduRatio + ". Status:");
                        status_Image_edu.setImageResource(R.drawable.green);
                    } else if (eduPercent >= 50 && eduPercent < 100) {
                        progress_ratio_edu.setText(eduPercent + " %" + " used of " + eduRatio + ". Status:");
                        status_Image_edu.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_edu.setText(eduPercent + " %" + " used of " + eduRatio + ". Status:");
                        status_Image_edu.setImageResource(R.drawable.red);
                    }

                    float ChaPercent = (chaTotal / chaRatio) * 100;
                    if (ChaPercent < 50) {
                        progress_ratio_cha.setText(ChaPercent + " %" + " used of " + chaRatio + ". Status:");
                        status_Image_cha.setImageResource(R.drawable.green);
                    } else if (ChaPercent >= 50 && ChaPercent < 100) {
                        progress_ratio_cha.setText(ChaPercent + " %" + " used of " + chaRatio + ". Status:");
                        status_Image_cha.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_cha.setText(ChaPercent + " %" + " used of " + chaRatio + ". Status:");
                        status_Image_cha.setImageResource(R.drawable.red);
                    }

                    float appPercent = (appTotal / appRatio) * 100;
                    if (appPercent < 50) {
                        progress_ratio_app.setText(appPercent + " %" + " used of " + appRatio + ". Status:");
                        status_Image_app.setImageResource(R.drawable.green);
                    } else if (appPercent >= 50 && appPercent < 100) {
                        progress_ratio_app.setText(appPercent + " %" + " used of " + appRatio + ". Status:");
                        status_Image_app.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_app.setText(appPercent + " %" + " used of " + appRatio + ". Status:");
                        status_Image_app.setImageResource(R.drawable.red);
                    }

                    float heaPercent = (heaTotal / heaRatio) * 100;
                    if (heaPercent < 50) {
                        progress_ratio_hea.setText(heaPercent + " %" + " used of " + heaRatio + ". Status:");
                        status_Image_hea.setImageResource(R.drawable.green);
                    } else if (heaPercent >= 50 && heaPercent < 100) {
                        progress_ratio_hea.setText(heaPercent + " %" + " used of " + heaRatio + ". Status:");
                        status_Image_hea.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_hea.setText(heaPercent + " %" + " used of " + heaRatio + ". Status:");
                        status_Image_hea.setImageResource(R.drawable.red);
                    }

                    float perPercent = (perTotal / perRatio) * 100;
                    if (perPercent < 50) {
                        progress_ratio_per.setText(perPercent + " %" + " used of " + perRatio + ". Status:");
                        status_Image_per.setImageResource(R.drawable.green);
                    } else if (perPercent >= 50 && perPercent < 100) {
                        progress_ratio_per.setText(perPercent + " %" + " used of " + perRatio + ". Status:");
                        status_Image_per.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_per.setText(perPercent + " %" + " used of " + perRatio + ". Status:");
                        status_Image_per.setImageResource(R.drawable.red);
                    }

                    float othPercent = (othTotal / othRatio) * 100;
                    if (othPercent < 50) {
                        progress_ratio_oth.setText(othPercent + " %" + " used of " + othRatio + ". Status:");
                        status_Image_oth.setImageResource(R.drawable.green);
                    } else if (othPercent >= 50 && othPercent < 100) {
                        progress_ratio_oth.setText(othPercent + " %" + " used of " + othRatio + ". Status:");
                        status_Image_oth.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_oth.setText(othPercent + " %" + " used of " + othRatio + ". Status:");
                        status_Image_oth.setImageResource(R.drawable.red);
                    }
                } else {
                    Toast.makeText(DailyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

