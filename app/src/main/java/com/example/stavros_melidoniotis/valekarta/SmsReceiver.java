package com.example.stavros_melidoniotis.valekarta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent calendarService = new Intent();
        calendarService.setClassName("com.example.stavros_melidoniotis.valekarta", "com.example.stavros_melidoniotis.valekarta.CalendarService");

        switch (intent.getAction()){
            case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                Toast.makeText(context, "Just received a message", Toast.LENGTH_SHORT).show();
                context.startService(calendarService);
                break;
        }
    }
}