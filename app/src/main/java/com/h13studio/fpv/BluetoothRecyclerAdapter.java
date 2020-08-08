package com.h13studio.fpv;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothRecyclerAdapter extends RecyclerView.Adapter<BluetoothRecyclerAdapter.ViewHolder>  {
    private List<String> mac;
    private List<String> name;
    private OnClick onclick;

    @NonNull
    @Override
    public BluetoothRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        @SuppressLint("ResourceType") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothRecyclerAdapter.ViewHolder holder, final int position) {
        if(this.getItemCount() != 0) {
            holder.bluetoothmac.setText(mac.get(position));
            holder.bluetoothname.setText(name.get(position));
            holder.linearlayout.setOnClickListener(onclick);
            holder.linearlayout.setTag(mac.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearlayout;
        TextView bluetoothname,bluetoothmac;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearlayout = (LinearLayout) itemView.findViewById(R.id.bluetoothitem);
            bluetoothname = (TextView) itemView.findViewById(R.id.bluetoothname);
            bluetoothmac = (TextView) itemView.findViewById(R.id.bluetoothmac);
        }
    }

    public void AddItem(String Name,String Mac){
        name.add(Name);
        mac.add(Mac);
    }

    public BluetoothRecyclerAdapter(){
        mac = new ArrayList<String>();
        name = new ArrayList<String>();
    }

    public static class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Log.i("ClickID","");
        }
    }

    public void setOnclick(OnClick onClick){
        onclick = onClick;
    }
}
