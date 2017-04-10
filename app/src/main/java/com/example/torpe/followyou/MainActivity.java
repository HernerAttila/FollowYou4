package com.example.torpe.followyou;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    public static Config config;
    private String userId;
    protected ServerCommunication serverCommunication;
    protected TrackGPS gps;
    protected DataCollector dataCollector;
    protected Puffer puffer;
    protected SendDataObject sendDataObject;
    private final static int INTERVAL = 1000 * 30 * 1; //0,5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.config = new Config(MainActivity.this);
        config.setNewConfig();
        this.dataCollector = new DataCollector(MainActivity.this);
        this.puffer = new Puffer(MainActivity.this);
        this.userId = config.getUserId();
        this.serverCommunication = new ServerCommunication(MainActivity.this);
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                followYouService();
            }
        }, 0, INTERVAL);

        Button btnGetData = (Button) findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateUI();
            }
        });
    }

    public void followYouService(){
        sendDataObject = this.dataCollector.collectData();
        if(serverCommunication.isConnected()) {
            List<SendDataObject> sendDataArray = new ArrayList<SendDataObject>();
            sendDataArray.add(sendDataObject);
            serverCommunication.sendLocationData(sendDataArray);
        }else{
            this.puffer.add(sendDataObject);
        }
    }

    void updateUI(){
        TextView lattitudeTextView = (TextView) findViewById(R.id.lattitudeTextView);
        TextView longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        TextView timerTextView = (TextView) findViewById(R.id.timerTextView);
        lattitudeTextView.setText(new Double(sendDataObject.Latitude).toString());
        longitudeTextView.setText(new Double(sendDataObject.Longitude).toString());
        timerTextView.setText(sendDataObject.Time);
    }

}
