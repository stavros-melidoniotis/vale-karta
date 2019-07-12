package com.example.stavros_melidoniotis.valekarta;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;

public class SmsReceiverService extends Service {
    private SmsBroadcastReceiver smsBroadcastReceiver;

    public SmsReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("------------------------------Service Started---------------------------------");
        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.setListener(new SmsListener() {
            @Override
            public void messageReceived(String message) {
                System.out.println("------------------------------"+message+"---------------------------------");
            }
        });

        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsBroadcastReceiver, filter);
        System.out.println("------------------------------Receiver Registered---------------------------------");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsBroadcastReceiver);
        smsBroadcastReceiver = null;
    }
}
