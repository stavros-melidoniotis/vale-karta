package com.example.stavros_melidoniotis.valekarta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChargerConnectedBroadcastReceiver extends BroadcastReceiver {
    public ChargerConnectedBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent calendarService = new Intent();
        calendarService.setClassName("com.example.stavros_melidoniotis.valekarta", "com.example.stavros_melidoniotis.valekarta.CalendarService");

        switch (intent.getAction()){
            case Intent.ACTION_POWER_CONNECTED:
                context.startService(calendarService);
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                context.stopService(calendarService);
                break;
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                context.startService(calendarService);
                break;
        }
    }
}