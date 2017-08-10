package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Product;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class ItemDialog extends Dialog {
    public EditText name;
    public EditText quantity;
    public EditText price;

    public ItemDialog(@NonNull Context context) {
        super(context);
    }

    public void InitControls() {
        name = (EditText) findViewById(R.id.editName);
        quantity = (EditText) findViewById(R.id.editQuantity);
        price = (EditText) findViewById(R.id.editPrice);
    }

    public String getIsValid() {
        String result = "";

        if(name.getText().toString().matches(""))
            result = "Enter Name";
        else if(quantity.getText().toString().matches(""))
            result = "Enter Quantity";
        else if(price.getText().toString().matches(""))
            result = "Enter Price";

        return result;
    }

    public Item getItem() {
        Item item = new Item();
        item.Name = name.getText().toString();
        item.Quantity = Float.parseFloat(quantity.getText().toString());
        item.UnitPrice = Float.parseFloat(price.getText().toString());
        item.ID = "000";
        return item;
    }

    public void clear() {
        name.setText("");
        quantity.setText("");
        price.setText("");
    }
}
