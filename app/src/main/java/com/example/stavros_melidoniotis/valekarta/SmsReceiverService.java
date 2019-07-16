package com.example.stavros_melidoniotis.valekarta;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

public class SmsReceiverService extends Service {
    public SmsReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        System.out.println("------------------------------Service Started---------------------------------");
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null);

        String smsData = "";

        if (cursor.moveToFirst()) {
            do {
                // get sender's name
                String sender = cursor.getString(cursor.getColumnIndex("address"));

                if (sender.equals("WhatsUP")) {
                    // get SMS's text
                    String text = cursor.getString(cursor.getColumnIndex("body"));
                    System.out.println(text);
                } else {
                    continue;
                }
            } while (cursor.moveToNext());

            //System.out.println(smsData);
        }
    }

    @Override
    public void onDestroy() {

    }
}
