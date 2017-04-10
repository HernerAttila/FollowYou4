package com.example.torpe.followyou;

import android.Manifest;
import android.content.Context;
import android.widget.TextView;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Torpe on 2017. 04. 09..
 */

public class DataCollector {
    SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    protected SendDataObject dataObject;
    protected TrackGPS gps;

    public DataCollector(Context context){
        gps = new TrackGPS(context);

    }

    public SendDataObject collectData(){
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        gps.getLocation();
        dataObject = new SendDataObject(gps.getLatitude(),gps.getLongitude(),formattedDate,MainActivity.config.getUserId(),0);
        return  dataObject;
    }
}
