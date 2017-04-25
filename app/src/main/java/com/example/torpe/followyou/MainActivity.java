package com.example.torpe.followyou;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkPremmisions();
        }
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));

        mServiceIntent = new Intent(MainActivity.this, FollowYou.class);
        MainActivity.this.startService(mServiceIntent);
    }

    public void start() {

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            TextView lattitudeTextView = (TextView) findViewById(R.id.lattitudeTextView);
            TextView longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
            TextView timerTextView = (TextView) findViewById(R.id.timerTextView);
            TextView intervallumTextView = (TextView) findViewById(R.id.intervallumTextView);
            TextView userIdTextView = (TextView) findViewById(R.id.userIdTextView);
            TextView statusCodeTextView = (TextView) findViewById(R.id.statusCodeTextView);
            lattitudeTextView.setText(intent.getStringExtra("Latitude"));
            longitudeTextView.setText(intent.getStringExtra("Longitude"));
            intervallumTextView.setText(intent.getStringExtra("Intervallum"));
            timerTextView.setText(intent.getStringExtra("Time"));
            userIdTextView.setText(intent.getStringExtra("userId"));
            statusCodeTextView.setText(intent.getStringExtra("statusCode"));
        }
    };

    private void checkPremmisions() {
        // the only way we insert the dummy contact if if we are below M.
        // Else we continue on and prompt the user for permissions

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("INTERNET STÁTUSZ");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission)) return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
