package com.kumarangarden.billingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_UI.EmployeeDialog;
import com.kumarangarden.billingsystem.m_UI.EmployeeViewHolder;
import com.kumarangarden.billingsystem.m_Utility.InputFilterMinMax;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class PayslipFragment extends Fragment {

    EmployeeDialog newEmployee;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView employeesView;
    FirebaseRecyclerAdapter<Employee, EmployeeViewHolder> firebaseRecyclerAdapter;
    EditText editSTDate, editWorkDays;
    TextView labelStatus;
    SharedPreferences sharedPreferences;

    int startDate =1;
    int workDays = 31;
    String stDate, edDate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_payslip, container, false);

        labelStatus = (TextView) view.findViewById(R.id.labelStatus);
        editSTDate = (EditText) view.findViewById(R.id.editSTDate);
        editWorkDays = (EditText) view.findViewById(R.id.editWorkDays);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext().getApplicationContext());
        startDate  =  sharedPreferences.getInt("PayDate", 1);

        findStartEndDate();

        editSTDate.setText(startDate + "");
        editSTDate.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "28")});
        editSTDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startDate = 1;
                try {
                    startDate = Integer.parseInt(editSTDate.getText().toString());
                } catch(NumberFormatException nfe) {
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("PayDate", startDate);
                editor.commit();

                findStartEndDate();
                editWorkDays.setText(workDays + "");
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editWorkDays.setText(workDays + "");
        editWorkDays.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "31")});
        editWorkDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    workDays = Integer.parseInt(editWorkDays.getText().toString());
                } catch(NumberFormatException nfe) {
                }

                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Employee, EmployeeViewHolder>(Employee.class,
                R.layout.employeecard, EmployeeViewHolder.class, db.child("Employees").getRef()) {
            @Override
            protected void populateViewHolder(EmployeeViewHolder holder, final Employee employee, final int position) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);
                employee.SetName(databaseReference.getKey());
                holder.Initialize(employee, stDate, edDate, workDays);
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        newEmployee.setEmployee(employee);
                        newEmployee.show();
                        return false;
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EmployeeActivity.class);
                        intent.putExtra("Name", employee.GetName());
                        startActivity(intent);
                    }
                });
            }
        };

        employeesView = (RecyclerView) view.findViewById(R.id.employeesView);
        employeesView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        employeesView.setAdapter(firebaseRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(view.getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                EmployeeViewHolder holder = (EmployeeViewHolder) viewHolder;
                Toast.makeText(view.getContext(), holder.getName() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(employeesView);

        newEmployee = new EmployeeDialog(getContext());
        newEmployee.setTitle("Employee");
        newEmployee.setContentView(R.layout.employeeform);
        newEmployee.InitControls();


        return view;
    }

    private void findStartEndDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DAY_OF_MONTH);

        if(month == 0)
        {
            month = 12; //to december
            year--; // one year back
        }

        stDate = String.format("%04d%02d%02d", year, month, this.startDate);

        if(startDate == 1) {
            edDate = String.format("%04d%02d%02d", year, month, workDays);
            workDays =  GetMonthDays(year, month -1);
            labelStatus.setText( startDate + "/" + month + "/" + year + " முதல் "
                    + workDays + "/" + month + "/" + year + " வரை" );
        }
        else
        {
            int eYear = year;
            int eMonth = month + 1;
            int eDay = startDate;

            if(date >= startDate)
            {
                if(eMonth == 13)
                {
                    eMonth = 1; //to january
                    eYear++; // one year up
                }
                edDate = String.format("%04d%02d%02d", eYear, eMonth, eDay-1);
            }
            else
            {
                month--;
                if(month == 0)
                {
                    month = 12; //to december
                    year--; // one year back
                }
                stDate = String.format("%04d%02d%02d", year, month, this.startDate);
                eYear = year;
                eMonth = month + 1;
                if(eMonth == 13)
                {
                    eMonth = 1; //to january
                    eYear++; // one year up
                }
                edDate = String.format("%04d%02d%02d", eYear, eMonth, eDay-1);
            }
            workDays =  GetDays(year, month-1, startDate, eYear, eMonth-1, eDay);
            labelStatus.setText( startDate + "/" + month + "/" + year + " முதல் "
                    + (eDay-1) + "/" + eMonth + "/" + eYear + " வரை" );
        }

    }

    private int GetMonthDays(int year, int month) {
        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(year, month, 1);
        // Get the number of days in that month
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
    }
    private int GetDays(int syear, int smonth, int sday, int eyears, int emonth, int eday)
    {
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(syear, smonth , sday);
        date2.clear();
        date2.set(eyears, emonth , eday);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return (int) dayCount;
    }

    void AddEmployee(View view)
    {
        newEmployee.show();
    }

    void CancelEmployee(View view)
    {
        newEmployee.cancel();
    }

    void SaveEmployee(View view)
    {
        String toastMessage = newEmployee.getIsValid();

        if(toastMessage.matches(""))    //no error
        {
            Employee employee = newEmployee.getEmployee();
            if(!helper.save(employee))
                toastMessage ="Failed Saving";
            newEmployee.clear();
            toastMessage = employee.GetName() + " Added";
        }

        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
    }

}
