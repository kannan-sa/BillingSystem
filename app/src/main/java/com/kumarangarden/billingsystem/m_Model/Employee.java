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


    public String GetName() {
        return Name;
    }

    public void SetName(String name) {
        Name = name;
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
}
