package com.kumarangarden.billingsystem.m_Print;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.MainActivity;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_Model.Item;

/**
 * Created by kanna_000 on 18-08-2017.
 */

public class BillingService extends Service {

    DatabaseReference db;
    public static boolean initialized;
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service Created", Toast.LENGTH_LONG ).show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            if(!initialized) {
                initialized = true;
                firebaseDatabase.setPersistenceEnabled(true);
            }

        db = firebaseDatabase.getReference();
        db.keepSynced(true);
        db.child("Commands/Print").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.getValue(Integer.class) > 0) {
                        //MainActivity.SetDateTime(db);
                        PrintPurchases();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void PrintPurchases() {
        db.child("Commands").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String db_name = dataSnapshot.child("Name").getValue(String.class);

                final String name = db_name.matches("") ? "பொது" : db_name;
                final String date = dataSnapshot.child("Date").getValue(String.class);
                final String time = dataSnapshot.child("Time").getValue(String.class);

                db.child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() > 0)
                        {
                            PrintHelper printHelper = new PrintHelper(getApplicationContext());
                            if (!printHelper.runPrintReceiptSequence(dataSnapshot, name, date, time)) {
                                //db.child("Commands/Print").setValue(1); //2. else when no printer is avaliable just set command to other devices..
                                //Toast.makeText(MainActivity.this, "Command Set", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                MainActivity.ClearFlags();

                                String basekey = "Purchases/" + name + "/" +date +"/" + time;
                                //Copy to sales..
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    Item item = ds.getValue(Item.class);
                                    item.SetID(ds.getKey());
                                    String key = basekey + "/" + item.GetID();
                                    db.child(key).setValue(item);
                                }


                                //Clear
                                db.child("Items").removeValue();
                                db.child("Commands/Print").setValue(0);
                                db.child("Commands/Name").setValue("பொது");

                                final String customerKey = "Customers/" + name;
                                db.child(customerKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists())
                                            db.child(customerKey).setValue(new Customer(name));
                                        db.child(customerKey).setPriority(ServerValue.TIMESTAMP);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service Started", Toast.LENGTH_LONG ).show();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
