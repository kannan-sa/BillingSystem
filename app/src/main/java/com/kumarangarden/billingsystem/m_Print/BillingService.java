package com.kumarangarden.billingsystem.m_Print;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_Model.Item;

/**
 * Created by kanna_000 on 18-08-2017.
 */

public class BillingService extends Service {

    DatabaseReference db;
    static boolean initialized;
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service Created", Toast.LENGTH_LONG ).show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            if(!initialized) {
                initialized = true;
                //firebaseDatabase.setPersistenceEnabled(true);
            }

        db = firebaseDatabase.getReference();
        db.keepSynced(true);
        db.child("Commands/Print").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.getValue(Integer.class) == 1) {
                       db.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               //Toast.makeText(BillingService.this, "Has :" + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();

                               //TODO .. Do billing .. send to printer..
                               PrintHelper printHelper = new PrintHelper(BillingService.this);
                               if(printHelper.runPrintReceiptSequence(dataSnapshot))
                                   db.child("Commands/Print").setValue(0);
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
