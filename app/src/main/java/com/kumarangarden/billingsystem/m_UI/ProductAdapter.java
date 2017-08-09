package com.kumarangarden.billingsystem.m_UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Product;

import java.util.ArrayList;
import android.view.View;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder>
{
    Context context;
    ArrayList<Product> products;


    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.productcard, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.Name);
        holder.id.setText(product.ID);
        holder.price.setText("₹: " + product.Price);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
