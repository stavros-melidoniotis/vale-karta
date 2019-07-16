package com.example.stavros_melidoniotis.valekarta;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ChargerConnectedBroadcastReceiver chargerConnectedBroadcastReceiver;
    private static final int SMS_PERMISSION_CODE = 0;

    /**
     * Check if we have SMS permission
     */
    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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

        // check if read SMS permission is granted
        if (isSmsPermissionGranted()) {
            chargerConnectedBroadcastReceiver = new ChargerConnectedBroadcastReceiver();

            // filter for when charger is connected or disconnected
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

            registerReceiver(chargerConnectedBroadcastReceiver, filter);

            Toast.makeText(this, "Receiver registered", Toast.LENGTH_SHORT).show();
        } else {
            requestReadSmsPermission();
        }
    }
}
