package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by 11000257 on 8/16/2017.
 */

public class Employee {
    private String Name;
    public String Phone;
    public String Address;
    public float Wage;


    public String GetName() {
        return Name;
    }

    public void SetName(String name) {
        Name = name;
    }

    public float GetTotal() {
        return  1500;
    }

    public float GetRemaining() {
        return 180;
    }

    public float GetSalary() {
        return 1200;
    }
}
