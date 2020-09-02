package com.h13studio.fpv;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

public class ControllerOnCheckedChanged implements CompoundButton.OnCheckedChangeListener{
    private AdvancedSettingsAdapter.ControlViewHolder holder;
    private Settings settings;

    public ControllerOnCheckedChanged(final AdvancedSettingsAdapter.ControlViewHolder holder,Settings settings){
        this.holder = holder;
        this.settings = settings;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.i("CheckedChanged","OnChanged");
        switch (compoundButton.getId()){
            case R.id.Switchl:{
                if(b) {
                    holder.rockerviewl.setVisibility(View.INVISIBLE);
                    holder.seekbarl.setVisibility(View.VISIBLE);
                    holder.model.setText("滑杆");
                }else {
                    holder.rockerviewl.setVisibility(View.VISIBLE);
                    holder.seekbarl.setVisibility(View.INVISIBLE);
                    holder.model.setText("摇杆");
                }

                settings.setModeLeft(b);
                break;
            }
            case R.id.Switchr:{
                if(b){
                    holder.rockerviewr.setVisibility(View.INVISIBLE);
                    holder.seekbarr.setVisibility(View.VISIBLE);
                    holder.moder.setText("滑杆");
                }else{
                    holder.rockerviewr.setVisibility(View.VISIBLE);
                    holder.seekbarr.setVisibility(View.INVISIBLE);
                    holder.moder.setText("摇杆");
                }

                settings.setModeRight(b);
                break;
            }
        }
    }
}