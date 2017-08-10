package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
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
        customer.Name = name.getText().toString();
        customer.Phone = phone.getText().toString();
        customer.Address = address.getText().toString();
        return customer;
    }

    public void clear() {
        name.setText("");
        phone.setText("");
        address.setText("");
    }
}
