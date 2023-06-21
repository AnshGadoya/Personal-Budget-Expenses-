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
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class WeeklyAnalyticsActivity extends AppCompatActivity {

    private Toolbar my_Feed_Toolbar;

   
    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expensesRef,PersonalRef;

    private TextView monthRatioSpending,monthSpentAmount;

    private TextView totalBudgetAmountTextView,analyticsTransportAmount,analyticsFoodAmount,analyticsHouseExpensesAmount;
    private TextView analyticsEntertainmentAmount,analyticsEducationAmount,analyticsCharityAmount,analyticsApparelAmount,analyticsHealthAmount,analyticsPersonalExpensesAmount,analyticsOtherAmount;

    private RelativeLayout linearLayoutTransport,linearLayoutFood,linearLayoutHouse,linearLayoutEntertainment,linearLayoutEducation,linearLayoutAnalytics;
    private RelativeLayout linearLayoutCharity,linearLayoutApparel,linearLayoutHealth,linearLayoutPersonalExp,linearLayoutOther;

    private AnyChartView anyChartView;
    private TextView progress_ratio_transport,progress_ratio_food,progress_ratio_house,progress_ratio_ent,progress_ratio_edu,progress_ratio_cha,progress_ratio_app,progress_ratio_hea,progress_ratio_per,progress_ratio_oth;
    private ImageView status_Image_transport,status_Image_food,status_Image_house,status_Image_ent,status_Image_edu,status_Image_cha,status_Image_app,status_Image_hea,status_Image_per,status_Image_oth,monthRatioSpending_Image ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_analytics);

        getSupportActionBar().hide();
        mAuth= FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        expensesRef= FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        PersonalRef= FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        totalBudgetAmountTextView=findViewById(R.id.totalBudgetAmountTextView);

        //general analytics
        linearLayoutAnalytics=findViewById(R.id.linearLayoutAnalytics);
        monthRatioSpending=findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image=findViewById(R.id.monthRatioSpending_Image);
        monthSpentAmount=findViewById(R.id.monthSpentAmount);

        analyticsTransportAmount=findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount=findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount=findViewById(R.id.analyticsHouseExpensesAmount);
        analyticsEntertainmentAmount=findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount=findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount=findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount=findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount=findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalExpensesAmount=findViewById(R.id.analyticsPersonalExpensesAmount);
        analyticsOtherAmount=findViewById(R.id.analyticsOtherAmount);


        //RelativeLayout

        linearLayoutTransport=findViewById(R.id.linearLayoutTransport);
        linearLayoutFood=findViewById(R.id.linearLayoutFood);
        linearLayoutHouse=findViewById(R.id.linearLayoutHouse);
        linearLayoutEntertainment=findViewById(R.id.linearLayoutEntertainment);
        linearLayoutEducation=findViewById(R.id.linearLayoutEducation);
        linearLayoutCharity=findViewById(R.id.linearLayoutCharity);
        linearLayoutApparel=findViewById(R.id.linearLayoutApparel);
        linearLayoutHealth=findViewById(R.id.linearLayoutHealth);
        linearLayoutPersonalExp=findViewById(R.id.linearLayoutPersonalExp);
        linearLayoutOther=findViewById(R.id.linearLayoutOther);

        anyChartView=findViewById(R.id.anyChartView);

        //TextVieW
        progress_ratio_transport=findViewById(R.id.progress_ratio_transport);
        progress_ratio_food=findViewById(R.id.progress_ratio_food);
        progress_ratio_house=findViewById(R.id.progress_ratio_house);
        progress_ratio_ent=findViewById(R.id.progress_ratio_ent);
        progress_ratio_edu=findViewById(R.id.progress_ratio_edu);
        progress_ratio_cha=findViewById(R.id.progress_ratio_cha);
        progress_ratio_app=findViewById(R.id.progress_ratio_app);
        progress_ratio_hea=findViewById(R.id.progress_ratio_hea);
        progress_ratio_per=findViewById(R.id.progress_ratio_per);
        progress_ratio_oth=findViewById(R.id.progress_ratio_oth);


        //IMAGE VIEW
        status_Image_transport=findViewById(R.id.status_Image_transport);
        status_Image_food=findViewById(R.id.status_Image_food);
        status_Image_house=findViewById(R.id.status_Image_house);
        status_Image_ent=findViewById(R.id.status_Image_ent);
        status_Image_edu=findViewById(R.id.status_Image_edu);
        status_Image_cha=findViewById(R.id.status_Image_cha);
        status_Image_app=findViewById(R.id.status_Image_app);
        status_Image_hea=findViewById(R.id.status_Image_hea);
        status_Image_per=findViewById(R.id.status_Image_per);
        status_Image_oth=findViewById(R.id.status_Image_oth);



        getTotalweekTransportExpenses();
        getTotalweekFoodExpenses();
        getTotalweekHouseExpenses();
        getTotalweekEntertainmentExpenses();
        getTotalweekEducationExpenses();
        getTotalweekCharityExpenses();
        getTotalweekApparelExpenses();
        getTotalweekHealthExpenses();
        getTotalweekPersonalExpenses();
        getTotalweekOtherExpenses();
        getTotalweekSpending();


        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResource();
            }

            },2000
        );
    }

    private void getTotalweekTransportExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Transport"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                    PersonalRef.child("weekTrans").setValue(totalAmount);
                }
                else {
                    linearLayoutTransport.setVisibility(View.GONE);
                    PersonalRef.child("weekTrans").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekFoodExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Food"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsFoodAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekFood").setValue(totalAmount);
                }
                else {
                    linearLayoutFood.setVisibility(View.GONE);
                    PersonalRef.child("weekFood").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekHouseExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="House"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsHouseExpensesAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekHouse").setValue(totalAmount);
                }
                else {
                    linearLayoutHouse.setVisibility(View.GONE);
                    PersonalRef.child("weekHouse").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekEntertainmentExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Entertainment"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsEntertainmentAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekEnt").setValue(totalAmount);
                }
                else {
                    linearLayoutEntertainment.setVisibility(View.GONE);
                    PersonalRef.child("weekEnt").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekEducationExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Education"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsEducationAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekEdu").setValue(totalAmount);
                }
                else {
                    linearLayoutEducation.setVisibility(View.GONE);
                    PersonalRef.child("weekEdu").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekCharityExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Charity"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsCharityAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekCha").setValue(totalAmount);
                }
                else {
                    linearLayoutCharity.setVisibility(View.GONE);
                    PersonalRef.child("weekCha").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekApparelExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Apparel"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsApparelAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekApp").setValue(totalAmount);
                }
                else {
                    linearLayoutApparel.setVisibility(View.GONE);
                    PersonalRef.child("weekApp").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekHealthExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Health"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsHealthAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekHea").setValue(totalAmount);
                }
                else {
                    linearLayoutHealth.setVisibility(View.GONE);
                    PersonalRef.child("weekHea").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekPersonalExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Personal"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsPersonalExpensesAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekPer").setValue(totalAmount);
                }
                else {
                    linearLayoutPersonalExp.setVisibility(View.GONE);
                    PersonalRef.child("weekPer").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekOtherExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek ="Other"+weeks.getWeeks();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                        analyticsOtherAmount.setText("Spents " + totalAmount);
                    }
                    PersonalRef.child("weekOth").setValue(totalAmount);
                }
                else {
                    linearLayoutOther.setVisibility(View.GONE);
                    PersonalRef.child("weekOth").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalweekSpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expense").child(onlineUserId);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
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
                    }
                    totalBudgetAmountTextView.setText("Total day's Spending: Rs " + totalAmount);
                    monthSpentAmount.setText("Total Spent: Rs " + totalAmount);
                }
                else {
                   anyChartView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGraph() {
        PersonalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int traTotal;
                    if (snapshot.hasChild("weekTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("weekTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }
                    int foodTotal;
                    if (snapshot.hasChild("weekFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("weekFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    int houseTotal;
                    if (snapshot.hasChild("weekHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("weekHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }
                    int entTotal;
                    if (snapshot.hasChild("weekEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("weekEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }
                    int eduTotal;
                    if (snapshot.hasChild("weekEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("weekEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }
                    int chaTotal;
                    if (snapshot.hasChild("weekCharity")) {
                        chaTotal = Integer.parseInt(snapshot.child("weekCharity").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }
                    int appTotal;
                    if (snapshot.hasChild("weekApp")) {
                        appTotal = Integer.parseInt(snapshot.child("weekApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }
                    int heaTotal;
                    if (snapshot.hasChild("weekHealth")) {
                        heaTotal = Integer.parseInt(snapshot.child("weekHealth").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }
                    int perTotal;
                    if (snapshot.hasChild("weekPer")) {
                        perTotal = Integer.parseInt(snapshot.child("weekPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }
                    int othTotal;
                    if (snapshot.hasChild("weekOther")) {
                        othTotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
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
                    pie.title("Week Analytics");
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
                    Toast.makeText(WeeklyAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void setStatusAndImageResource() {
        PersonalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("weekTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("weekTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("weekFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("weekFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("weekHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("weekHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("weekEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("weekEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("weekEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("weekEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    float chaTotal;
                    if (snapshot.hasChild("weekCharity")) {
                        chaTotal = Integer.parseInt(snapshot.child("weekCharity").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("weekApp")) {
                        appTotal = Integer.parseInt(snapshot.child("weekApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("weekHealth")) {
                        heaTotal = Integer.parseInt(snapshot.child("weekHealth").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("weekPer")) {
                        perTotal = Integer.parseInt(snapshot.child("weekPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("weekOther")) {
                        othTotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("week")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("week").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }


                    //GEETING RATIO
                    float traRatio;
                    if (snapshot.hasChild("weekTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("weekTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }
                    float foodRatio;
                    if (snapshot.hasChild("weekFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("weekFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }
                    float houseRatio;
                    if (snapshot.hasChild("weekHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("weekHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }
                    float entRatio;
                    if (snapshot.hasChild("weekEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("weekEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }
                    float eduRatio;
                    if (snapshot.hasChild("weekEduRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("weekEduRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }
                    float chaRatio;
                    if (snapshot.hasChild("weekChaRatio")) {
                        chaRatio = Integer.parseInt(snapshot.child("weekChaRatio").getValue().toString());
                    } else {
                        chaRatio = 0;
                    }
                    float appRatio;
                    if (snapshot.hasChild("weekAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("weekAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }
                    float heaRatio;
                    if (snapshot.hasChild("weekHealthRatio")) {
                        heaRatio = Integer.parseInt(snapshot.child("weekHealthRatio").getValue().toString());
                    } else {
                        heaRatio = 0;
                    }
                    float perRatio;
                    if (snapshot.hasChild("weekPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("weekPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }
                    float othRatio;
                    if (snapshot.hasChild("weekOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("weekOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("weeklyBudget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("weeklyBudget").getValue().toString());
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
                    Toast.makeText(WeeklyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}