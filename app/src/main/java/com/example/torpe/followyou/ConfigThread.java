package com.example.torpe.followyou;

import android.content.Context;
import android.util.Log;


/**
 * Created by Torpe on 2017. 04. 04..
 */

public class ConfigThread implements Runnable {
    protected Context context;
    public ConfigThread(Context context) {
        this.context = context;
    }

    public void run() {
        ServerCommunication serverCommunication = new ServerCommunication(this.context);
        String result = serverCommunication.getConfigData();
        Log.e("ConfigThread",result);
    }
}