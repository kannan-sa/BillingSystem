package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_Model.Item;

/**
 * Created by 11000257 on 8/10/2017.
 */

public class EmployeeDialog extends Dialog {
    public EditText name;
    public EditText phone;
    public EditText address;
    public EditText wage;

    public EmployeeDialog(@NonNull Context context) {
        super(context);
    }

    public void InitControls() {
        name = (EditText) findViewById(R.id.editName);
        phone = (EditText) findViewById(R.id.editPhone);
        address = (EditText) findViewById(R.id.editAddress);
        wage = (EditText) findViewById(R.id.editWage);
    }

    public String getIsValid() {
        String result = "";

        if(name.getText().toString().matches(""))
            result = "Enter Name";
        else if(wage.getText().toString().matches(""))
            result = "Enter Wage";
        else if(phone.getText().toString().matches(""))
            result = "Enter Phone";
        else if(address.getText().toString().matches(""))
            result = "Enter Address";

        return result;
    }

    public Employee getEmployee() {
        Employee employee = new Employee();
        employee.SetName(name.getText().toString());
        employee.Phone = phone.getText().toString();
        employee.Address = address.getText().toString();
        return employee;
    }

    public void clear() {
        name.setText("");
        phone.setText("");
        address.setText("");
    }
}
