package com.h13studio.fpv;

import android.text.SpannableString;
import android.view.View;
import android.widget.AdapterView;

public class ControlModeItemSelected implements AdapterView.OnItemSelectedListener{
    private Settings settings;
    private AdvancedSettingsAdapter.ModeViewHolder holder;

    public ControlModeItemSelected(final AdvancedSettingsAdapter.ModeViewHolder holder,Settings settings){
        this.holder = holder;
        this.settings = settings;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        settings.setConrtolMode(i);
        if(i == 1){
            holder.ControlAddress.setHint(new SpannableString("暂时不支持自动填充蓝牙MAC地址,请从主页面复制过来"));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
