package com.example.torpe.followyou;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Torpe on 2017. 04. 09..
 */

public class DataCollector {
    SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    protected SendDataObject dataObject;
    protected TrackGPS gps;

    public DataCollector(Context context) {
        gps = new TrackGPS(context);

    }

    public SendDataObject collectData() {
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        gps.getLocation();
        if (gps.isGPSEnabled) {
            dataObject = new SendDataObject(gps.getLatitude(), gps.getLongitude(), formattedDate, FollowYou.config.userId, 0);
        } else {
            dataObject = new SendDataObject(gps.getLatitude(), gps.getLongitude(), formattedDate, FollowYou.config.userId, -1);
        }
        return dataObject;
    }
}
