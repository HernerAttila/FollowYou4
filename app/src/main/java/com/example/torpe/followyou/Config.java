package com.example.torpe.followyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;


/**
 * Created by Torpe on 2017. 04. 02..
 */

public class Config {
    private Context mContext;
    private Gson gson = new Gson();

    public String userId;
    public String pufferFile;
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private SharedPreferences prefs;
    String sendingDaysKey = "com.example.torpe.followyou.sending_days";
    String intervallumKey = "com.example.torpe.followyou.intervallum";


    public Config(Context context) {
        mContext = context;
        prefs = context.getSharedPreferences("com.example.torpe.followyou", Context.MODE_PRIVATE);
        userId = mContext.getResources().getString(R.string.userId);
        pufferFile = mContext.getResources().getString(R.string.pufferFile);
    }

    public void getNewConfig() {
        Runnable r = new ConfigThread(mContext);
        new Thread(r).start();
    }

    public void setNewConfig(String dataStr) {
        GetDataObject dataObject = (gson.fromJson(dataStr, GetDataObject.class));
        if(dataObject.is_user) {
            prefs.edit().putInt(intervallumKey, dataObject.intervallum).commit();
            prefs.edit().putString(sendingDaysKey, dataObject.sending_days).commit();
            //{"is_user":true,"is_sender":true,"sending_days":"1;2;3;4;5;","start_hour":7,"start_min":0,"end_hour":19,"end_min":0,"intervallum":3}
        }
        else{
            FollowYou.stopService();
        }
    }

    public int getIntervallum() {
        int intervallum = prefs.getInt(intervallumKey, 1);
        if (intervallum < 1) return 5;
        return intervallum;
    }

    public String sendingDays() {
        return prefs.getString(sendingDaysKey, "");
    }
}