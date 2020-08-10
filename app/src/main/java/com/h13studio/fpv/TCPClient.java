package com.h13studio.fpv;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
public class TCPClient extends Thread {
    private String host;
    private int port;
    private boolean MSGOccuping;
    private Socket socket;
    private String MSG = new String();
    private byte[] data;
    private boolean dataOccuping;
    private OnMainCallBack mOnMainCallBack;

    public TCPClient(String Host,int Port,OnMainCallBack mainCallBack) {
        host = Host;
        port = Port;
        this.mOnMainCallBack = mainCallBack;

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        SendtoSever();
                        Log.i("TCP", "OK");
                    } catch (IOException e) {
                        try {
                            if (socket != null)
                                socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Log.i("TCP", e.toString());
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                while (true) {
                    if(socket != null) {
                        try {
                            BufferedReader msg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            if(msg != null){
                                String data = "";
                                try{
                                    data = msg.readLine();
                                }catch (IOException e){

                                }
                                if ((data != null) && (data.length() != 0)){
                                    mOnMainCallBack.onMainCallBack(msg.readLine() + "\r\n");
                                }
                            }
                        } catch (IOException e) {

                        }
                    }else {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }



    private void SendtoSever() throws IOException {
        socket = new Socket(host, port);
        while (true) {
            if (MSG != "") {
                MSGOccuping = true;
                socket.getOutputStream().write(MSG.getBytes());
                socket.getOutputStream().flush();
                MSG = "";
                MSGOccuping = false;
            }

            if(data != null){
                dataOccuping = true;
                socket.getOutputStream().write(data);
                socket.getOutputStream().flush();
                data = null;
                dataOccuping = false;
            }
        }
    }

    public void SendMSG(String string){
        if(!MSGOccuping)
            MSG = string;
    }

    public void Sendbyte(byte[] Data){
        if(!dataOccuping)
            data = Data;
    }

    /**主线程回调接口*/
    public interface OnMainCallBack{
        void onMainCallBack(String data);
    }
}