package com.h13studio.fpv;

import android.annotation.SuppressLint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kongqw.rockerlibrary.view.RockerView;

public class AdvancedSettingsAdapter extends RecyclerView.Adapter<AdvancedSettingsAdapter.ViewHolder> {
    private static final int ControllerSettings = 0;
    private static final int ModeSettings = 1;
    private static final int SwitchSettings = 2;

    private Settings settings;

    public AdvancedSettingsAdapter(Settings settings){
        this.settings = settings;
    }

    @NonNull
    @Override
    public AdvancedSettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        AdvancedSettingsAdapter.ViewHolder holder = null;


        switch (viewType){
            case ControllerSettings:{
                View v = mInflater.inflate(R.layout.advanced_settings_controller,parent,false);
                holder = new ControlViewHolder(v);
                break;
            }

            case ModeSettings:{
                View v = mInflater.inflate(R.layout.advanced_settings_modeselect,parent,false);
                holder = new ModeViewHolder(v);
                break;
            }

            case SwitchSettings:{
                View v = mInflater.inflate(R.layout.advanced_settings_switch,parent,false);
                holder = new SwitchHolder(v);
                break;
            }
            default:{
                return null;
            }
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:{
                return ControllerSettings;
            }

            case 1:{
                return ModeSettings;
            }

            case 2:{
                return SwitchSettings;
            }

            default:{
                return -1;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final AdvancedSettingsAdapter.ViewHolder holder, int position) {
        if(holder instanceof ControlViewHolder){

            //Mode为Fales则为摇杆,为True则为滑杆
            if (settings.getModeLeft()) {
                ((ControlViewHolder) holder).rockerviewl.setVisibility(View.INVISIBLE);
                ((ControlViewHolder) holder).seekbarl.setVisibility(View.VISIBLE);
                ((ControlViewHolder) holder).switchl.setChecked(true);
                ((ControlViewHolder) holder).model.setText("滑杆");
            } else {
                ((ControlViewHolder) holder).rockerviewl.setVisibility(View.VISIBLE);
                ((ControlViewHolder) holder).seekbarl.setVisibility(View.INVISIBLE);
                ((ControlViewHolder) holder).switchl.setChecked(false);
                ((ControlViewHolder) holder).model.setText("摇杆");
            }

            if (settings.getModeRight()) {
                ((ControlViewHolder) holder).rockerviewr.setVisibility(View.INVISIBLE);
                ((ControlViewHolder) holder).seekbarr.setVisibility(View.VISIBLE);
                ((ControlViewHolder) holder).switchr.setChecked(true);
                ((ControlViewHolder) holder).moder.setText("滑杆");
            } else {
                ((ControlViewHolder) holder).rockerviewr.setVisibility(View.VISIBLE);
                ((ControlViewHolder) holder).seekbarr.setVisibility(View.INVISIBLE);
                ((ControlViewHolder) holder).switchr.setChecked(false);
                ((ControlViewHolder) holder).moder.setText("滑杆");
            }

            //注册监听事件
            ControllerOnCheckedChanged controllerOnCheckedChanged = new ControllerOnCheckedChanged((ControlViewHolder) holder,settings);
            ((ControlViewHolder) holder).switchl.setTag("SwitchLeft");
            ((ControlViewHolder) holder).switchl.setOnCheckedChangeListener(controllerOnCheckedChanged);
            ((ControlViewHolder) holder).switchr.setTag("SwitchRight");
            ((ControlViewHolder) holder).switchr.setOnCheckedChangeListener(controllerOnCheckedChanged);

        }else if(holder instanceof ModeViewHolder){

            //初始化UI
            ((ModeViewHolder) holder).FPVMode.setSelection(settings.getFPVMode());
            ((ModeViewHolder) holder).ControlMode.setSelection(settings.getControlMode());

            switch (settings.getFPVMode()){
                case 1:{
                    ((ModeViewHolder) holder).FPVAddress.setText(settings.gethttpAddress());
                    break;
                }

                case 2:{
                    ((ModeViewHolder) holder).FPVAddress.setText(settings.getUDPAddress());
                    break;
                }

                case 3:{
                    ((ModeViewHolder) holder).FPVAddress.setText(settings.getPhotoAddress());
                    break;
                }

                default:{
                    break;
                }
            }

            switch (settings.getControlMode()){
                case 0:{
                    ((ModeViewHolder) holder).ControlAddress.setText(settings.getTCPAddress());
                    break;
                }

                case 1:{
                    ((ModeViewHolder) holder).ControlAddress.setText(settings.getBluetoothAddress());
                    break;
                }

                default:{
                    break;
                }
            }

            //注册监听事件
            FPVModeItemSelected fpvModeItemSelected = new FPVModeItemSelected((ModeViewHolder) holder,settings);
            ((ModeViewHolder) holder).FPVMode.setOnItemSelectedListener(fpvModeItemSelected);

            ControlModeItemSelected controlModeItemSelected = new ControlModeItemSelected((ModeViewHolder) holder,settings);
            ((ModeViewHolder) holder).ControlMode.setOnItemSelectedListener(controlModeItemSelected);

            FPVAddressTextWatcher fpvAddressTextWatcher = new FPVAddressTextWatcher((ModeViewHolder) holder,settings);
            ((ModeViewHolder) holder).FPVAddress.addTextChangedListener(fpvAddressTextWatcher);

            ControlAddressTextWatcher controlAddressTextWatcher = new ControlAddressTextWatcher((ModeViewHolder) holder,settings);
            ((ModeViewHolder) holder).ControlAddress.addTextChangedListener(controlAddressTextWatcher);

        }else if (holder instanceof SwitchHolder){

            //初始化UI
            ((SwitchHolder) holder).CheckConfig.setChecked(settings.getCheckConfig());
            ((SwitchHolder) holder).CheckUpdate.setChecked(settings.getCheckUpdate());

            //注册监听事件
            SwitchOnCheckedChanged switchOnCheckedChanged = new SwitchOnCheckedChanged((SwitchHolder) holder,settings);
            ((SwitchHolder) holder).CheckConfig.setOnCheckedChangeListener(switchOnCheckedChanged);
            ((SwitchHolder) holder).CheckUpdate.setOnCheckedChangeListener(switchOnCheckedChanged);

        }
    }



    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }

