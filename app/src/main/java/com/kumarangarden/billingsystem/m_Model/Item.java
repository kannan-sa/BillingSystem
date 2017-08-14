package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by kanna_000 on 10-08-2017.
 */

public class Item {
    public String Name;
    public  float Quantity;
    public  float UnitPrice;

    private String id;

    public String GetID() {
        return id;
    }

    public void SetID(String id) {
        this.id = id;
    }

    public float GetNetPrice() {
        return Quantity * UnitPrice;
    }
}
