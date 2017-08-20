package com.kumarangarden.billingsystem;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_Print.PrintHelper;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;


public class MainActivity extends AppCompatActivity {

    PurchaseFragment purchaseFragment;
    ProductsFragment productsFragment;
    CustomersFragment customersFragment;
    PayslipFragment payslipFragment;
    BottomBar bottomBar;
    Dialog confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        confirm = new Dialog(this);
        confirm.setContentView(R.layout.dialog_confirm);
        confirm.setTitle("Confirm");

        purchaseFragment = new PurchaseFragment();
        productsFragment = new ProductsFragment();
        customersFragment = new CustomersFragment();
        payslipFragment = new PayslipFragment();

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
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
                    case R.id.navigation_employees:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, payslipFragment).commit();
                    break;
                }
            }
        });

        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int bottomBarState  =  saved_values.getInt("ONLY_PURCHASE", 0);
        bottomBar.setVisibility(bottomBarState == View.VISIBLE ? View.VISIBLE : View.INVISIBLE);
    }

    public void ConfirmPrint(View view) {
        confirm.show();
    }

    public void ClearItems(View view) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Items").removeValue();
        confirm.cancel();
    }

    public void PrintReceipt(View view) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // 1. if printer avaliable then send content to printer

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PrintHelper printHelper = new PrintHelper(MainActivity.this);
                if( !printHelper.runPrintReceiptSequence(dataSnapshot)) {
                    db.child("Commands/Print").setValue(1); //2. else when no printer is avaliable just set command to other devices..
                    Toast.makeText(MainActivity.this, "Command Set", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Printing Procedures..
        confirm.cancel();
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

    public void AddEmployee(View view)
    {
        payslipFragment.AddEmployee(view);
    }
    public void SaveEmployee(View view) {
        payslipFragment.SaveEmployee(view);
    }
    public void CancelEmployee(View view)  {
        payslipFragment.CancelEmployee(view);
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
