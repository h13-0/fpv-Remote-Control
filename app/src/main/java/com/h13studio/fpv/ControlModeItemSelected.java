package com.h13studio.fpv;

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
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
