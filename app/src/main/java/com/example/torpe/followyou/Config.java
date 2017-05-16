package com.example.torpe.followyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    public String dataCollectorTimeFile;
    public String configTimeFile;

    private Date date;
    private Date dateCompareStart;
    private Date dateCompareEnd;
    private SimpleDateFormat inputParser = new SimpleDateFormat("H:mm");
    private SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

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
        dataCollectorTimeFile = mContext.getResources().getString(R.string.dataCollectorTimeFile);
        configTimeFile = mContext.getResources().getString(R.string.configTimeFile);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(configTimeFile, Context.MODE_APPEND));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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
            Calendar c = Calendar.getInstance();
            String formattedDate = df.format(c.getTime());
            writeToFile(formattedDate);
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

    public boolean isSendingTime(String lastSendingTimeStr) {
        Date lastSendingTime = parseConfigTime(lastSendingTimeStr);
        Date nowDate = new Date();
        long timeDiff = Math.abs(lastSendingTime.getTime() - nowDate.getTime());
        if (timeDiff < 1000*60*getIntervallum()){
            return false;
        }

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
        return false;
    }

    public boolean isConfigGetTime (){
        String lastGetConfig = readFromFile();
        Date lastGetConfigTime = parseConfigTime(lastGetConfig);
        Date now = new Date();
        long timeDiff = Math.abs(lastGetConfigTime.getTime() - now.getTime());
        return timeDiff > 1000*60*60;
    }

    private Date parseConfigTime(String date) {
        try {
            return df.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
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

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(configTimeFile, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = mContext.openFileInput(configTimeFile);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}