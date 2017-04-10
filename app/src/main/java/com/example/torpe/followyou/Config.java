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
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private SharedPreferences prefs;
    String sendingDaysKey = "com.example.torpe.followyou.sending_days";
    String intervallumKey = "com.example.torpe.followyou.intervallum";


    public Config(Context context) {
        mContext = context;
        prefs = context.getSharedPreferences("com.example.torpe.followyou", Context.MODE_PRIVATE);
        try {
            Date date = df.parse(this.mContext.getResources().getString(R.string.maxTime));
            //maxTime = new Time(date.getHours(),date.getMinutes(),0);
            //Log.e("Config:", maxTime.toString());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        userId = mContext.getResources().getString(R.string.userId);
    }

    public void getNewConfig() {
        Runnable r = new ConfigThread(mContext);
        new Thread(r).start();
    }

    public void setNewConfig(String dataStr) {
        GetDataObject dataObject = (gson.fromJson(dataStr, GetDataObject.class));
        prefs.edit().putInt(intervallumKey, dataObject.intervallum).commit();
        prefs.edit().putString(sendingDaysKey, dataObject.sending_days).commit();
        Log.e("setNewConfig:", "");
    }

    public int getIntervallum() {
        return prefs.getInt(intervallumKey, 5);
    }

    public String sendingDays() {
        return prefs.getString(sendingDaysKey, "");
    }
}