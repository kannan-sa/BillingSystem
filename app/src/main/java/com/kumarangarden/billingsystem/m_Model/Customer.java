package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by kanna_000 on 10-08-2017.
 */

public class Customer {

    private String Name;
    public String Phone;
    public String Address;

    public Customer() {

    }
    public Customer(String name) {
        Name = name;
        Phone = "000";
        Address = "NA";
    }

    public String GetName() {
        return Name;
    }

    public void SetName(String name) {
        Name = name;
    }
}
