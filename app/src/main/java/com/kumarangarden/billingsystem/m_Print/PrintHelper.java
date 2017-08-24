package com.kumarangarden.billingsystem.m_Print;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.R;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Log;
import com.kumarangarden.billingsystem.m_Model.Employee;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_UI.ShowMsg;
import com.kumarangarden.billingsystem.m_Utility.DateTimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by kanna_000 on 19-08-2017.
 */

public class PrintHelper implements ReceiveListener {
    private final Handler handler;
    private Printer mPrinter = null;
    private Context context;

    public PrintHelper(Context context) {
        this.context = context;
        handler = new Handler(context.getMainLooper());
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_P20, Printer.MODEL_SOUTHASIA, context);
        } catch (Exception e) {
            ShowMsg.showException(e, "Printer", context);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private boolean createPayslipData(Employee employee) {
        String method = "addText";
        if (mPrinter == null) {
            return false;
        }

        try {

            mPrinter.addTextSize(1, 2);
            addText(employee.GetName() + "\n", Printer.ALIGN_LEFT);
            mPrinter.addTextSize(1, 1);
            addText( employee.GetSalaryLine() + "\n", Printer.ALIGN_RIGHT);
            addText( employee.GetDueLine() + "\n", Printer.ALIGN_RIGHT);
            addText( employee.GetRemainingLine() + "\n", Printer.ALIGN_RIGHT);
            addText("----------------------------------------\n", Printer.ALIGN_CENTER);
            mPrinter.addFeedLine(2);

        } catch (Exception e) {
            ShowMsg.showException(e, method, context);
            return false;
        }

        return true;
    }
    private boolean createReceiptData(DataSnapshot dataSnapshot, String name, String date, String time) {
        String method = "";
        Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_billing_18dp);
        StringBuilder textData = new StringBuilder();

        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            /*method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);


            method = "addImage";
            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);


            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            */
            mPrinter.addTextSize(1, 2);
            addText("குமரன் கார்டன்ஸ்", Printer.ALIGN_LEFT);
            mPrinter.addTextSize(1, 1);
            addText("    - 9629680504\n", Printer.ALIGN_RIGHT);

            String formattedDate = DateTimeUtil.GetFormatChanged("yyyyMMdd", "dd/MM/yyyy", date);
            addText(name + "\n", Printer.ALIGN_LEFT);
            addText(formattedDate + " - " + time + "\n", Printer.ALIGN_RIGHT);

            addText("----------------------------------------\n", Printer.ALIGN_CENTER);

            int i = 1;
            float sum = 0;
            for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                Item item = suggestionSnapshot.getValue(Item.class);
                try {
                    float price = item.GetNetPrice();
                    addText( i + ". " + item.Name +"\n", Printer.ALIGN_LEFT);
                    addText( "₹: " + item.UnitPrice + " x " + item.Quantity + " = ₹: " + price +"\n", Printer.ALIGN_RIGHT);
                    i++;
                    sum += price;
                } catch (Epos2Exception e) {
                    ShowMsg.showException(e, "Add Items\n", context);
                }
            }
            try {
                addText("----------------------------------------\n", Printer.ALIGN_CENTER);
                mPrinter.addTextSize(1, 2);
                addText("மொத்தம்  ₹:" + sum + "\n", Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(1, 1);
                mPrinter.addFeedLine(2);
            } catch (Epos2Exception e) {
                ShowMsg.showException(e, "Add Total\n", context);
            }

/*

            //textData.append("7/01/07 16:58 6153 05 0191 134\n");
            //textData.append("ST# 21 OP# 001 TE# 01 TR# 747\n");
            //textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
            /*textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
            textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
            textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
            textData.append("474 TRIPOD              8.99 R\n");
            textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
            textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
            textData.append("493 30L BLK FF DRESS   16.99 R\n");
            textData.append("407 LEVITATING DESKTOP  7.99 R\n");
            textData.append("441 **Blue Overprint P  2.99 R\n");
            textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
            textData.append("461 WESTGATE BLACK 25  59.99 R\n");
            textData.append("------------------------------\n");*/
    /*        method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("SUBTOTAL                160.38\n");
            textData.append("TAX                      14.43\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("TOTAL    174.81\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            textData.append("CASH                    200.00\n");
            textData.append("CHANGE                   25.19\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("Purchased item total number\n");
            textData.append("Sign Up and Save !\n");
            textData.append("With Preferred Saving Card\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
/*
            method = "addBarcode";
            mPrinter.addBarcode("01209457",
                    Printer.BARCODE_CODE39,
                    Printer.HRI_BELOW,
                    Printer.FONT_A,
                    barcodeWidth,
                    barcodeHeight);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);*/
        } catch (Exception e) {
            ShowMsg.showException(e, method, context);
            return false;
        }

        textData = null;

        return true;
    }

    private void addText(String string, int allignment) throws Epos2Exception {
        mPrinter.addTextAlign(allignment);
        mPrinter.addText(string);
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), context);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", context);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect("BT:00:01:90:C6:9D:45", Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", context);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", context);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += "Roll paper is nearly end.\\n";
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += "Battery level of printer is low.\\n";
        }

        if(warningsMsg != "")
            Toast.makeText(context, warningsMsg, Toast.LENGTH_LONG).show();
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", context);
                }
            });
        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {

                    ShowMsg.showException(e, "disconnect", context);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }
    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += context.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += context.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += context.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_autocutter);
            msg += context.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += context.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += context.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }
    public boolean runPrintReceiptSequence(DataSnapshot dataSnapshot, String name, String date, String time) {
        if (!initializeObject()) {
            return false;
        }

        if (!createReceiptData(dataSnapshot, name, date, time)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        return true;
    }

    public boolean runPrintPlayslipSequence(Employee employee) {

        if (!initializeObject()) {
            return false;
        }

        if (!createPayslipData(employee)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }


    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), context);

                dispPrinterWarnings(status);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }
}