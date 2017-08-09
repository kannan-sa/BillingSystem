package com.kumarangarden.billingsystem;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_UI.ProductAdapter;
import com.kumarangarden.billingsystem.m_UI.ProductDialog;

public class BillingActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FloatingActionButton fab;
    ProductDialog newProduct;

    DatabaseReference db;
    FirebaseHelper helper;
    ProductAdapter productAdapter;
    RecyclerView productsView;

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

        db = FirebaseDatabase.getInstance().getReference();

        helper = new FirebaseHelper(db);

        productAdapter = new ProductAdapter(this, helper.retrieve());
        productsView.setAdapter(productAdapter);



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
                productAdapter = new ProductAdapter(BillingActivity.this, helper.retrieve());
                productsView.setAdapter(productAdapter);
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
