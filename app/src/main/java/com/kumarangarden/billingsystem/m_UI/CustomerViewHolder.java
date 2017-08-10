package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Item;

/**
 * Created by 11000257 on 8/10/2017.
 */

public class CustomerViewHolder extends RecyclerView.ViewHolder  {
    private TextView name, phone, address;

    public CustomerViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.textName);
        phone = (TextView) itemView.findViewById(R.id.textPhone);
        address = (TextView) itemView.findViewById(R.id.textAddress);
    }

    public String getName() {
        return name.getText().toString();
    }

    public void Initialize(Customer customer)
    {
        name.setText(customer.Name);
        phone.setText(customer.Phone);
        address.setText(customer.Address);
    }

}