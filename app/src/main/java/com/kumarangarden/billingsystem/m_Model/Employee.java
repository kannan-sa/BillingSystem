package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by 11000257 on 8/16/2017.
 */

public class Employee {
    private String Name;
    public String Phone;
    public String Address;
    public float Wage;

    private float leaves, credits;
    private int workDays;
    private boolean seleted;

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

    public float GetWorkDays() {
        return workDays;
    }

    public void SetWorkDays(int workDays) {
        this.workDays = workDays;
    }

    public float GetLeaves() {
        return leaves;
    }

    public void SetLeaves(float leaves) {
        this.leaves = leaves;
    }

    public float GetCredits() {
        return credits;
    }

    public void SetCredits(float credits) {
        this.credits = credits;
    }

    // for pay slip
    public String GetSalaryLine()
    {
        String message = "(" + workDays + ") - சம்பளம் ₹: " + workDays * Wage;
        return message;
    }

    public String GetDueLine()
    {
        String message = "வி - " + leaves + " + க - ₹:" + credits + " = ₹:" +((Wage * leaves) + credits);
        return message;
    }

    public String GetRemainingLine()
    {
        String message = "மீதம் ₹:" +  ((workDays * Wage) - ((Wage * leaves) + credits));
        return message;
    }
}
