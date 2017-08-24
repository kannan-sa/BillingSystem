package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Sale;
import com.kumarangarden.billingsystem.m_Utility.DateTimeUtil;

import java.util.List;

/**
 * Created by kanna_000 on 23-08-2017.
 */

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleHolder> {
    private List<Sale> salesList;
    public String name;

    public class SaleHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public int index;

        public SaleHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textInfo);


            //genre = (TextView) view.findViewById(R.id.genre);
            //year = (TextView) view.findViewById(R.id.year);
        }
    }


    public SaleAdapter(List<Sale> salesList) {
        this.salesList = salesList;
    }

    @Override
    public SaleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_card, parent, false);

        return new SaleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SaleHolder holder, int position) {
        final Sale sale = salesList.get(position);
        String formattedDate = DateTimeUtil.GetFormatChanged("yyyyMMdd", "dd/MM/yyyy", sale.date);
        holder.title.setText(formattedDate + " - " + sale.time);
        ImageButton addItems = (ImageButton) holder.itemView.findViewById(R.id.addItems);
        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                String key = "Purchases/" + name + "/" + sale.date + "/" + sale.time;
                db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                        for(DataSnapshot dateRef : dataSnapshot.getChildren()) {
                            Item item = dateRef.getValue(Item.class);
                            item.SetID(dateRef.getKey());

                            db.child("Items/" + item.GetID()).setValue(item);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }
}