    class ControlViewHolder extends AdvancedSettingsAdapter.ViewHolder {
        RockerView rockerviewl,rockerviewr;
        AppCompatSeekBar seekbarl,seekbarr;
        SwitchCompat switchl,switchr;
        TextView model,moder;

        @SuppressLint("ResourceType")
        public ControlViewHolder(@NonNull View itemView) {
            super(itemView);
            rockerviewl = itemView.findViewById(R.id.rockerViewdemol);
            rockerviewr = itemView.findViewById(R.id.rockerViewdemor);
            seekbarl = itemView.findViewById(R.id.seekbardemol);
            seekbarr = itemView.findViewById(R.id.seekbardemor);
            switchl = itemView.findViewById(R.id.Switchl);
            switchr = itemView.findViewById(R.id.Switchr);
            model = itemView.findViewById(R.id.model);
            moder = itemView.findViewById(R.id.moder);
        }
    }

    class ModeViewHolder extends AdvancedSettingsAdapter.ViewHolder {
        Spinner FPVMode,ControlMode;
        EditText FPVAddress,ControlAddress;

        @SuppressLint("ResourceType")
        public ModeViewHolder(@NonNull View itemView) {
            super(itemView);
            FPVMode = itemView.findViewById(R.id.DefaultFPVMode);
            ControlMode = itemView.findViewById(R.id.DefaultControlMode);
            FPVAddress = itemView.findViewById(R.id.DefaultFPVAddress);
            ControlAddress = itemView.findViewById(R.id.DefaultControlAddress);
        }
    }

    class SwitchHolder extends AdvancedSettingsAdapter.ViewHolder {
        Switch CheckConfig,CheckUpdate;

        @SuppressLint("ResourceType")
        public SwitchHolder(@NonNull View itemView) {
            super(itemView);
            CheckUpdate = itemView.findViewById(R.id.CheckUpdate);
            CheckConfig = itemView.findViewById(R.id.CheckConfig);
        }
    }

}
