package com.h13studio.fpv;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.security.Policy;
import java.util.Set;

public class MsgObject implements Serializable {
    //private static final long serialVersionUID = 1L; //一会就说这个是做什么的
    //0--TCP 1--Bluetooth
    private int ControlMode;
    private TextView msgview;

    //TCP Parameters
    private transient TCPClient tcpclient;
    private String host;
    private int port;

    //Bluetooth Parameters
    private BluetoothClient bluetoothclient;
    private String mac;

    public MsgObject(String Host, int Port, TextView MsgView){
        host = Host;
        port = Port;
        ControlMode = 0;
        msgview = MsgView;
        tcpclient = new TCPClient(host, port, new TCPClient.OnMainCallBack() {
            @Override
            public void onMainCallBack(String data) {
                msgview.append(data);
            }
        });
    }

    public MsgObject(String Mac,TextView MsgView) {
        mac = Mac;
        ControlMode = 1;
        msgview = MsgView;
        bluetoothclient = new BluetoothClient(mac, new BluetoothClient.OnMainCallBack() {
            @Override
            public void onMainCallBack(String data) {
                msgview.append(data);
            }
        });
    }

    public void SendMsg(String string){
        if(ControlMode == 0){
            tcpclient.SendMSG(string);
        }else {
            bluetoothclient.SenMsg(string);
        }
    }

    public void SendMsg(byte[] data){
        if(ControlMode == 0){
            tcpclient.Sendbyte(data);
        }else {
            bluetoothclient.SenMsg(data);
        }
    }

    public void stop(){

    }
}
