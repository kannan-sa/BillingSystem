package com.kumarangarden.billingsystem;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Print.PrintHelper;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    PurchaseFragment purchaseFragment;
    ProductsFragment productsFragment;
    CustomersFragment customersFragment;
    PayslipFragment payslipFragment;
    BottomBar bottomBar;
    Dialog confirm;

    public static boolean dateSet = false, timeSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        confirm = new Dialog(this);
        confirm.setContentView(R.layout.dialog_confirm);
        confirm.setTitle("Confirm");

        Button printButton = (Button) confirm.findViewById(R.id.cmdSave);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintReceipt();
            }
        });

        Button clearButton = (Button) confirm.findViewById(R.id.cmdCancel);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("Items").removeValue();
                confirm.cancel();
            }
        });


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

    public void PrintReceipt() {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        SetDateTime(db);
        // 1. if printer avaliable then send content to printer
        db.child("Commands").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name = (String) dataSnapshot.child("Name").getValue();
                final String date = (String) dataSnapshot.child("Date").getValue();
                final String time = (String) dataSnapshot.child("Time").getValue();

                db.child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() > 0)
                        {
                            PrintHelper printHelper = new PrintHelper(MainActivity.this);
                            if (!printHelper.runPrintReceiptSequence(dataSnapshot, name, date, time)) {
                                db.child("Commands/Print").setValue(1); //2. else when no printer is avaliable just set command to other devices..
                                Toast.makeText(MainActivity.this, "Command Set", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                db.child("Commands/Print").setValue(0);
                                String basekey = "Purchases/" + name + "/" +date +"/" + time;
                                //Copy to sales..
                                for (DataSnapshot ds : dataSnapshot.child("Items").getChildren()){
                                    Item item = ds.getValue(Item.class);
                                    item.SetID(ds.getKey());
                                    String key = basekey + "/" + item.GetID();
                                    db.child(key).setValue(item);
                                }
                                db.child("Customers/" + name).setPriority(ServerValue.TIMESTAMP);
                                //Clear
                                db.child("Items").removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Printing Procedures..
        confirm.cancel();
    }

    public void SetDateTime(DatabaseReference db) {
        if (!dateSet) {
            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            db.child("Commands/Date").setValue(date);
        }
        if (!timeSet) {
            String time = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            db.child("Commands/Time").setValue(time);
        }
        dateSet = false;
        timeSet = false;
    }

    public void AddCustomer(View view)
    {
        customersFragment.AddCustomer(view);
    }
    public void SaveCustomer(View view) {
        customersFragment.SaveCustomer(view);
    }

    public void AddEmployee(View view)
    {
        payslipFragment.AddEmployee(view);
    }
    public void SaveEmployee(View view) {
        payslipFragment.SaveEmployee(view);
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
