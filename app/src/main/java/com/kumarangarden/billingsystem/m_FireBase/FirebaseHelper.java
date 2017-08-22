package com.kumarangarden.billingsystem.m_FireBase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.kumarangarden.billingsystem.m_Model.Credit;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Leave;
import com.kumarangarden.billingsystem.m_Model.Product;

import java.util.ArrayList;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved = null;

    public FirebaseHelper(DatabaseReference db)
    {
        this.db = db;
    }

    public Boolean save(Product product)
    {
        if(product == null)
            saved = false;
        else
        {
            try
            {
                //db.child("Products").push().setValue(product);
                db.child("Products").child(product.GetId()).setValue(product);

                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }

    public Boolean save(Item item)
    {
        if(item == null)
            saved = false;
        else
        {
            try
            {
                //db.child("Items").push().setValue(item);
                db.child("Items").child(item.GetID()).setValue(item);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }

    public Boolean save(Customer customer)
    {
        if(customer == null)
            saved = false;
        else
        {
            try
            {
                //db.child("Customers").push().setValue(customer);
                db.child("Customers").child(customer.GetName()).setValue(customer);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }

    public Boolean save(Employee employee)
    {
        if(employee == null)
            saved = false;
        else
        {
            try
            {
                //db.child("Customers").push().setValue(customer);
                db.child("Employees").child(employee.GetName()).setValue(employee);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }

    public Boolean save(String name, Leave leave)
    {
        if(leave == null)
            saved = false;
        else
        {
            try
            {
                String key = "Leaves/" + name + "/" + leave.GetKey();
                db.child(key).setValue(leave);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }

    public Boolean save(String name, Credit credit)
    {
        if(credit == null)
            saved = false;
        else
        {
            try
            {
                String key = "Credits/" + name + "/" + credit.GetKey();
                db.child(key).setValue(credit);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }
}
