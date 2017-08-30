package com.kumarangarden.billingsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Product;
import com.kumarangarden.billingsystem.m_UI.ItemDialog;
import com.kumarangarden.billingsystem.m_UI.ProductDialog;
import com.kumarangarden.billingsystem.m_UI.ProductViewHolder;

import static com.kumarangarden.billingsystem.OperationMode.Select;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class ProductsFragment extends Fragment {

    ProductDialog newProduct;
    ItemDialog newItem;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView productsView;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter;
    EditText editFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_products, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        newItem = new ItemDialog(getContext());
        newItem.setTitle("Item");
        newItem.setContentView(R.layout.itemform);
        newItem.InitControls(helper, true);
        newItem.idRow.setVisibility(View.GONE);
        editFilter = (EditText) view.findViewById(R.id.editFilter);
        editFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                UpdateProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        productsView = (RecyclerView) view.findViewById(R.id.productsView);
        productsView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        UpdateProducts();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(view.getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ProductViewHolder holder = (ProductViewHolder) viewHolder;
                Toast.makeText(view.getContext(), holder.getName() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(productsView);

        newProduct = new ProductDialog(getContext());
        newProduct.setTitle("Product");
        newProduct.setContentView(R.layout.productform);
        newProduct.InitControls(helper);

        FloatingActionButton addProduct = (FloatingActionButton) view.findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newProduct.show();
            }
        });

        return view;
    }

    public void UpdateProducts() {
        String filter = editFilter.getText().toString();
        Query query = db.child("Products");
        if(!filter.matches(""))
            query = db.child("Products").orderByChild("Name")
                    .startAt(filter)
                    .endAt(filter+"\uf8ff");


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(Product.class,
                R.layout.productcard, ProductViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ProductViewHolder holder, final Product product, final int position) {
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                product.SetId(dbRef.getKey());
                holder.Initialize(product);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newItem.setItem(new Item(product));
                        newItem.show();
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        newProduct.setProduct(product);
                        newProduct.show();
                        return false;
                    }
                });
            }
        };

        productsView.getRecycledViewPool().clear();
        productsView.setAdapter(firebaseRecyclerAdapter);
    }
}
