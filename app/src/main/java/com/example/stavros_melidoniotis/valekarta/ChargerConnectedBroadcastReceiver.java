package com.example.stavros_melidoniotis.valekarta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChargerConnectedBroadcastReceiver extends BroadcastReceiver {
    public ChargerConnectedBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent smsService = new Intent();
        smsService.setClassName("com.example.stavros_melidoniotis.valekarta", "com.example.stavros_melidoniotis.valekarta.SmsReceiverService");

        switch (intent.getAction()){
            case Intent.ACTION_POWER_CONNECTED:
                context.startService(smsService);
                System.out.println("Power connected --- Starting service");
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                context.stopService(smsService);
                System.out.println("Power disconnected --- Stoping service");
                break;
//            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
//                context.startService(smsService);
//                System.out.println("Power connected --- Starting service");
//                break;
        }
    }
}