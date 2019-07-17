package com.example.stavros_melidoniotis.valekarta;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;

import java.util.Calendar;


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

        String smsBody = getSMSBody();

        // if smsBody is null, then no text from What's Up was found
        if (smsBody != null) {
            String date = parseSMSBody(smsBody);
            String[] splittedDate = date.split("/");

            String day = splittedDate[0];
            String month = splittedDate[1];

            // if month value is e.g 06 change it to 6
            if (month.charAt(0) == '0') {
                month = String.valueOf(month.charAt(1));
            }

            System.out.println("Month: "+month+"\nDay: "+day);

            if (createCalendarReminder(Integer.parseInt(month), Integer.parseInt(day)) != null) {
                System.out.println("Reminder added successfully");
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // this method is used to retrieve the SMS message sent from WhatsUP, located in phone's SMS inbox
    private String getSMSBody(){
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null);
        String text = null;

        if (cursor.moveToFirst()) {
            do {
                // get sender's name
                String sender = cursor.getString(cursor.getColumnIndex("address"));

                if (sender.equals("WhatsUP")) {
                    // get SMS's text
                    text = cursor.getString(cursor.getColumnIndex("body"));
                } else {
                    continue;
                }
            } while (cursor.moveToNext());
        }
        return text;
    }

    // method used to create a calendar reminder one day before the date found inside message's body
    private Uri createCalendarReminder(int month, int day){
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        int year = beginTime.get(Calendar.YEAR);

        // months range from 0 to 11
        beginTime.set(year, month - 1, day - 1 , 12, 30);
        long startMillis = beginTime.getTimeInMillis();
        endTime.set(year, month - 1, day - 1, 17, 30);
        long endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Ανανέωση υπολοίπου What's Up");
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Athens");

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        return uri;
    }

    // method used to parse due date from message's body
    private String parseSMSBody(String body){
        return body.substring(104, 109).trim();
    }

}
