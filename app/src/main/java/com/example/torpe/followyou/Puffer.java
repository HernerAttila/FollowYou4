package com.example.torpe.followyou;

/**
 * Created by Torpe on 2017. 04. 01..
 */

import android.content.Context;
import android.util.Log;

import java.io.IOException;
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
        return allData;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}


