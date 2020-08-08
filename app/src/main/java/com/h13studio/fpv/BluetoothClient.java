package com.h13studio.fpv;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;

public class BluetoothClient {
    private BluetoothSocket socket = null;
    private BluetoothDevice device;
    private BluetoothAdapter bluetoothAdapter;
    //private static BluetoothLeService mBluetoothLeService;
    private boolean checkpass = true;
    private boolean connected = false;
    private boolean connecting;
    private int connetTime;
    private String mac;
    private boolean dataoccuping,MSGoccuping;
    private String MSG = new String();
    private byte[] data;
    private OnMainCallBack mOnMainCallBack;

    public BluetoothClient(String Mac, OnMainCallBack mainCallBack){
        mac = Mac;
        Log.i("Mac",mac);
        // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mOnMainCallBack = mainCallBack;
        if(bluetoothAdapter == null)
        {
            Log.i("Adapetr","null");
        }

        device = bluetoothAdapter.getRemoteDevice(mac);

        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        }catch (IOException e){
            Log.i("Bluetooth",e.toString());
        }

        while (!connected && connetTime <= 10) {
            connectDevice();
        }

        //发送线程
        if(connected){
            new Thread(){
                @Override
                public void run(){
                    while (true){
                        try {
                            if(socket != null) {
                                SendtoSever();
                            }else {
                                Log.i("Bluetooth","null");
                            }
                        }catch (IOException e){

                        }
                    }
                }
            }.start();
        }

        //接收线程
        if(connected){
            new Thread(){
                @Override
                public void run(){
                    while (true){
                        if(socket!=null) {
                            try {
                                BufferedReader msg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                if ((msg != null) && (msg.readLine().length() != 0)) {
                                    mOnMainCallBack.onMainCallBack(msg.readLine() + "\r\n");
                                }
                            } catch (IOException e) {
                                try {
                                    socket.close();
                                    socket = null;
                                    connectDevice();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
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
    }

    //发送
    private void SendtoSever() throws IOException {
        while (true) {
            if (MSG != "") {
                MSGoccuping = true;
                socket.getOutputStream().write(MSG.getBytes());
                socket.getOutputStream().flush();
                MSG = "";
                MSGoccuping = false;
            }

            if(data != null){
                dataoccuping = true;
                socket.getOutputStream().write(data);
                socket.getOutputStream().flush();
                data = null;
                dataoccuping = false;
            }
        }
    }

    //连接到目标蓝牙设备
    protected void connectDevice() {
        try {
            // 连接建立之前的先配对
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                Method creMethod = BluetoothDevice.class
                        .getMethod("createBond");
                Log.e("TAG", "开始配对");
                creMethod.invoke(device);
            } else {
            }
        } catch (Exception e) {
            // TODO: handle exception
            //DisplayMessage("无法配对！");
            e.printStackTrace();
        }
        bluetoothAdapter.cancelDiscovery();
        try {
            socket.connect();
            Log.i("Connect","OK");
            //DisplayMessage("连接成功!");
            //connetTime++;
            connected = true;
        } catch (IOException e) {
            // TODO: handle exception
            //DisplayMessage("连接失败！");
            Log.i("Bluetooth Connect", "error: " + e.toString());
            connetTime++;
            connected = false;
            try {
                socket.close();
                socket = null;
            } catch (IOException e2) {
                // TODO: handle exception
                Log.i("Connect","error");
            }
        } finally {
            connecting = false;
        }
    }

    public void disconnect(){
        try {
            socket.close();
        }catch (IOException e){
            Log.i("Connect",e.toString());
        } finally {
            connecting = false;
        }
    }

    public void SenMsg(byte[] Data){
        if(!dataoccuping) {
            data = Data;
        }
    }

    public void SenMsg(String string){
        if(!MSGoccuping){
            MSG = string;
        }
    }

    /**主线程回调接口*/
    public interface OnMainCallBack{
        void onMainCallBack(String data);
    }
}
