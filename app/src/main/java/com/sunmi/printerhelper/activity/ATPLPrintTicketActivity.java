package com.sunmi.printerhelper.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sunmi.printerhelper.BaseApp;
import com.sunmi.printerhelper.R;
import com.sunmi.printerhelper.utils.AidlUtil;
import com.sunmi.printerhelper.utils.BluetoothUtil;
import com.sunmi.printerhelper.utils.ESCUtil;


import java.io.IOException;

/**
 * Created by Ishant on 2019/8/13.
 */

public class ATPLPrintTicketActivity extends AppCompatActivity {

    // Text Settings
    byte resource = 0x00;
    String charset = "utf-8";
    float fontSize = 24;

    // QR Code Settings
    int printSize = 6;
    int errorLevel = 3;

    BaseApp baseApp;

    // Ticket Data
    String ticketHeader = "Feroze Gandhi Market\nABC Contractor\nGST#03AAMQ1de54A";
    String ticketHashCodeForQRCode = "www.finlo.in/q=415321";
    String url = "www.finlo.in";
    String ticketReceiptNumber = "B1987472913";
    String ticketSeparator = "--------------------------------";
    String ticketDetails = "Vehicle# PB10AL2937\nIn Time:17:00\nOut Time: 19:00";
    String ticketTerms="Terms\nFine of Rs.100 If Ticket Lost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_ticket);

        AidlUtil.getInstance().initPrinter();

        baseApp = (BaseApp)getApplication();

        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printTicket();
            }
        });
    }

    // Implement printByAidl and printByBluetooth as per ticket

    public void printTicket(){
        // Check if Connected to AIDL Service and print directly
        if (baseApp.isAidl()) {
            printByAidl();
            Log.i("PrintTicketActivity", ">> Printed with AIDL");
        } else { // Else print from BT. In case BT is also non functional -> Restart the App
            printByBluetooth();
            Log.i("PrintTicketActivity", ">> Printed with BlueTooth");
        }
    }


    void printByAidl(){

        AidlUtil.getInstance().sendRawData(ESCUtil.alignCenter());

        AidlUtil.getInstance().printText(ticketHeader, fontSize, true, false);
        AidlUtil.getInstance().lineWrap(2);

        AidlUtil.getInstance().printQr(ticketHashCodeForQRCode, printSize, errorLevel);
        AidlUtil.getInstance().lineWrap(1);

        AidlUtil.getInstance().printText(url, fontSize, true, false);
        AidlUtil.getInstance().lineWrap(1);

        AidlUtil.getInstance().printText(ticketReceiptNumber, fontSize, false, false);
        AidlUtil.getInstance().lineWrap(1);

        AidlUtil.getInstance().printText(ticketSeparator, fontSize, false, false);
        AidlUtil.getInstance().lineWrap(1);

        AidlUtil.getInstance().sendRawData(ESCUtil.alignLeft());
        AidlUtil.getInstance().printText(ticketDetails, fontSize, false, false);
        AidlUtil.getInstance().lineWrap(1);

        AidlUtil.getInstance().sendRawData(ESCUtil.alignCenter());
        AidlUtil.getInstance().printText(ticketSeparator, fontSize, false, false);
        AidlUtil.getInstance().lineWrap(1);

        AidlUtil.getInstance().sendRawData(ESCUtil.alignCenter());
        AidlUtil.getInstance().printText(ticketTerms, fontSize, false, false);
        AidlUtil.getInstance().lineWrap(3);
    }

    private void printByBluetooth() {

        try {

            BluetoothUtil.sendData(ESCUtil.boldOn());
            BluetoothUtil.sendData(ESCUtil.alignCenter());

            BluetoothUtil.sendData(ESCUtil.singleByteOff());
            BluetoothUtil.sendData(ESCUtil.setCodeSystem(resource));

            BluetoothUtil.sendData(ticketHeader.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(2));

            BluetoothUtil.sendData(ESCUtil.getPrintQRCode(ticketHashCodeForQRCode, printSize, errorLevel));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));

            BluetoothUtil.sendData(ESCUtil.boldOff());

            BluetoothUtil.sendData(url.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));

            BluetoothUtil.sendData(ticketReceiptNumber.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));

            BluetoothUtil.sendData(ticketSeparator.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));

            BluetoothUtil.sendData(ESCUtil.alignLeft());

            BluetoothUtil.sendData(ticketDetails.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));

            BluetoothUtil.sendData(ticketSeparator.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));

            BluetoothUtil.sendData(ticketTerms.getBytes(charset));
            BluetoothUtil.sendData(ESCUtil.nextLine(3));

        } catch (IOException e) {
            Toast.makeText(this, ">> Re-Start the App !! Some Printing Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("PrintTicketActivity", ">> Exception while Printing: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
