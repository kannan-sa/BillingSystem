package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Credit;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_Model.Leave;

/**
 * Created by 11000257 on 8/10/2017.
 */

public class EmployeeViewHolder extends RecyclerView.ViewHolder  {
    private TextView title, subText, total, salary, detail;

    public EmployeeViewHolder(View itemView) {
        super(itemView);

        title  = (TextView) itemView.findViewById(R.id.textTitle);
        total  = (TextView) itemView.findViewById(R.id.textTotal);
        subText = (TextView) itemView.findViewById(R.id.textSubText);
        salary = (TextView) itemView.findViewById(R.id.textSalary);
        detail = (TextView) itemView.findViewById(R.id.textDetail);
    }

    public String getName() {
        return title.getText().toString();
    }

    private float leavesSum = 0;
    private float creditsSum = 0;

    public void Initialize(final Employee employee, final String stDate, final String edDate, final int workDays)
    {
        String title = employee.GetName() + "  ( ₹ " + employee.Wage + ")";
        total.setText(" ₹" + employee.Wage * workDays);
        this.title.setText(title);


        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query leavesQuery = db.child("Leaves/" + employee.GetName())
                .orderByKey()
                .startAt(stDate)
                .endAt(edDate);
        leavesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                leavesSum = 0;
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    Leave item = suggestionSnapshot.getValue(Leave.class);
                    leavesSum += item.days;
                }
                employee.SetLeaves(leavesSum);
                Query creditsQuery = db.child("Credits/" + employee.GetName())
                        .orderByKey()
                        .startAt(stDate)
                        .endAt(edDate);

                creditsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        creditsSum = 0;
                        for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                            Credit item = suggestionSnapshot.getValue(Credit.class);
                            creditsSum += item.amount;
                        }
                        employee.SetCredits(creditsSum);
                        float due = (leavesSum * employee.Wage)  + creditsSum;
                        String sDue = "விடுப்பு: " + leavesSum;
                        sDue += " நாள், கடன்: ₹" + creditsSum ;
                        detail.setText(sDue);
                        subText.setText ("பிடிப்பு: ₹" + due);
                        salary.setText(" மீதம்: ₹" + ((employee.Wage * workDays) - due));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





    }
}