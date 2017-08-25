package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
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

    public void InitControls(final FirebaseHelper helper) {
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

        Button cancel = (Button) findViewById(R.id.cmdCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        Button saveCmd = (Button) findViewById(R.id.cmdSave);
        saveCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastMessage = getIsValid();

                if(toastMessage.matches(""))    //no error
                {
                    Product product = getProduct();
                    if(!helper.save(product))
                        toastMessage ="Failed Saving";
                    else {
                        clear();
                        toastMessage = product.Name + " சேர்க்கப்பட்டது";
                    }
                }

                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getIsValid() {
        String result = "";

        if(name.getText().toString().matches(""))
            result = "பெயரைக் கொடுங்கள்";
        else if(id.getText().toString().matches("") || id.getText().toString().matches("000"))
            result = "எண்ணை கொடுங்கள்";
        else if(price.getText().toString().matches(""))
            result = "விலை  கொடுங்கள்";

        return result;
    }

    public Product getProduct() {
        Product product = new Product();
        product.Name = name.getText().toString();
        product.SetId(id.getText().toString());
        product.Price = Float.parseFloat(price.getText().toString());
        return product;
    }

    public void setProduct(Product product)
    {
        name.setText(product.Name);
        id.setText(product.GetId());
        price.setText(product.Price +"");
    }

    public void clear() {
        name.setText("");
        id.setText("");
        price.setText("");
    }
}
