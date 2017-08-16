package com.kumarangarden.billingsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_UI.CustomerViewHolder;
import com.kumarangarden.billingsystem.m_UI.EmployeeDialog;
import com.kumarangarden.billingsystem.m_UI.EmployeeViewHolder;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class PayslipFragment extends Fragment {

    EmployeeDialog newEmployee;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView employeesView;
    FirebaseRecyclerAdapter<Employee, EmployeeViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_payslip, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Employee, EmployeeViewHolder>(Employee.class,
                R.layout.employeecard, EmployeeViewHolder.class, db.child("Employees").getRef()) {
            @Override
            protected void populateViewHolder(EmployeeViewHolder holder, final Employee employee, final int position) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);
                employee.SetName(databaseReference.getKey());
                holder.Initialize(employee);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Touched  " + employee.GetName(), Toast.LENGTH_LONG).show();
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
