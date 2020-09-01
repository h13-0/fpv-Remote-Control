package com.h13studio.fpv;

import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

// 创建ping任务
public class PingTask extends Thread {
    String host;
    int timelimit;
    private OnMainCallBack mOnMainCallBack;

    public PingTask(String Host,int TimeLimit){
        host = Host;
        timelimit = TimeLimit;
    }

    public PingTask(String Host, int TimeLimit, OnMainCallBack mainCallBack){
        host = Host;
        timelimit = TimeLimit;
        this.mOnMainCallBack = mainCallBack;
    }

    public String Ping(){
        StringBuffer buffer = new StringBuffer();

        try {
            Process p = null;
            p = Runtime.getRuntime().exec("ping -c 1 -w " + timelimit + " " + host);
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            Log.i("Ping", buffer.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    
    public void StartPingTask(final int delay){
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                Process p = null;
                String Source = "";

                while (true) {
                    try {
                        p = Runtime.getRuntime().exec("ping -c 1 -w " + timelimit + " " + host);
                        InputStream input = p.getInputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(input));
                        String line = "";
                        if(in.readLine() != null)
                        {
                            Source = in.readLine();
                            Log.i("Ping", Source);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("error",e.toString());
                    }

                    String[] temp;
                    String time;

                    if (Source.contains("time=")) {
                        temp = Source.split("ttl=");
                        temp = temp[1].split(" ");
                        time = temp[1];
                        time = time.replace("time=","");
                        Source = ("Ping = " + time + "ms");
                    } else {
                        Source = "TCP ping error!";
                    }

                    mOnMainCallBack.onMainCallBack(Source);

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**主线程回调接口*/
    public interface OnMainCallBack{
        void onMainCallBack(String data);
    }

    public void StopPingTask()
    {

    }
}
