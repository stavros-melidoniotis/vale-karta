package com.example.stavros_melidoniotis.valekarta;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ChargerConnectedBroadcastReceiver chargerConnectedBroadcastReceiver;
    private static final int ALL_PERMISSIONS = 0;

    String[] permissions = {
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALENDAR
    };

    /**
     * Check if we have required permissions
     */
    public boolean permissionsGranted() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**
     * Request runtime permissions
     */
    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chargerConnectedBroadcastReceiver = new ChargerConnectedBroadcastReceiver();

                    // filter for when charger is connected or disconnected
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_POWER_CONNECTED);
                    filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

                    registerReceiver(chargerConnectedBroadcastReceiver, filter);

                    Toast.makeText(this, "Receiver registered", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if permissions are granted
        if (permissionsGranted()) {
            chargerConnectedBroadcastReceiver = new ChargerConnectedBroadcastReceiver();

            // filter for when charger is connected or disconnected
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            //filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

            registerReceiver(chargerConnectedBroadcastReceiver, filter);

            Toast.makeText(this, "Receiver registered", Toast.LENGTH_SHORT).show();
        } else {
            requestAllPermissions();
        }
    }
}
