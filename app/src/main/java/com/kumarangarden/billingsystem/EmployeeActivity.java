package com.kumarangarden.billingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by 11000257 on 8/21/2017.
 */

public class EmployeeActivity extends AppCompatActivity {

    TextView employeeName;
    NumberPicker month, year;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Intent intent = getIntent();
        String message = intent.getStringExtra("Name");
        employeeName = (TextView) findViewById(R.id.labelName);

        employeeName.setText(message);

        month = (NumberPicker) findViewById(R.id.numbermonth);
        month.setMinValue(1);
        month.setMaxValue(12);
        year = (NumberPicker) findViewById(R.id.numberyear);
        year.setMinValue(2017);
        year.setMaxValue(2050);
    }
}
