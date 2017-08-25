package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class Product {
    public String Name;
    //public String ID;
    public  float Price;

    private String id;

    public Product() {

    }
    public Product(Item item) {
        Name = item.Name;
        id = item.GetID();
        Price = item.UnitPrice;
    }

    public String GetId() {
        return id;
    }

    public void SetId(String id) {
        this.id = id;
    }
}
