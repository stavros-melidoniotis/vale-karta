package com.example.stavros_melidoniotis.valekarta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final int ALL_PERMISSIONS = 0;
    private Intent foregroundService = new Intent()
            .setClassName("com.example.stavros_melidoniotis.valekarta", "com.example.stavros_melidoniotis.valekarta.ForegroundService");
    private Intent calendarService = new Intent()
            .setClassName("com.example.stavros_melidoniotis.valekarta", "com.example.stavros_melidoniotis.valekarta.CalendarService");
    private String[] permissions = {
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.RECEIVE_SMS
    };

    //Check if we have required permissions
    public boolean permissionsGranted() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    //Request runtime permissions
    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(foregroundService);
            } else {
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if permissions are granted
        if (permissionsGranted()) {
            startForegroundService(foregroundService);

            // when button is pressed, start calendar service manually and check for an sms message
            findViewById(R.id.buttonAddEvent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startService(calendarService);
                }
            });
        } else {
            requestAllPermissions();
        }
    }
}
