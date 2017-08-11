package com.kumarangarden.billingsystem;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class BillingActivity extends AppCompatActivity {

    PurchaseFragment purchaseFragment;
    ProductsFragment productsFragment;
    CustomersFragment customersFragment;

    static boolean initialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        if(!initialized) {
            initialized = true;
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        purchaseFragment = new PurchaseFragment();
        productsFragment = new ProductsFragment();
        customersFragment = new CustomersFragment();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Fragment fragment = null;
                switch (tabId)
                {
                    case R.id.navigation_purchase:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, purchaseFragment).commit();
                        break;
                    case R.id.navigation_products:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, productsFragment).commit();
                        break;
                    case R.id.navigation_customers:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, customersFragment).commit();
                        break;
                }
            }
        });
    }

    public void AddProduct(View view)
    {
        productsFragment.AddProduct(view);
    }
    public void CancelProduct(View view)  {
        productsFragment.CancelProduct(view);
    }
    public void SaveProduct(View view) {
        productsFragment.SaveProduct(view);
    }

    public void AddItem(View view)
    {
        purchaseFragment.AddItem(view);
    }
    public void SaveItem(View view) {
        purchaseFragment.SaveItem(view);
    }
    public void CancelItem(View view)  {
        purchaseFragment.CancelItem(view);
    }

    public void AddCustomer(View view)
    {
        customersFragment.AddCustomer(view);
    }
    public void SaveCustomer(View view) {
        customersFragment.SaveCustomer(view);
    }
    public void CancelCustomer(View view)  {
        customersFragment.CancelCustomer(view);
    }

    public void SetDate(View view)
    {
        purchaseFragment.SetDate(view);
    }
    public void SetTime(View view)
    {
        purchaseFragment.SetTime(view);
    }
}
