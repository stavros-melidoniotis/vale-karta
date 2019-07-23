package com.example.stavros_melidoniotis.valekarta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.widget.Toast;

public class ChargerConnectedBroadcastReceiver extends BroadcastReceiver {
    public ChargerConnectedBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent calendarService = new Intent();
        calendarService.setClassName("com.example.stavros_melidoniotis.valekarta", "com.example.stavros_melidoniotis.valekarta.CalendarService");

        switch (intent.getAction()){
//            case Intent.ACTION_POWER_CONNECTED:
//                context.startService(calendarService);
//                break;
            case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                System.out.println("-------sms received from receiver-----");
                Toast.makeText(context, "Just received a message", Toast.LENGTH_SHORT).show();
                //context.startService(calendarService);
                break;
//            case Intent.ACTION_POWER_DISCONNECTED:
//                context.stopService(calendarService);
//                break;
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                context.startService(calendarService);
                break;
        }
    }
}