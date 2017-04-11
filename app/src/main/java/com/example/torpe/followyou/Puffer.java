package com.example.torpe.followyou;

/**
 * Created by Torpe on 2017. 04. 01..
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Puffer {
    protected Context context;
    private Gson gson = new Gson();

    public Puffer(Context context){
        this.context = context;
    }

    public void add(SendDataObject data){
        String json =  this.gson.toJson(data);
        Log.e ("Puffer.add", json);
        writeToFile(json);
    }

    public int getCount(){
        return 0;
    }

    public List<SendDataObject> getAll(){
        List<SendDataObject> allData = new ArrayList<SendDataObject>();
        String json = readFromFile();
        Log.e("getAll:", json);
        return allData;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FollowYou.config.pufferFile, context.MODE_PRIVATE));
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
            InputStream inputStream = context.openFileInput(FollowYou.config.pufferFile);

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


