package com.example.torpe.followyou;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
    public static Timer timer = null;
    private static TimerTask timerTask;
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
        SendDataObject dataObject = puffer.getLatest();
        if(dataObject != null) {
            sendMessageToActivity(dataObject, "");
        }
        serverCommunication = new ServerCommunication(FollowYou.this);
        timerTask = new mainTask();
        if(serverCommunication.isConnected()) {
            config.getNewConfig();
        } else if (config.getIntervallum() != 0){
            Log.e("start","start");
            startService();
        }
    }

    public static void startService()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000*60*config.getIntervallum());
    }

    public static void restartService(){
        timer.cancel();
        sendMessageToActivity(null, "restart");
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            if(config.isConfigGetTime() && serverCommunication.isConnected()){
                config.getNewConfig();
            }
            if(!config.isSendingTime(dataCollector.getLastCollectTime())){
                return;
            }
            sendDataObject = dataCollector.collectData();
            Puffer puffer = new Puffer(ctx);
            puffer.addLatest(sendDataObject);
            if(serverCommunication.isConnected()) {
                List<SendDataObject> sendDataArray = new ArrayList<SendDataObject>(puffer.getAll());
                sendDataArray.add(sendDataObject);
                String result = serverCommunication.sendLocationData(sendDataArray);
                if (result.equals("1") && sendDataArray.size() > 1){
                    puffer.clearPuffer();
                }
                else if(!result.equals("1")){
                    Log.e("run: ", result);
                }
            }else {
                puffer.add(sendDataObject);
            }
            sendMessageToActivity(sendDataObject, "");
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    private static void sendMessageToActivity(SendDataObject sendDataObject, String type ) {
        Intent intent = new Intent("GPSLocationUpdates");
        intent.putExtra("Type", type);
        if(!type.equals("restart")) {
            intent.putExtra("Latitude", new Double(sendDataObject.Latitude).toString());
            intent.putExtra("Longitude", new Double(sendDataObject.Longitude).toString());
            intent.putExtra("Time", sendDataObject.Time);
            intent.putExtra("Intervallum", new Integer(config.getIntervallum()).toString());
            intent.putExtra("userId", config.userId);
            intent.putExtra("statusCode", new Integer(sendDataObject.statusCode).toString());
        }
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

}
