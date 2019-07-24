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

        // when a new SMS arrives, display Toast message and start calendar service
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Toast.makeText(context, "Just received a message", Toast.LENGTH_SHORT).show();
            context.startService(calendarService);
        }
    }
}