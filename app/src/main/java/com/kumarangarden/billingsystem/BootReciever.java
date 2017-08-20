package com.kumarangarden.billingsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kumarangarden.billingsystem.m_Print.BillingService;

/**
 * Created by kanna_000 on 12-08-2017.
 */

public class BootReciever extends BroadcastReceiver {
    Intent service;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //Intent myIntent = new Intent(context, MainActivity.class);
        //myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(myIntent);

        service = new Intent(context, BillingService.class);
        context.startService(service);
    }
}
