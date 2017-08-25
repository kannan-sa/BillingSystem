package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Model.Product;

/**
 * Created by kanna_000 on 08-08-2017.
 */

public class ItemDialog extends Dialog {
    private EditText name;
    private EditText quantity;
    private EditText price;
    private NumberPicker digit1, digit2, digit3;
    private DatabaseReference db;
    private Button incQnt, decQnt, incPrc, decPrc;

    private long DELAY = 100;
    Handler handler;

    public void scheduleUpdate(final EditText control, final float update)
    {
        handler.postDelayed(new Runnable() {
            public void run() {
                Update(control, update);
                handler.postDelayed(this, DELAY);
            }
        }, DELAY);
    }

    public ItemDialog(@NonNull Context context) {
        super(context);
    }

    public void InitControls(final FirebaseHelper helper, final boolean closeOnSave) {
        name = (EditText) findViewById(R.id.editName);
        quantity = (EditText) findViewById(R.id.editQuantity);
        price = (EditText) findViewById(R.id.editPrice);

        digit1 = (NumberPicker) findViewById(R.id.digitOne);
        digit1.setMinValue(0);
        digit1.setMaxValue(9);

        digit2 = (NumberPicker) findViewById(R.id.dightTwo);
        digit2.setMinValue(0);
        digit2.setMaxValue(9);

        digit3 = (NumberPicker) findViewById(R.id.digitThree);
        digit3.setMinValue(0);
        digit3.setMaxValue(9);


        db = FirebaseDatabase.getInstance().getReference();

        handler = new Handler();

        incQnt = (Button) findViewById(R.id.incQnt);
        decQnt = (Button) findViewById(R.id.decQnt);
        incPrc = (Button) findViewById(R.id.incPrc);
        decPrc = (Button) findViewById(R.id.decPrc);

        incQnt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scheduleUpdate(quantity, 1);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        decQnt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scheduleUpdate(quantity, -1);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        incPrc.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scheduleUpdate(price, 1);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        decPrc.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scheduleUpdate(price, -1);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        //--

        InitNewDigits();

        //--

        digit1.setOnValueChangedListener(digitChangeListener);
        digit2.setOnValueChangedListener(digitChangeListener);
        digit3.setOnValueChangedListener(digitChangeListener);

        final Button cancel = (Button) findViewById(R.id.cmdCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        Button saveCmd = (Button) findViewById(R.id.cmdSave);
        saveCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastMessage = getIsValid();

                if(toastMessage.matches(""))    //no error
                {
                    final Item item = getItem();
                    if(!helper.save(item))
                        toastMessage ="Failed Saving";
                    else {
                        clear();
                        toastMessage = item.Name + " சேர்க்கப்பட்டது";

                        final String productKey = "Products/" + item.GetID();
                        db.child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists())
                                    helper.save(new Product(item));
                                clear();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if(closeOnSave)
                            cancel();
                    }
                }

                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void InitNewDigits() {
    }

    NumberPicker.OnValueChangeListener digitChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            String id = digit1.getValue() + "" + digit2.getValue() + "" + digit3.getValue();
            Query queryRef = db.child("Products/" + id);
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        Initialize(product);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    };

    private void Initialize(Product product) {
        name.setText(product.Name);
        price.setText(product.Price + "");
    }

    public String getIsValid() {
        String result = "";
        String id = digit1.getValue() + "" + digit2.getValue() + "" + digit3.getValue();

        if(name.getText().toString().matches(""))
            result = "பெயரைக் கொடுங்கள்";
        else if(id.matches("") || id.matches("000"))
            result = "எண்ணை கொடுங்கள்";
        else if(quantity.getText().toString().matches(""))
            result = "அளவு கொடுக்கவும்";
        else if(price.getText().toString().matches(""))
            result = "விலை  கொடுங்கள்";

        return result;
    }

    public Item getItem() {
        Item item = new Item();
        item.Name = name.getText().toString();
        item.Quantity = Float.parseFloat(quantity.getText().toString());
        item.UnitPrice = Float.parseFloat(price.getText().toString());
        item.SetID(digit1.getValue() + "" + digit2.getValue() + "" + digit3.getValue());
        return item;
    }

    public void setItem(Item item)
    {
        name.setText(item.Name);
        quantity.setText(item.Quantity + "");
        price.setText(item.UnitPrice + "");
        String id = item.GetID();

        String digit = id.substring(0, Math.min(id.length(), 1));
        digit1.setValue(Integer.parseInt(digit));

        digit = id.substring(1, Math.min(id.length(), 2));
        digit2.setValue(Integer.parseInt(digit));

        digit = id.substring(2, Math.min(id.length(), 3));
        digit3.setValue(Integer.parseInt(digit));
    }

    public void Update(EditText editText, float update)
    {

        float value = 0;
        if(!editText.getText().toString().matches(""))
            value = Float.parseFloat(editText.getText().toString());
        float result = value + update;
        if(result > 0)
            editText.setText(result+"");
    }

    public void clear() {
        digit1.setValue(0);
        digit2.setValue(0);
        digit3.setValue(0);
        name.setText("");
        quantity.setText("1.0");
        price.setText("1.0");
    }

    static int Digit(int a, int b) {
        int i, digit=1;
        for(i=b-1; i>0; i++)
            digit = digit*10;
        digit = (a/digit) % 10;
        return digit;
    }
}
