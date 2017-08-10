package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;

/**
 * Created by kanna_000 on 10-08-2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView name, price, quantity;

    public ItemViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.textName);
        price = (TextView) itemView.findViewById(R.id.textPrice);
        quantity = (TextView) itemView.findViewById(R.id.textQuantity);
    }
}
