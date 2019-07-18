package com.example.stavros_melidoniotis.valekarta;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;

import java.util.Calendar;
import java.util.TimeZone;


public class SmsReceiverService extends Service {

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

            long eventId = createCalendarEvent(Integer.parseInt(month), Integer.parseInt(day));

            if (eventId > 0) {
                System.out.println("Event added successfully");

//                if (createCalendarReminder(eventId)) {
//                    System.out.println("Reminder added successfully");
//                } else {
//                    System.out.println("Reminder not added successfully");
//                }
            } else {
                System.out.println("Event not added successfully");
            }
        } else {
            // ADD NOTIFICATION SHOWING NO MESSAGE WAS FOUND
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
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return text;
    }

    // method used to create a calendar reminder one day before the date found inside message's body
    private long createCalendarEvent(int month, int day) {
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        int year = beginTime.get(Calendar.YEAR);

        beginTime.set(year, month, day - 1, 12, 30);
        endTime.set(year, month, day - 1, 17, 30);

        long startMillis = beginTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();

        ContentValues event = new ContentValues();

        event.put(CalendarContract.Events.DTSTART, startMillis);
        event.put(CalendarContract.Events.DTEND, endMillis);
        event.put(CalendarContract.Events.TITLE, "Ανανέωση υπολοίπου What's Up");
        event.put(CalendarContract.Events.CALENDAR_ID, 1);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        event.put(CalendarContract.Events.HAS_ALARM, 1);

        Uri eventUri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);

        long eventId = Long.parseLong(eventUri.getLastPathSegment());

        return eventId;
    }

    // method used to add a reminder in calendar event
//    private boolean createCalendarReminder(long eventId){
//        ContentValues reminder = new ContentValues();
//
//        reminder.put(CalendarContract.Reminders.EVENT_ID, eventId);
//        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
//        reminder.put(CalendarContract.Reminders.MINUTES, 0);
//
//        Uri reminderUri = getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminder);
//
//        long reminderId = Long.parseLong(reminderUri.getLastPathSegment());
//
//        if (reminderId > 0)
//            return true;
//
//        return false;
//    }

    // method used to parse due date from message's body
    private String parseSMSBody(String body){
        return body.substring(104, 109).trim();
    }
}
