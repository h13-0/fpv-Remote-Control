package com.h13studio.fpv;

import android.text.Editable;
import android.text.TextWatcher;

public class ControlAddressTextWatcher implements TextWatcher {
    private AdvancedSettingsAdapter.ModeViewHolder holder;
    private Settings settings;

    public ControlAddressTextWatcher(final AdvancedSettingsAdapter.ModeViewHolder holder,Settings settings){
        this.holder = holder;
        this.settings = settings;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        switch (holder.ControlMode.getSelectedItemPosition()){
            case 0:{
                settings.setTCPAddress(editable.toString());
                break;
            }

            case 1:{
                settings.setBluetoothAddress(editable.toString());
                break;
            }

            default:{
                break;
            }
        }
    }
}
