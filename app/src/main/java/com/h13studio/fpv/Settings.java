package com.h13studio.fpv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    private SharedPreferences datapreference;
    private SharedPreferences.Editor editor;

    public final int http = 0;
    public final int UDP = 1;
    public final int Photo = 2;

    public Settings(SharedPreferences Datapreference) {
        datapreference = Datapreference;
        editor = datapreference.edit();
    }


    /**
     * @brief: 图传界面左侧控件类型
     * @return: True -> 滑杆 False -> 摇杆
     */
    public Boolean getModeLeft() {
        return datapreference.getBoolean("ModeLeft", new Boolean(false));
    }

    public void setModeLeft(boolean data) {
        editor.putBoolean("ModeLeft", data).commit();
    }


    /**
     * @brief: 图传界面右侧控件类型
     * @return: True -> 滑杆 False -> 摇杆
     */
    public Boolean getModeRight() {
        return datapreference.getBoolean("ModeRight", new Boolean(true));
    }

    public void setModeRight(boolean data) {
        editor.putBoolean("ModeRight", data).commit();
    }


    /**
     * @brief: 在正式遥控前是否强制检测配置项
     * @return: True or False
     */
    public Boolean getCheckConfig(){
        return datapreference.getBoolean("CheckConfig", new Boolean(true));
    }

    public void setCheckConfig(boolean data){
        editor.putBoolean("CheckConfig",data).commit();
    }

    /**
     * @brief: 有更新时提示
     * @return: True or False
     */
    public Boolean getCheckUpdate(){
        return datapreference.getBoolean("CheckUpdate", new Boolean(true));
    }

    public void setCheckUpdate(boolean data){
        editor.putBoolean("CheckUpdate",data).commit();
    }

    /**
     * @brief: 遥控模式
     * @return:
     *     0 -> TCP
     *     1 -> Bluetooth
     */
    public int getControlMode(){
        return datapreference.getInt("ControlMode", 0);
    }

    public void setConrtolMode(int Mode){
        editor.putInt("ControlMode", Mode).commit();
    }


    /**
     * @brief: 图传模式
     * @return:
     *     0 -> http
     *     1 -> UDP
     *     2 -> Photo
     */
    public int getFPVMode(){
        return datapreference.getInt("FPVMode", 0);
    }

    public void setFPVMode(int Mode){
        editor.putInt("FPVMode",Mode).commit();
    }


    /**
     * @brief: http Address
     * @return: URL
     */
    public String gethttpAddress(){
        return datapreference.getString("httpAddress","");
    }

    public void sethttpAddress(String address){
        editor.putString("httpAddress",address).commit();
    }


    /**
     * @brief: UDP Address
     * @return: URL
     */
    public String getUDPAddress(){
        return datapreference.getString("UDPAddress","");
    }

    public void setUDPAddress(String address){
        editor.putString("UDPAddress",address).commit();
    }


    /**
     * @brief: Photo Address
     * @return: Path
     */
    public String getPhotoAddress(){
        return datapreference.getString("PhotoAddress","");
    }

    public void setPhotoAddress(String address){
        editor.putString("PhotoAddress",address).commit();
    }


    /**
     * @brief: TCP Address
     * @return: Address
     */
    public String getTCPAddress(){
        return datapreference.getString("TCPAddress","");
    }

    public void setTCPAddress(String address){
        editor.putString("TCPAddress",address).commit();
    }


    /**
     * @brief: Bluetooth Address
     * @return: MAC Address
     */
    public String getBluetoothAddress(){
        return datapreference.getString("BluetoothAddress","");
    }

    public void setBluetoothAddress(String address){
        editor.putString("BluetoothAddress",address).commit();
    }
}
