package com.kumarangarden.billingsystem.m_Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Employee {
    private String Name;
    public String Phone;
    public String Address;
    public float Wage;

    private float leaves, credits;
    private int workDays;
    private boolean seleted;

    private float leavesSum = 0;
    private float creditsSum = 0;

    public boolean IsSeleted() {
        return seleted;
    }

    public void SetSeleted(boolean seleted) {
        this.seleted = seleted;
    }

    public String GetName() {
        return Name;
    }

    public void SetName(String name) {
        Name = name;
    }


    public void SetWorkDays(int workDays) {
        this.workDays = workDays;
    }

    public void SetLeaves(float leaves) {
        this.leaves = leaves;
    }

    public void SetCredits(float credits) {
        this.credits = credits;
    }

    // for pay slip
    public String GetSalaryLine() {
        String message = "(" + workDays + ") - சம்பளம் ₹: " + workDays * Wage;
        return message;
    }

    public String GetDueLine() {
        String message = "வி - " + leaves + " + க - ₹:" + credits + " = ₹:" + ((Wage * leaves) + credits);
        return message;
    }

    public String GetRemainingLine() {
        String message = "மீதம் ₹:" + ((workDays * Wage) - ((Wage * leaves) + credits));
        return message;
    }

    public void CalcPay(final String stDate, final String edDate, final EmployeePayslipCalcuation delegate) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query leavesQuery = db.child("Leaves/" + Name)
                .orderByKey()
                .startAt(stDate)
                .endAt(edDate);

        leavesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.

                leavesSum = 0;
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    Leave item = suggestionSnapshot.getValue(Leave.class);
                    leavesSum += item.days;
                }
                SetLeaves(leavesSum);

                Query creditsQuery = db.child("Credits/" + Name)
                        .orderByKey()
                        .startAt(stDate)
                        .endAt(edDate);

                creditsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        creditsSum = 0;
                        for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                            Credit item = suggestionSnapshot.getValue(Credit.class);
                            creditsSum += item.amount;
                        }
                        SetCredits(creditsSum);

                        if (delegate != null)
                            delegate.OnPayslipCalculation();

/*
                        float due = (leavesSum * Wage) + creditsSum;
                        String sDue = "விடுப்பு: " + leavesSum;
                        sDue += " நாள், கடன்: ₹" + creditsSum;


                        detail.setText(sDue);
                        subText.setText("பிடிப்பு: ₹" + due);
                        salary.setText(" மீதம்: ₹" + ((employee.Wage * workDays) - due));*/
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

    public float GetLeavesSum() {
        return leavesSum;
    }

    public float GetCreditsSum() {
        return creditsSum;
    }

    public int GetWorkDays() {
        return workDays;
    }
}