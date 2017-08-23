package com.kumarangarden.billingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_Model.Sale;
import com.kumarangarden.billingsystem.m_UI.SaleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by kanna_000 on 23-08-2017.
 */

public class CustomerActivity extends AppCompatActivity {

    private TextView customerName;
    private NumberPicker month, year;
    private String stDate, edDate;

    private List<Sale> sales = new ArrayList<>();

    private RecyclerView salesView;
    private SaleAdapter salesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Intent intent = getIntent();

        final String custName = intent.getStringExtra("Name");
        customerName = (TextView) findViewById(R.id.labelName);
        customerName.setText(custName);

        salesView = (RecyclerView) findViewById(R.id.salesView);
        salesAdapter = new SaleAdapter(sales);
        salesAdapter.name = custName;

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        salesView.setLayoutManager(mLayoutManager);
        salesView.setItemAnimator(new DefaultItemAnimator());
        salesView.setAdapter(salesAdapter);

        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        month = (NumberPicker) findViewById(R.id.numbermonth);
        month.setMinValue(1);
        month.setMaxValue(12);
        month.setValue(calendar.get(Calendar.MONTH) + 1);

        year = (NumberPicker) findViewById(R.id.numberyear);
        year.setMinValue(2017);
        year.setMaxValue(2050);
        year.setValue(calendar.get(Calendar.YEAR));

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                UpdateDateRange();
            }
        });

        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                UpdateDateRange();
            }
        });

        UpdateDateRange();
    }

    private void UpdateDateRange() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String startDate = String.format("%04d%02d%02d", year.getValue(), month.getValue(), 1);
        String endDate = String.format("%04d%02d%02d", year.getValue(), month.getValue(), GetMonthDays(year.getValue(), month.getValue()-1));

        String name = customerName.getText().toString();


        sales.clear();
        String baseKey = "Purchases/" + name;

        Query query = db.child(baseKey)
                .orderByKey()
                .startAt(startDate)
                .endAt(endDate);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dateRef : dataSnapshot.getChildren())
                {
                    for(DataSnapshot timeRef : dateRef.getChildren()) {
                        Sale sale = new Sale();
                        sale.date = dateRef.getKey();
                        sale.time = timeRef.getKey();
                        sales.add(sale);
                    }
                }
                salesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private int GetMonthDays(int year, int month) {
        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(year, month, 1);
        // Get the number of days in that month
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
    }
}
