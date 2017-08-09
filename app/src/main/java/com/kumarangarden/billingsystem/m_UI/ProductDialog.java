package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.widget.EditText;

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
        product.ID = id.getText().toString();
        product.Price = Float.parseFloat(price.getText().toString());
        return product;
    }

    public void clear() {
        name.setText("");
        id.setText("");
        price.setText("");
    }
}
