package com.kumarangarden.billingsystem.m_FireBase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.kumarangarden.billingsystem.m_Model.Product;

import java.util.ArrayList;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved = null;
    ArrayList<Product> products = new ArrayList<>();

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
                db.child("Products").push().setValue(product);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = true;
            }
        }
        return  saved;
    }

    public void fetchData(DataSnapshot dataSnapshot)
    {
        products.clear();

        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            Product product = ds.getValue(Product.class);
            products.add(product);
        }
    }

    public ArrayList<Product> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return products;
    }

}
