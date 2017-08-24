package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Item;

/**
 * Created by 11000257 on 8/10/2017.
 */

public class CustomerDialog extends Dialog {
    public EditText name;
    public EditText phone;
    public EditText address;

    public CustomerDialog(@NonNull Context context) {
        super(context);
    }

    public void InitControls() {
        name = (EditText) findViewById(R.id.editName);
        phone = (EditText) findViewById(R.id.editPhone);
        address = (EditText) findViewById(R.id.editAddress);
        Button cancel = (Button) findViewById(R.id.cmdCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public String getIsValid() {
        String result = "";

        if(name.getText().toString().matches(""))
            result = "Enter Name";
        else if(phone.getText().toString().matches(""))
            result = "Enter Phone";
        else if(address.getText().toString().matches(""))
            result = "Enter Address";

        return result;
    }

    public Customer getCustomer() {
        Customer customer = new Customer();
        customer.SetName(name.getText().toString());
        customer.Phone = phone.getText().toString();
        customer.Address = address.getText().toString();
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        name.setText(customer.GetName());
        phone.setText(customer.Phone);
        address.setText(customer.Address);
    }

    public void clear() {
        name.setText("");
        phone.setText("");
        address.setText("");
    }
}
