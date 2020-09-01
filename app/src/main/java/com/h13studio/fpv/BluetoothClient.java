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
    private boolean checkpass = true;
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
            Log.w("Adapetr","BluetoothAdapter is null.");
        }

        device = bluetoothAdapter.getRemoteDevice(mac);

        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        }catch (IOException e){
            Log.w("Bluetooth",e.toString());
        }

        ConnectDevice();

        //发送线程
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (socket.isConnected()) {
                        try {
                            SendtoSever();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.w("Bluetooth", "Bluetooth Client lose connection.");
                        mOnMainCallBack.onMainCallBack("Bluetooth Client lose connection.\r\n");
                        if (socket != null) {
                            ConnectDevice();
                        }
                    }
                }
            }
        }.start();


        //接收线程
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (socket.isConnected()) {
                        try {
                            BufferedReader msg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            if ((msg != null) && (msg.readLine().length() != 0)) {
                                mOnMainCallBack.onMainCallBack(msg.readLine() + "\r\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
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

    //发送
    private void SendtoSever() throws IOException {
        while (true) {
            if (socket.isConnected()) {
                if (MSG != "") {
                    MSGoccuping = true;
                    socket.getOutputStream().write(MSG.getBytes());
                    socket.getOutputStream().flush();
                    MSG = "";
                    MSGoccuping = false;
                }

                if (data != null) {
                    dataoccuping = true;
                    socket.getOutputStream().write(data);
                    socket.getOutputStream().flush();
                    data = null;
                    dataoccuping = false;
                }
            }
        }
    }

    //连接到目标蓝牙设备
    protected void ConnectDevice() {
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
            Log.e("Bluetooth","配对失败");
            e.printStackTrace();
        }
        bluetoothAdapter.cancelDiscovery();
        try {
            if(socket != null) {
                socket.connect();
            }
            Log.i("Connect","OK");
            mOnMainCallBack.onMainCallBack("Bluetooth Client Connected.\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            socket.close();
        }catch (IOException e){
            Log.i("Connect",e.toString());
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
