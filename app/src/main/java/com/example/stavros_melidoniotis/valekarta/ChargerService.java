package com.example.stavros_melidoniotis.valekarta;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class ChargerService extends Service {
    private ChargerConnectedBroadcastReceiver chargerConnectedBroadcastReceiver;
    private IntentFilter filter;

    public ChargerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        System.out.println("---------Charger Service created----------");

        chargerConnectedBroadcastReceiver = new ChargerConnectedBroadcastReceiver();
        filter = new IntentFilter();

        // filter for when charger is connected or disconnected
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(chargerConnectedBroadcastReceiver, filter);
        System.out.println("-----------Receiver registered from ChargerService");
        Toast.makeText(this, "Receiver Registered", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(chargerConnectedBroadcastReceiver);
    }
}
