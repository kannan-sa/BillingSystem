package com.kumarangarden.billingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Leave;
import com.kumarangarden.billingsystem.m_UI.ItemDialog;
import com.kumarangarden.billingsystem.m_UI.LeaveDialog;
import com.kumarangarden.billingsystem.m_UI.LeaveViewHolder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by 11000257 on 8/21/2017.
 */

public class EmployeeActivity extends AppCompatActivity {

    TextView employeeName;
    NumberPicker month, year;

    LeaveDialog newLeave;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView leavesView;
    FirebaseRecyclerAdapter<Leave, LeaveViewHolder> leavesAdapter;

    ImageButton addLeave, addCredit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Intent intent = getIntent();

        String empName = intent.getStringExtra("Name");
        employeeName = (TextView) findViewById(R.id.labelName);
        employeeName.setText(empName);

        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        month = (NumberPicker) findViewById(R.id.numbermonth);
        month.setMinValue(1);
        month.setMaxValue(12);
        month.setValue(calendar.get(Calendar.MONTH) + 1);

        year = (NumberPicker) findViewById(R.id.numberyear);
        year.setMinValue(2017);
        year.setMaxValue(2050);
        year.setValue(calendar.get(Calendar.YEAR));


        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelper(db);


        String startDate = year.getValue() + "-" + (month.getValue() +1) + "-" + 1;
        String endDate = year.getValue() + "-" + (month.getValue() +1) + "-" + 31;

        leavesView = (RecyclerView) findViewById(R.id.leavesView);
        leavesView.setLayoutManager(new LinearLayoutManager(this));

        Query leavesQuery = db.child("Leaves/" + empName).orderByKey().startAt(startDate).endAt(endDate);

        leavesAdapter = new FirebaseRecyclerAdapter<Leave, LeaveViewHolder>(Leave.class,
                R.layout.leave_card, LeaveViewHolder.class, leavesQuery.getRef()) {
            @Override
            protected void populateViewHolder(LeaveViewHolder holder, final Leave item, final int position) {
                DatabaseReference databaseReference = leavesAdapter.getRef(position);
                item.SetKey(databaseReference.getKey());
                try {
                    holder.Initialize(item);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            newLeave.setLeave(item);
                            newLeave.show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
        };


        leavesView.setAdapter(leavesAdapter);

        newLeave = new LeaveDialog(this);
        newLeave.setTitle("Leave");
        newLeave.setContentView(R.layout.leaveform);
        newLeave.InitControls();

        newLeave.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toastMessage = newLeave.getIsValid();

                if(toastMessage.matches(""))    //no error
                {
                    Leave leave = newLeave.getLeave();
                    String key = year.getValue() + "-" + month.getValue() +"-" + newLeave.date.getValue();
                    leave.SetKey(key);
                    if(!helper.save(employeeName.getText().toString(), leave))
                        toastMessage ="Failed Saving";
                    newLeave.clear();
                    newLeave.cancel();
                }
                else
                    Toast.makeText(EmployeeActivity.this, toastMessage, Toast.LENGTH_LONG).show();

            }
        });

        addLeave = (ImageButton) findViewById(R.id.addleave);
        addLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLeave.date.setValue(calendar.get(Calendar.DATE));
                newLeave.show();
            }
        });

    }
}
