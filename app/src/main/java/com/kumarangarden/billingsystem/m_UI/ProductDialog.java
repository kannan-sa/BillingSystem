package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Product;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class ProductDialog extends Dialog {
    public EditText name;
    public EditText id;
    public EditText price;


    public ProductDialog(@NonNull Context context) {
        super(context);
    }

    public void InitControls() {
        name = (EditText) findViewById(R.id.editName);
        id = (EditText) findViewById(R.id.editID);
        price = (EditText) findViewById(R.id.editPrice);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query queryRef = db.child("Products").orderByKey();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long nxid = dataSnapshot.getChildrenCount() + 1;
                id.setText(String.format("%03d", nxid));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String getIsValid() {
        String result = "";

        if(name.getText().toString().matches(""))
            result = "Enter Name";
        else if(id.getText().toString().matches(""))
            result = "Enter ID";
        else if(price.getText().toString().matches(""))
            result = "Enter Price";

        return result;
    }

    public Product getProduct() {
        Product product = new Product();
        product.Name = name.getText().toString();
        product.SetId(id.getText().toString());
        product.Price = Float.parseFloat(price.getText().toString());
        return product;
    }

    public void clear() {
        name.setText("");
        id.setText("");
        price.setText("");
    }
}
