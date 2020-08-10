package com.h13studio.fpv;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdvancedSettingsAdapter extends RecyclerView.Adapter<AdvancedSettingsAdapter.ViewHolder> {


    @NonNull
    @Override
    public AdvancedSettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdvancedSettingsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            linearlayout = (LinearLayout) itemView.findViewById(R.id.bluetoothitem);
//            bluetoothname = (TextView) itemView.findViewById(R.id.bluetoothname);
//            bluetoothmac = (TextView) itemView.findViewById(R.id.bluetoothmac);
        }
    }
}
