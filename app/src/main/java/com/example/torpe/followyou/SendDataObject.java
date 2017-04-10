package com.example.torpe.followyou;

/**
 * Created by Torpe on 2017. 02. 21..
 */

public class SendDataObject {
    protected double Latitude;
    protected double Longitude;
    protected String Time;
    protected String userId;
    protected int statusCode;

    public SendDataObject(double Latitude, double Longitude, String Time, String userId, int statusCode){
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Time = Time;
        this.userId = userId;
        this.statusCode = statusCode;
    }
}
