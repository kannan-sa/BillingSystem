package com.kumarangarden.billingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Credit;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Leave;
import com.kumarangarden.billingsystem.m_UI.CreditViewHolder;
import com.kumarangarden.billingsystem.m_UI.LeaveDialog;
import com.kumarangarden.billingsystem.m_UI.LeaveViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
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
    RecyclerView leavesView, creditsView;
    FirebaseRecyclerAdapter<Leave, LeaveViewHolder> leavesAdapter;
    FirebaseRecyclerAdapter<Credit, CreditViewHolder> creditsAdapter;

    ImageButton addLeave, addCredit;
    SimpleDateFormat dateFormat;
    TextView labelCredits, labelLeaves;

    Employee employee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Intent intent = getIntent();

        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        final String empName = intent.getStringExtra("Name");
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

        labelLeaves = (TextView) findViewById(R.id.labelleave);
        labelCredits = (TextView) findViewById(R.id.labelcredit);

        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelper(db);

        newLeave = new LeaveDialog(this);
        newLeave.setContentView(R.layout.leaveform);
        newLeave.InitControls();

        newLeave.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toastMessage = newLeave.getIsValid();

                if(toastMessage.matches(""))    //no error
                {
                    String key = String.format("%04d%02d%02d", year.getValue(), month.getValue(), newLeave.date.getValue());
                    if(newLeave.labelOperation.getText().toString().matches("நாள்:")) {
                        Leave leave = newLeave.getLeave();
                        leave.SetKey(key);
                        if (!helper.save(employeeName.getText().toString(), leave))
                            toastMessage = "Failed Saving";
                    }
                    else
                    {
                        Credit credit = newLeave.getCredit();
                        credit.SetKey(key);
                        if (!helper.save(employeeName.getText().toString(), credit))
                            toastMessage = "Failed Saving";
                    }
                    newLeave.clear();
                    newLeave.cancel();
                }
                if(!toastMessage.matches(""))
                    Toast.makeText(EmployeeActivity.this, toastMessage, Toast.LENGTH_LONG).show();

            }
        });

        db.child("Employees/" + empName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employee = dataSnapshot.getValue(Employee.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        leavesView = (RecyclerView) findViewById(R.id.leavesView);
        leavesView.setLayoutManager(new LinearLayoutManager(this));

        creditsView = (RecyclerView) findViewById(R.id.duesView);
        creditsView.setLayoutManager(new LinearLayoutManager(this));

        UpdateLeavesAndCredits();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                LeaveViewHolder holder = (LeaveViewHolder) viewHolder;
                Toast.makeText(EmployeeActivity.this, holder.GetName() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = leavesAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(leavesView);

        ItemTouchHelper.SimpleCallback creditItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                CreditViewHolder holder = (CreditViewHolder) viewHolder;
                Toast.makeText(EmployeeActivity.this, holder.GetName() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = creditsAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper creditTouchHelper = new ItemTouchHelper(creditItemTouchCallback);
        creditTouchHelper.attachToRecyclerView(creditsView);





        addLeave = (ImageButton) findViewById(R.id.addleave);
        addLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLeave.date.setValue(calendar.get(Calendar.DATE));
                newLeave.promptLeave();
            }
        });
        addCredit = (ImageButton) findViewById(R.id.addcredit);
        addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLeave.date.setValue(calendar.get(Calendar.DATE));
                newLeave.promptCredit();
            }
        });

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                UpdateLeavesAndCredits();
            }
        });

        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                UpdateLeavesAndCredits();
            }
        });
    }

    private void UpdateLeavesAndCredits() {

        newLeave.setDateLimit(GetMonthDays(year.getValue(), month.getValue() - 1));

        String startDate = String.format("%04d%02d%02d", year.getValue(), month.getValue(), 1);
        String endDate = String.format("%04d%02d%02d", year.getValue(), month.getValue(), GetMonthDays(year.getValue(), month.getValue()-1));
        Query leavesQuery = db.child("Leaves/" + employeeName.getText())
                .orderByKey()
                .startAt(startDate)
                .endAt(endDate);

        leavesAdapter = new FirebaseRecyclerAdapter<Leave, LeaveViewHolder>(Leave.class,
                R.layout.leave_card, LeaveViewHolder.class, leavesQuery) {
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
                            newLeave.promptLeave();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
        };
        leavesView.getRecycledViewPool().clear();
        leavesView.setAdapter(leavesAdapter);

        leavesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float sum = 0;
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    Leave item = suggestionSnapshot.getValue(Leave.class);
                    sum += item.days;
                }
                String leavesLabel = "விடுப்பு: " + sum + " நாள் = ₹ " + (sum * employee.Wage) ;
                labelLeaves.setText(leavesLabel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query creditsQuery = db.child("Credits/" + employeeName.getText())
                .orderByKey()
                .startAt(startDate)
                .endAt(endDate);

        creditsAdapter = new FirebaseRecyclerAdapter<Credit, CreditViewHolder>(Credit.class,
                R.layout.leave_card, CreditViewHolder.class, creditsQuery) {
            @Override
            protected void populateViewHolder(CreditViewHolder holder, final Credit item, final int position) {
                DatabaseReference databaseReference = creditsAdapter.getRef(position);
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
                            newLeave.setCredit(item);
                            newLeave.promptCredit();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
        };
        creditsView.getRecycledViewPool().clear();
        creditsView.setAdapter(creditsAdapter);

        creditsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float sum = 0;
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    Credit item = suggestionSnapshot.getValue(Credit.class);
                    sum += item.amount;
                }
                String creditsLeave = "கடன்: ₹ "+ sum;
                labelCredits.setText(creditsLeave);
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
