package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Credit;
import com.kumarangarden.billingsystem.m_Model.Leave;

import java.text.ParseException;

/**
 * Created by kanna_000 on 22-08-2017.
 */

public class CreditViewHolder extends RecyclerView.ViewHolder {
    private TextView date, leaveText;

    public CreditViewHolder(View itemView) {
        super(itemView);
        date = (TextView) itemView.findViewById(R.id.textDate);
        leaveText = (TextView) itemView.findViewById(R.id.textLeave);
    }
    public void Initialize(Credit credit) throws ParseException {
        String key = credit.GetKey();
        String date = key.substring(key.length() - 2);
        this.date.setText(date); //"தேதி: " +
        leaveText.setText( "₹ " + credit.amount + " - "  + credit.reason);
    }

    public String GetName() {
        return date.getText().toString()  + leaveText.getText();
    }
}
