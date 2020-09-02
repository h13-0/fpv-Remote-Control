package com.h13studio.fpv;

import android.os.Looper;
import android.os.Trace;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUpdate extends Thread {
    public CheckUpdate(){
        new Thread(){
            public void run() {
                int failedtimes = 0;
                super.run();
                Looper.prepare();
                while (failedtimes < 3){
                    try {
                        URL url = new URL("http://www.h13studio.com/DownLoad/Ver/com.h13studio.fpv");
                        HttpURLConnection connnection = (HttpURLConnection) url.openConnection();

                        //默认值我GET
                        connnection.setRequestMethod("GET");

                        int responseCode = connnection.getResponseCode();
                        if(responseCode == 200){
                            System.out.println("Response Code : " + responseCode);

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(connnection.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine + "\r\n");
                            }
                            in.close();
                            Log.i("Update",response.toString());


                            break;
                        }else {
                            failedtimes ++;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
