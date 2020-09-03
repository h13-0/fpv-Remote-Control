package com.h13studio.fpv;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class FPVModeItemSelected implements AdapterView.OnItemSelectedListener{
    private Settings settings;
    private AdvancedSettingsAdapter.ModeViewHolder holder;

    public FPVModeItemSelected(final AdvancedSettingsAdapter.ModeViewHolder holder,Settings settings){
        this.holder = holder;
        this.settings = settings;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        settings.setFPVMode(i);
        if(i == 2){
            holder.FPVAddress.setHint(new SpannableString("暂时不支持自动填充图片URL,请从主页面复制过来"));
        } else {
            holder.FPVAddress.setHint(new SpannableString(""));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}


