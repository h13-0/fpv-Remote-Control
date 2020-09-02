package com.h13studio.fpv;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class FPVAddressTextWatcher implements TextWatcher {
    private Settings settings;
    private AdvancedSettingsAdapter.ModeViewHolder holder;

    public FPVAddressTextWatcher(final AdvancedSettingsAdapter.ModeViewHolder holder,Settings settings){
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
        switch (holder.FPVMode.getSelectedItemPosition()){
            case 0:{
                settings.sethttpAddress(editable.toString());
                break;
            }

            case 1:{
                settings.setUDPAddress(editable.toString());
                break;
            }

            case 2:{
                settings.setPhotoAddress(editable.toString());
                break;
            }

            default:{
                break;
            }
        }
    }
}
