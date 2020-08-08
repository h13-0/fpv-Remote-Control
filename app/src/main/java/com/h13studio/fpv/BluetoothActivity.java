package com.h13studio.fpv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.RadioAccessSpecifier;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;

public class BluetoothActivity extends AppCompatActivity {
    private RecyclerView targetlist;
    private RecyclerView unpariedtargetlist;
    private BluetoothRecyclerAdapter targetadapter;
    private BluetoothRecyclerAdapter unpairedtargetadapter;
    private Toolbar toolbar;
    private boolean checkpass = true;
    private List<String> unpairedmac = new ArrayList<String>();
    private BluetoothLeScanner scanner;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        targetadapter = new BluetoothRecyclerAdapter();
        unpairedtargetadapter = new BluetoothRecyclerAdapter();

        targetlist = findViewById(R.id.bluetoothlist);
        unpariedtargetlist = findViewById(R.id.unpaired_bluetooth_list);
        toolbar = findViewById(R.id.bluetoothbar);

        //初始化Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner.stopScan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                    }
                });
                finish();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        //设置布局管理器
        unpariedtargetlist.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
        //设置增加或删除条目的动画
        targetlist.setItemAnimator( new DefaultItemAnimator());
        targetlist.setAdapter(targetadapter);

        LinearLayoutManager unpairedlayoutManager = new LinearLayoutManager(this );
        //设置为垂直布局，这也是默认的
        unpairedlayoutManager.setOrientation(OrientationHelper. VERTICAL);
        targetlist.setLayoutManager(unpairedlayoutManager);

        unpariedtargetlist.setItemAnimator( new DefaultItemAnimator());
        unpariedtargetlist.setAdapter(unpairedtargetadapter);

        BluetoothAdapter bluetoothAdapter;
        int REQUEST_ENABLE_BT = 1;

        // 判断手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getApplicationContext(), "这台手机不支持蓝牙串口,砸了吧", Toast.LENGTH_SHORT).show();
            checkpass = false;
        }

        //获取手机本地的蓝牙适配器

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();


        //将已配对设备加入列表
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(Iterator<BluetoothDevice> iter = devices.iterator();iter.hasNext();)
        {
            BluetoothDevice device = iter.next();
            targetadapter.AddItem(device.getName(),device.getAddress());
        }

        //adapter添加监听事件
        targetadapter.setOnclick(new BluetoothRecyclerAdapter.OnClick(){
            public void onClick(View view) {
                Log.i("ClickID", (String) view.getTag());
                Intent intent = getIntent();
                //这里使用bundle绷带来传输数据
                Bundle bundle =new Bundle();
                //传输的内容仍然是键值对的形式
                bundle.putString("Mac",(String) view.getTag());
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //开始扫描未配对设备
        scanner = bluetoothAdapter.getBluetoothLeScanner();
        scanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                Log.i("Unpaired",device.getAddress());
                if(!unpairedmac.contains(device.getAddress())){
                    unpairedmac.add(device.getAddress());
                    unpairedtargetadapter.AddItem(device.getName(),device.getAddress());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scanner.stopScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                }
            });
            finish();
        }
        return super.onKeyDown(keyCode,event);
    }
}