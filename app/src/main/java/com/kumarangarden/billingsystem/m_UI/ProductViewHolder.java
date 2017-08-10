package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Product;

import org.w3c.dom.Text;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private TextView name, price, id;

    public ProductViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.textName);
        price = (TextView) itemView.findViewById(R.id.textPrice);
        id = (TextView) itemView.findViewById(R.id.textID);
    }

    public String getName() {
        return name.getText().toString();
    }

    public  void Initialize(Product product)
    {
        name.setText(product.Name);
        id.setText(product.ID);
        price.setText("₹: " + product.Price);
    }
}
