package com.example.torpe.followyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));

        mServiceIntent = new Intent(MainActivity.this, FollowYou.class);
        MainActivity.this.startService(mServiceIntent);
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
            lattitudeTextView.setText(intent.getStringExtra("Latitude"));
            longitudeTextView.setText(intent.getStringExtra("Longitude"));
            intervallumTextView.setText(intent.getStringExtra("Intervallum"));
            timerTextView.setText(intent.getStringExtra("Time"));
            userIdTextView.setText(intent.getStringExtra("userId"));
        }
    };

}
