package com.example.torpe.followyou;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hernera on 2017. 04. 11..
 */

public class FollowYou extends Service {
    public static Config config;
    protected ServerCommunication serverCommunication;
    protected DataCollector dataCollector;
    protected Puffer puffer;
    public SendDataObject sendDataObject;

    private static Timer timer = new Timer();
    private static Context ctx;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        config = new Config(FollowYou.this);
        dataCollector = new DataCollector(FollowYou.this);
        puffer = new Puffer(FollowYou.this);
        serverCommunication = new ServerCommunication(FollowYou.this);
        if(serverCommunication.isConnected()) {
            config.getNewConfig();
        }
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000*60*config.getIntervallum());
    }

    public static void stopService(){
        timer.cancel();
        Log.e("stopService:","stopped");
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            sendDataObject = dataCollector.collectData();
            Puffer puffer = new Puffer(ctx);
            if(serverCommunication.isConnected()) {
                List<SendDataObject> sendDataArray = new ArrayList<SendDataObject>(puffer.getAll());
                sendDataArray.add(sendDataObject);
                String result = serverCommunication.sendLocationData(sendDataArray);
                if (result.equals("1") && sendDataArray.size() > 1){
                    puffer.clearPuffer();
                }
            }else{
                puffer.add(sendDataObject);
            }
            sendMessageToActivity(sendDataObject);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    private static void sendMessageToActivity(SendDataObject sendDataObject) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("Latitude", new Double(sendDataObject.Latitude).toString());
        intent.putExtra("Longitude", new Double(sendDataObject.Longitude).toString());
        intent.putExtra("Time", sendDataObject.Time);
        intent.putExtra("Intervallum", new Integer(config.getIntervallum()).toString());
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

}
