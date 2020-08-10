package com.h13studio.fpv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.Toolbar;

import com.kongqw.rockerlibrary.view.RockerView;

public class AdvancedSettings extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AdvancedSettingsAdapter recycleradapter;
    private SwitchCompat switchl,switchr;
    private RockerView rockerviewl,rockerviewr;
    private AppCompatSeekBar seekbarl,seekbarr;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);

        recyclerView = findViewById(R.id.AdvancedSettingsRecyclerview);
        toolbar = findViewById(R.id.AdvancedSettingsToolBar);

        settings = new Settings(getSharedPreferences("Settings",MODE_PRIVATE));

        //初始化Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recycleradapter = new AdvancedSettingsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager(layoutManager);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(recycleradapter);

        //找到摇杆demmo
        rockerviewl = findViewById(R.id.rockerViewdemol);
        rockerviewr = findViewById(R.id.rockerViewdemor);

        //找到滑杆demo
        seekbarl = findViewById(R.id.seekbardemol);
        seekbarr = findViewById(R.id.seekbardemor);

        //找到Switch
        switchl = findViewById(R.id.Switchl);
        switchr = findViewById(R.id.Switchr);

        //Mode为Fales则为摇杆,为True则为滑杆
        if (settings.getModeLeft()) {
            rockerviewl.setVisibility(View.INVISIBLE);
            seekbarl.setVisibility(View.VISIBLE);
        } else {
            rockerviewl.setVisibility(View.VISIBLE);
            seekbarl.setVisibility(View.INVISIBLE);
        }

        if (settings.getModeRight()) {
            rockerviewr.setVisibility(View.INVISIBLE);
            seekbarr.setVisibility(View.VISIBLE);
        } else {
            rockerviewr.setVisibility(View.VISIBLE);
            seekbarr.setVisibility(View.INVISIBLE);
        }

        switchl.setChecked(settings.getModeLeft());
        switchr.setChecked(settings.getModeRight());

        //设置监听事件
        switchl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    rockerviewl.setVisibility(View.INVISIBLE);
                    seekbarl.setVisibility(View.VISIBLE);
                }else{
                    rockerviewl.setVisibility(View.VISIBLE);
                    seekbarl.setVisibility(View.INVISIBLE);
                }

                settings.setModeLeft(b);
            }
        });

        switchr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    rockerviewr.setVisibility(View.INVISIBLE);
                    seekbarr.setVisibility(View.VISIBLE);
                }else{
                    rockerviewr.setVisibility(View.VISIBLE);
                    seekbarr.setVisibility(View.INVISIBLE);
                }

                settings.setModeRight(b);
            }
        });
    }
}