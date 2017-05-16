package com.example.torpe.followyou;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Torpe on 2017. 04. 09..
 */

public class DataCollector {
    SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    protected SendDataObject dataObject;
    protected TrackGPS gps;
    protected Context context;

    public DataCollector(Context context) {
        gps = new TrackGPS(context);
        this.context = context;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FollowYou.config.dataCollectorTimeFile, Context.MODE_APPEND));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    public SendDataObject collectData() {
        Integer statusCode = 0;
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        gps.getLocation();
        if (!gps.isGPSEnabled) {
            statusCode = 1;
        }
        if (!gps.isNetworkEnabled){
            statusCode += 2;
        }
        dataObject = new SendDataObject(gps.getLatitude(), gps.getLongitude(), formattedDate, FollowYou.config.userId, statusCode);
        writeToFile(formattedDate);
        return dataObject;
    }

    public String getLastCollectTime(){
        return  readFromFile();
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FollowYou.config.dataCollectorTimeFile, Context.MODE_PRIVATE));
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
            InputStream inputStream = context.openFileInput(FollowYou.config.dataCollectorTimeFile);
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
