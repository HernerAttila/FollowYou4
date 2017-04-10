package com.example.torpe.followyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    public static Config config;
    protected ServerCommunication serverCommunication;
    protected TrackGPS gps;
    protected DataCollector dataCollector;
    protected Puffer puffer;
    protected SendDataObject sendDataObject;
    private int INTERVAL; //0,5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = new Config(MainActivity.this);
        serverCommunication = new ServerCommunication(MainActivity.this);
        if(serverCommunication.isConnected()) {
            config.getNewConfig();
        }
        dataCollector = new DataCollector(MainActivity.this);
        puffer = new Puffer(MainActivity.this);

        startService();
        Button btnGetData = (Button) findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateUI();
            }
        });
    }

    public void startService(){
        INTERVAL = 1000 * 30 * config.getIntervallum();
        Log.e("intervallum", new Integer(INTERVAL).toString());
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                followYouService();
            }
        }, 0, INTERVAL);
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
        TextView intervallumTextView = (TextView) findViewById(R.id.intervallumTextView);
        lattitudeTextView.setText(new Double(sendDataObject.Latitude).toString());
        longitudeTextView.setText(new Double(sendDataObject.Longitude).toString());
        intervallumTextView.setText(new Integer(config.getIntervallum()).toString());
        timerTextView.setText(sendDataObject.Time);
    }

}
