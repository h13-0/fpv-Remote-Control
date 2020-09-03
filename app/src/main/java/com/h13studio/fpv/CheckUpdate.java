package com.h13studio.fpv;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.Trace;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.spec.PSource;

public class CheckUpdate extends Thread {
    private CheckUpdate.OnMainCallBack mOnMainCallBack;

    public CheckUpdate(final int VersionCode, final CheckUpdate.OnMainCallBack mainCallBack){
        new Thread(){
            public void run() {
                int failedtimes = 0;
                boolean NeedtoUpGrade = false;
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

                            JSONObject jsonObj = JSON.parseObject(response.toString());


                            if(jsonObj.get("Latest version Code") != null){
                                if(VersionCode < (int)jsonObj.get("Latest version Code")){
                                    NeedtoUpGrade = true;
                                } else {
                                    NeedtoUpGrade = false;
                                }
                            }

                            Log.i("Update", String.valueOf(jsonObj.get("Latest version Code")));
                            break;
                        }else {
                            failedtimes ++;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                failedtimes = 0;
                while ((NeedtoUpGrade) && (failedtimes < 3)){
                    try {
                        URL url = new URL("http://www.h13studio.com/DownLoad/Ver/com.h13studio.fpv.updatelog");
                        HttpURLConnection connnection = (HttpURLConnection) url.openConnection();

                        //默认值我GET
                        connnection.setRequestMethod("GET");

                        int responseCode = connnection.getResponseCode();
                        if (responseCode == 200) {
                            System.out.println("Response Code : " + responseCode);

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(connnection.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine + "\r\n");
                            }
                            in.close();
                            mainCallBack.onMainCallBack(true,response.toString());
                            Log.i("Update",response.toString());
                            break;
                        } else {
                            failedtimes++;
                            Log.i("Update","Faild");
                        }
                    }catch (IOException e){

                    }
                }
            }
        }.start();
    }

    /**主线程回调接口*/
    public interface OnMainCallBack{
        void onMainCallBack(boolean NewVersion, String UpdateLog);
    }

    private class VersionJson{
        @JSONField(name = "Latest version Code")
        public int VersionCode;

        @JSONField(name = "Latest version")
        public String VersionName;

        @JSONField(name = "Update Log")
        public String UpdateLog;
    }
}
