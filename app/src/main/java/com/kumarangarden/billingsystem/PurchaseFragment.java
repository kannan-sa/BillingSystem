package com.kumarangarden.billingsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_UI.ItemDialog;
import com.kumarangarden.billingsystem.m_UI.ItemViewHolder;
import com.kumarangarden.billingsystem.m_UI.ProductViewHolder;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class PurchaseFragment extends Fragment {

    ItemDialog newItem;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView itemsView;
    FirebaseRecyclerAdapter<Item, ItemViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_purchase, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class,
                R.layout.itemcard, ItemViewHolder.class, db.child("Items").getRef()) {
            @Override
            protected void populateViewHolder(ItemViewHolder holder, final Item item, final int position) {
                holder.name.setText(item.Name);
                holder.quantity.setText(item.Quantity + " x ₹: " + item.UnitPrice);
                holder.price.setText("₹: " + item.getPrice());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Touched  " + item.Name, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        itemsView = (RecyclerView) view.findViewById(R.id.itemsView);
        itemsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        itemsView.setAdapter(firebaseRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(view.getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                Toast.makeText(view.getContext(), holder.name.getText().toString() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(itemsView);

        newItem = new ItemDialog(getContext());
        newItem.setTitle("Item");
        newItem.setContentView(R.layout.itemform);
        newItem.InitControls();

        return view;
    }

    void AddItem(View view)
    {
        newItem.show();
    }

    void CancelItem(View view)
    {
        newItem.cancel();
    }

    void SaveItem(View view)
    {
        String toastMessage = newItem.getIsValid();

        if(toastMessage.matches(""))    //no error
        {
            Item item = newItem.getItem();
            if(!helper.save(item))
                toastMessage ="Failed Saving";
            newItem.clear();
            toastMessage = item.Name + " Added";
        }

        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
    }
}
