package com.example.stavros_melidoniotis.valekarta;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;

public class SmsReceiverService extends Service {
    private static SmsBroadcastReceiver smsBroadcastReceiver;

    public SmsReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        registerSmsReceiver();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsBroadcastReceiver);
        smsBroadcastReceiver = null;
    }

    private void registerSmsReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();

        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsBroadcastReceiver, filter);
    }
}
