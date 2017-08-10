package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by kanna_000 on 10-08-2017.
 */

public class Item {
    public String Name;
    public String ID;
    public  float Quantity;
    public  float UnitPrice;

    public float getPrice() {
        return Quantity * UnitPrice;
    }
}
