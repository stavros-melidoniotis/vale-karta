package com.example.stavros_melidoniotis.valekarta;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;

public class ChargerService extends Service {
    private ChargerConnectedBroadcastReceiver chargerConnectedBroadcastReceiver;
    private IntentFilter filter;
    private static final String CHANNEL_ID = "foregroundService";
    private static final int NOTIFICATION_ID = 21653;
    private Notification notification;
    private NotificationCompat.Builder builder;

    public ChargerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        chargerConnectedBroadcastReceiver = new ChargerConnectedBroadcastReceiver();
        filter = new IntentFilter();

        // filter for when charger is connected or disconnected
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        createNotificationChannel();
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        // set standard notification characteristics
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Charger service running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // create and display notification
        notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(chargerConnectedBroadcastReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(chargerConnectedBroadcastReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
