package com.kumarangarden.billingsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_UI.ItemDialog;
import com.kumarangarden.billingsystem.m_UI.ItemViewHolder;
import com.kumarangarden.billingsystem.m_UI.ProductViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class PurchaseFragment extends Fragment {

    ItemDialog newItem;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView itemsView;
    FirebaseRecyclerAdapter<Item, ItemViewHolder> firebaseRecyclerAdapter;
    TextView dateView, timeView;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_purchase, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class,
                R.layout.itemcard, ItemViewHolder.class, db.child("Items").getRef()) {
            @Override
            protected void populateViewHolder(ItemViewHolder holder, final Item item, final int position) {
                holder.Initialize(item);
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
                Toast.makeText(view.getContext(), holder.getName() + " Removed", Toast.LENGTH_SHORT).show();

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

        //SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss");
        //String currentDateandTime = sdf.format(new Date());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(c.getTime());

        dateView = (TextView) view.findViewById(R.id.textDate);
        dateView.setText(currentDateandTime);

        sdf = new SimpleDateFormat("HH:mm a");
        currentDateandTime = sdf.format(c.getTime());

        timeView = (TextView) view.findViewById(R.id.textTime);
        timeView.setText(currentDateandTime);


        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(view.getContext(), R.layout.customerlist);
        db.child("Customers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                autoComplete.clear();
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    //Get the suggestion by childing the key of the string you want to get.
                    String suggestion = suggestionSnapshot.child("Name").getValue(String.class);
                    //Add the retrieved string to the list
                    autoComplete.add(suggestion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AutoCompleteTextView editCustomer = (AutoCompleteTextView)view.findViewById(R.id.editCustomer);
        editCustomer.setThreshold(1);
        editCustomer.setAdapter(autoComplete);

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

    public void SetDate(View view) {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String currentDateandTime = sdf.format(c.getTime());
                dateView.setText(currentDateandTime);
            }
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        datePickerDialog.show();
    }

    public void SetTime(View view) {
        final Calendar c = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), hourOfDay, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String currentDateandTime = sdf.format(c.getTime());
                timeView.setText(currentDateandTime);
            }
        }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }
}
