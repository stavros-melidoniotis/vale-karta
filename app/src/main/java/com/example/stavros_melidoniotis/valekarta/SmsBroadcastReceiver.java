package com.example.stavros_melidoniotis.valekarta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    private static final String messageText = "ΣΤΟ WHAT'S UP ΤΙΠΟΤΑ ΔΕ ΠΑΕΙ ΧΑΜΕΝΟ!";
    private SmsListener listener;

    public SmsBroadcastReceiver(){
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsBody = "";

            System.out.println("----------------------SMS Received - Reading message------------------");
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsBody += smsMessage.getMessageBody();
            }

            System.out.println("Broadcast receiver:\n----------------------"+smsBody+"------------------");

            if (smsBody.startsWith(messageText)) {
                if (listener != null) {
                    listener.messageReceived(smsBody);
                }
            }
        }
    }

    public void setListener(SmsListener listener) {
        this.listener = listener;
    }
}
