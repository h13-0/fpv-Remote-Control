package com.h13studio.fpv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    private SharedPreferences datapreference;
    private SharedPreferences.Editor editor;

    public Settings(SharedPreferences Datapreference) {
        datapreference = Datapreference;
        editor = datapreference.edit();
    }

    public Boolean getModeLeft() {
        return datapreference.getBoolean("ModeLeft", new Boolean(false));
    }

    public Boolean getModeRight() {
        return datapreference.getBoolean("ModeRight", new Boolean(true));
    }

    public Boolean getCheckConfig(){
        return datapreference.getBoolean("CheckConfig", new Boolean(true));
    }

    public void setModeLeft(boolean data) {
        editor.putBoolean("ModeLeft", data).commit();
    }

    public void setModeRight(boolean data) {
        editor.putBoolean("ModeRight", data).commit();
    }

    public void setCheckConfig(boolean data){
        editor.putBoolean("CheckConfig",data).commit();
    }
}
