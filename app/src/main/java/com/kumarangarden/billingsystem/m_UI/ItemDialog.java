package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Product;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class ItemDialog extends Dialog {
    private EditText name;
    private EditText quantity;
    private EditText price;
    private NumberPicker digit1, digit2, digit3;

    public ItemDialog(@NonNull Context context) {
        super(context);
    }

    public void InitControls() {
        name = (EditText) findViewById(R.id.editName);
        quantity = (EditText) findViewById(R.id.editQuantity);
        price = (EditText) findViewById(R.id.editPrice);

        digit1 = (NumberPicker) findViewById(R.id.digitOne);
        digit1.setMinValue(0);
        digit1.setMaxValue(9);
        digit1.setOnValueChangedListener(digitChangeListener);

        digit2 = (NumberPicker) findViewById(R.id.dightTwo);
        digit2.setMinValue(0);
        digit2.setMaxValue(9);
        digit2.setOnValueChangedListener(digitChangeListener);

        digit3 = (NumberPicker) findViewById(R.id.digitThree);
        digit3.setMinValue(0);
        digit3.setMaxValue(9);
        digit3.setOnValueChangedListener(digitChangeListener);
    }

    NumberPicker.OnValueChangeListener digitChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            String id = digit1.getValue() + "" + digit2.getValue() + "" + digit3.getValue();
            //Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
        }
    };

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
        item.ID = digit1.getValue() + "" + digit2.getValue() + "" + digit3.getValue();
        return item;
    }

    public void clear() {
        digit1.setValue(0);
        digit2.setValue(0);
        digit3.setValue(0);
        name.setText("");
        quantity.setText("");
        price.setText("");
    }
}
