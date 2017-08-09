package com.kumarangarden.billingsystem;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Product;
import com.kumarangarden.billingsystem.m_UI.ProductAdapter;
import com.kumarangarden.billingsystem.m_UI.ProductDialog;
import com.kumarangarden.billingsystem.m_UI.ProductViewHolder;

public class BillingActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FloatingActionButton fab;
    ProductDialog newProduct;

    DatabaseReference db;
    FirebaseHelper helper;
    ProductAdapter productAdapter;
    RecyclerView productsView;

    FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });

        productsView = (RecyclerView) findViewById(R.id.productsView);
        productsView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(Product.class,
                R.layout.productcard, ProductViewHolder.class, db.child("Products").getRef()) {
            @Override
            protected void populateViewHolder(ProductViewHolder holder, final Product product, final int position) {
                holder.name.setText(product.Name);
                holder.id.setText(product.ID);
                holder.price.setText("₹: " + product.Price);

                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Remove swiped item from list and notify the RecyclerView
                        DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                        dbRef.removeValue();
                    }
                };

/*
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(BillingActivity.this, "Deleted " + product.Name, Toast.LENGTH_LONG).show();

                        DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                        dbRef.removeValue();
                    }
                });*/
            }
        };




        productsView.setAdapter(firebaseRecyclerAdapter);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(BillingActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                //recycleAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                //return true;
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                ProductViewHolder holder = (ProductViewHolder) viewHolder;

                Toast.makeText(BillingActivity.this, "Removing:" + holder.name.getText().toString(), Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(productsView);

        newProduct = new ProductDialog(this);
        newProduct.setTitle("Product");
        newProduct.setContentView(R.layout.productform);
        newProduct.InitControls();
    }

    private void ShowDialog()
    {
        newProduct.show();
    }

    void OnAddCancel(View view)
    {
        newProduct.cancel();
    }

    void OnAddSave(View view)
    {
        String toastMessage = newProduct.getIsValid();

        if(toastMessage.matches(""))
        {
            //Add new product to view..
            if(helper.save(newProduct.getProduct()))
            {
                //productAdapter = new ProductAdapter(BillingActivity.this, helper.retrieve());
                //productsView.setAdapter(productAdapter);
            }
            else
                toastMessage ="Failed Saving";
            //newProduct.cancel();
            newProduct.clear();
            toastMessage ="New Product Added";
        }

        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }
}
