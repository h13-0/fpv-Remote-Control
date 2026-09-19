package com.h13studio.fpv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

public class AdvancedSettings extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AdvancedSettingsAdapter recycleradapter;

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

        recycleradapter = new AdvancedSettingsAdapter(settings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager(layoutManager);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(recycleradapter);
    }
}