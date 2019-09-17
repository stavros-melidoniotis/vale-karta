package com.example.stavros_melidoniotis.valekarta;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class CalendarService extends Service {
    private static final String CHANNEL_ID = "valeKartaChannel";
    private static final int NOTIFICATION_ID = 21635;
    private NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        // set standard notification characteristics
        builder.setSmallIcon(R.drawable.calendar_notification_logo)
                .setContentTitle("Ειδοποίηση")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Sleep for 1 second before searching for the SMS.
        //This is used so that the system will have time to save the just received SMS in inbox folder.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] smsData = getSMSBody();
        String smsBody = smsData[1];
        int smsID = Integer.parseInt(smsData[0]);

        // if smsBody is null, then no text from What's Up was found
        if (smsBody != null) {
            String date = parseSMSBody(smsBody);
            String[] splittedDate = date.split("/");
            String day = splittedDate[0];
            String month = splittedDate[1];


            // if month value is e.g 06 change it to 6
            if (month.charAt(0) == '0')
                month = String.valueOf(month.charAt(1));


            // create a calendar event
            long eventID = createCalendarEvent(Integer.parseInt(month) - 1, Integer.parseInt(day) - 1);


            // if event was added successfully put a reminder, otherwise display appropriate notification
            if (eventID != -1) {
                if (createCalendarReminder(eventID)) {

                    // following three lines of code are used to open device's calendar when user taps on the notification
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                    Intent calendarIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
                    PendingIntent openCalendar = PendingIntent.getActivity(this, 1001, calendarIntent, 0);

                    // event added notification
                    builder.setContentText("Δημιουργήθηκε συμβάν στο ημερολόγιο για την ανανέωση του υπολοίπου σας.")
                            .setContentIntent(openCalendar)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Δημιουργήθηκε συμβάν στο ημερολόγιο για την ανανέωση του υπολοίπου σας."));
                }
            }
            deleteSMSFromInbox(smsID);
        } else {
            // message not found notification
            builder.setContentText("Δεν βρέθηκε μήνυμα για δημιουργία συμβάντος.")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Δεν βρέθηκε μήνυμα για δημιουργία συμβάντος."));
        }
        // build and display notification
        Notification notification = builder.build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String[] getSMSBody() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null);
        String text = null;
        int id = -1;

        if (cursor.moveToFirst()) {
            do {
                // get sender's name
                String sender = cursor.getString(cursor.getColumnIndex("address"));

                if (sender.equals("WhatsUP")) {
//                    for(int idx=0;idx<cursor.getColumnCount();idx++) {
//                        System.out.println(cursor.getColumnName(idx) + ":" + cursor.getString(idx));
//                    }

                    // if SMS is already read then don't keep it's text
                    // this is a temporary fix until deletion is implemented
                    if (cursor.getInt(cursor.getColumnIndex("read")) == 1)
                        break;


                    // get SMS's text
                    text = cursor.getString(cursor.getColumnIndex("body"));
                    id = cursor.getInt(cursor.getColumnIndex("_id"));
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return new String[]{String.valueOf(id), text};
    }

    /* Method used to create a calendar event one day before the date found inside message's body
     *
     * @param month
     * @param day
     * @return eventID
     */
    private long createCalendarEvent(int month, int day) {
        Uri eventUri;
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        int year = beginTime.get(Calendar.YEAR);

        beginTime.set(year, month, day, 12, 30);
        endTime.set(year, month, day, 17, 30);

        long startMillis = beginTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();

        ContentValues event = new ContentValues();

        event.put(CalendarContract.Events.DTSTART, startMillis);
        event.put(CalendarContract.Events.DTEND, endMillis);
        event.put(CalendarContract.Events.TITLE, "Ανανέωση υπολοίπου What's Up");
        event.put(CalendarContract.Events.CALENDAR_ID, 1);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        event.put(CalendarContract.Events.HAS_ALARM, 1);


        // add event to calendar only if permission was granted from the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            eventUri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
            return Long.parseLong(eventUri.getLastPathSegment());
        }
        return -1;
    }

    /* method used to add a reminder in calendar event
     *
     * @param eventID
     * @return true/false
     */
    private boolean createCalendarReminder(long eventID) {
        ContentValues reminder = new ContentValues();

        reminder.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminder.put(CalendarContract.Reminders.MINUTES, 0);


        // add reminder to calendar event only if permission was granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri reminderUri = getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminder);
            long reminderId = Long.parseLong(reminderUri.getLastPathSegment());

            return reminderId > 0;
        }
        return false;
    }

    /* method used to parse due date from message's body
     *
     * @param body
     * @return substring of original SMS text
     */
    private String parseSMSBody(String body){
        System.out.println(body);
        return body.substring(104, 109).trim();
    }

    /*
     *  method used to create a notification channel for post Oreo devices
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(CHANNEL_ID);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.MAGENTA);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // method used to delete SMS from inbox after event creation
    private void deleteSMSFromInbox(int id) {
        int deletedSMS = getContentResolver().delete(Uri.parse("content://sms/conversations"), "address=WhatsUP", null);

        if (deletedSMS > 0)
            System.out.println("-----SMS deleted from inbox-------");
    }
}
