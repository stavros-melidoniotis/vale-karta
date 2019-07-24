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
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class ForegroundService extends Service {
    private SmsReceiver smsReceiver;
    private IntentFilter filter;
    private static final String CHANNEL_ID = "foregroundService";
    private static final int NOTIFICATION_ID = 21653;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        smsReceiver = new SmsReceiver();
        filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        // set standard notification characteristics
        builder.setSmallIcon(R.drawable.sms_notification_logo)
                .setContentTitle("Sms receiving service running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // start foreground service and display notification
        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(smsReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(CHANNEL_ID);
            channel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
