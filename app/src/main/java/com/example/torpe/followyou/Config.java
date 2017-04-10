package com.example.torpe.followyou;

import android.content.Context;
import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Torpe on 2017. 04. 02..
 */

public class Config {
    private Context mContext;
    protected boolean LteEnabled;
    protected int pufferMaxSize;
    protected String userId;
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    protected Time maxTime;


    public Config(Context context){
        this.mContext = context;
        this.LteEnabled = this.mContext.getResources().getBoolean(R.bool.LteEnabled);
        try {
            Date date = df.parse(this.mContext.getResources().getString(R.string.maxTime));
            maxTime = new Time(date.getHours(),date.getMinutes(),0);
            Log.e("Config:", maxTime.toString());
        }
        catch(ParseException ex){
            ex.printStackTrace();
        }
        this.pufferMaxSize = this.mContext.getResources().getInteger(R.integer.pufferMaxSize);
        this.userId = this.mContext.getResources().getString(R.string.userId);
    }

    public void setNewConfig(){
        Runnable r = new ConfigThread(this.mContext);
        new Thread(r).start();
    }

    public boolean isLteEnabled() {
        return LteEnabled;
    }

    public int getPufferMaxSize() {
        return pufferMaxSize;
    }

    public String getUserId() {
        return userId;
    }
}