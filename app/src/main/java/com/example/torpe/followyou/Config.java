package com.example.torpe.followyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;


/**
 * Created by Torpe on 2017. 04. 02..
 */

public class Config {
    private Context mContext;
    private Gson gson = new Gson();

    public String userId;
    public String pufferFile;

    private Date date;
    private Date dateCompareStart;
    private Date dateCompareEnd;
    private SimpleDateFormat inputParser = new SimpleDateFormat("H:mm");

    private SharedPreferences prefs;
    String sendingDaysKey = "com.example.torpe.followyou.sendingDays";
    String intervallumKey = "com.example.torpe.followyou.intervallum";
    String startTimeKey = "com.example.torpe.followyou.startTime";
    String endTimeKey = "com.example.torpe.followyou.endTime";

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
            prefs.edit().putString(startTimeKey, dataObject.start_hour+":"+dataObject.start_min).commit();
            prefs.edit().putString(endTimeKey, dataObject.end_hour+":"+dataObject.end_min).commit();
            FollowYou.startService();
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

    public String getSendingDays() {
        return prefs.getString(sendingDaysKey, "");
    }

    public String getStartTime() {
       return prefs.getString(startTimeKey, "0:00");
    }

    public String getEndTime() {
        return prefs.getString(endTimeKey, "23:59");
    }

    public boolean isSendingTime() {
        Calendar now = Calendar.getInstance(Locale.GERMANY);
        int day = now.get(Calendar.DAY_OF_WEEK);

        if(!isInSendingDays(day)){
            return false;
        }

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        date = parseDate(hour + ":" + minute);
        dateCompareStart = parseDate(getStartTime());
        dateCompareEnd = parseDate(getEndTime());

        if ((!dateCompareStart.after(date)) && (!dateCompareEnd.before(date))) {
            return true;
        }
        return true;
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private boolean isInSendingDays(int day){
        List<String> days = Arrays.asList(getSendingDays().split(";"));
        return days.contains(new Integer(day).toString());
    }
}