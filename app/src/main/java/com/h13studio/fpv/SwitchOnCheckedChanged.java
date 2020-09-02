package com.h13studio.fpv;

import android.widget.CompoundButton;

public class SwitchOnCheckedChanged implements CompoundButton.OnCheckedChangeListener{
    private AdvancedSettingsAdapter.SwitchHolder holder;
    private Settings settings;

    public SwitchOnCheckedChanged(final AdvancedSettingsAdapter.SwitchHolder holder,Settings settings){
        this.holder = holder;
        this.settings = settings;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.CheckConfig:{
                settings.setCheckConfig(b);
                break;
            }

            case R.id.CheckUpdate:{
                settings.setCheckUpdate(b);
                break;
            }

            default:{
                break;
            }
        }
    }
}
