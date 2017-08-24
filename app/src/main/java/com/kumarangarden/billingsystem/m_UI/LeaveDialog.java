package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Credit;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Leave;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by 11000257 on 8/22/2017.
 */

public class LeaveDialog extends Dialog {

    public NumberPicker date;
    public EditText days;
    public EditText reason;
    public TextView labelOperation;

    public Button save, cancel;

    public LeaveDialog(@NonNull Context context) {
        super(context);
    }
    public void InitControls() {
        date = (NumberPicker) findViewById(R.id.numberDate);
        date.setMinValue(1);
        date.setMaxValue(31);
        days = (EditText) findViewById(R.id.editDays);
        reason = (EditText) findViewById(R.id.editReason);
        save = (Button) findViewById(R.id.buttonSave);
        cancel = (Button) findViewById(R.id.buttonCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        labelOperation = (TextView) findViewById(R.id.labeldays);

        Button cancel = (Button) findViewById(R.id.buttonCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public  void setDateLimit(int max)
    {
        date.setMaxValue(max);
    }

    public String getIsValid() {
        String result = "";

       if(days.getText().toString().matches(""))
            result = "Enter Days";
        else if(reason.getText().toString().matches(""))
            result = "Enter Reason";

        return result;
    }

    public Leave getLeave() {
        Leave leave = new Leave();
        //leave.SetName(name.getText().toString());
        leave.days = Float.parseFloat(days.getText().toString());
        leave.reason = reason.getText().toString();
        return leave;
    }

    public void setLeave(Leave leave) throws ParseException {
        String key = leave.GetKey();
        String date = key.substring(key.length() - 2);
        this.date.setValue(Integer.parseInt(date));
        days.setText(leave.days + "");
        reason.setText(leave.reason);
    }


    public Credit getCredit() {
        Credit credit = new Credit();
        //leave.SetName(name.getText().toString());
        credit.amount = Float.parseFloat(days.getText().toString());
        credit.reason = reason.getText().toString();
        return credit;
    }

    public void setCredit(Credit credit) throws ParseException {
        String key = credit.GetKey();
        String date = key.substring(key.length() - 2);
        this.date.setValue(Integer.parseInt(date));
        days.setText(credit.amount + "");
        reason.setText(credit.reason);
    }
    public void promptLeave()
    {
        setTitle("Leave");
        labelOperation.setText("நாள்:");
        show();
    }

    public void promptCredit()
    {
        setTitle("Credit");
        labelOperation.setText("தொகை:");
        show();
    }

    public void clear() {
        date.setValue(1);
        days.setText("1");
        reason.setText("");
    }
}
