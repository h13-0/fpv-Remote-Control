package com.h13studio.fpv;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.security.acl.Group;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private Button mBtn_Linear;
    private DrawerLayout drawer;
    private TextView fpvAddress,controladdress,EventLog;
    private Spinner fpvModeSpinner,ControlModeSpinner;
    private int fpvMode = 0;
    private int controlMode = 0;

    //用于标记应用是否刚刚打开,这样可以避免刚打开APP就有烦人的Toast
    private boolean firstrun = true;

    //找到侧边抽屉的navigationmenu控件
    private NavigationView navigationview;

    //标记是否成功选择目标蓝牙设备
    private boolean BluetoothTargetSelected = false;

    //控制对象
    private MsgObject msgobject;

    private Bundle bundletofpvactivity;

    //标记是否通过条件检查
    private boolean checkpass = true;

    //App设置
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到侧边抽屉控件
        drawer = findViewById(R.id.drawer_layout);
        //找到fpvAddress,controladdress,EventLog控件
        fpvAddress = findViewById(R.id.fpvaddress);
        controladdress = findViewById(R.id.controladdress);
        EventLog = findViewById(R.id.MainEventLog);
        //找到ToolBar控件
        toolbar = (Toolbar)findViewById(R.id.ToolBarMain0);
        //找到Start按钮
        mBtn_Linear = findViewById(R.id.startfpv);
        //找到fpvModeSpinner,控件
        fpvModeSpinner = findViewById(R.id.fpvModeSpinner);
        ControlModeSpinner = findViewById(R.id.ControlModeSpinner);
        //找到navigation控件
        navigationview = findViewById(R.id.nav_view);

        //初始化App设置
        settings = new Settings(getSharedPreferences("Settings",MODE_PRIVATE));

        //初始化ToolBar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_dehaze_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START, true);

            }
        });

        //设置fpv模式修改监听事件
        fpvModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:{
                        fpvMode = 0;
                        fpvAddress.setFocusable(true);
                        fpvAddress.setFocusableInTouchMode(true);
                        fpvAddress.setHint(new SpannableString("eg: http://192.168.192.101:80"));
                        if(!firstrun) {
                            Toast.makeText(getApplicationContext(), "不输入则默认白色背景", Toast.LENGTH_SHORT).show();
                        }
                        EventLog.append("fpv Service is running on http mode...\r\n");
                        break;
                    }
                    case 1:{
                        fpvMode = 1;
                        fpvAddress.setHint(new SpannableString("暂不支持,敬请期待"));
                        fpvAddress.setFocusable(false);
                        fpvAddress.setFocusableInTouchMode(false);
                        Toast.makeText(getApplicationContext(), "虽然理论上是更好的解决方案,但是暂时不支持,所以请关注更新哦~", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2:{
                        fpvMode = 2;
                        fpvAddress.setFocusable(false);
                        fpvAddress.setFocusableInTouchMode(false);

                        //让用户选择图片
                        choosePhoto();

                        //验证储存权限
                        verifyStoragePermissions(MainActivity.this);

                        EventLog.append("fpv Service is disabled...\r\n");
                        break;
                    }
                    default:{
                        fpvMode = 0;
                        fpvAddress.setHint(new SpannableString("暂不支持,敬请期待"));
                        fpvAddress.setFocusable(false);
                        fpvAddress.setFocusableInTouchMode(false);
                        Toast.makeText(getApplicationContext(), "暂不支持", Toast.LENGTH_SHORT).show();
                        EventLog.append("It's a feature, not a bug.\r\n");
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ControlModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:{
                        controlMode = 0;
                        controladdress.setFocusable(true);
                        controladdress.setFocusableInTouchMode(true);
                        controladdress.setHint(new SpannableString("eg: 192.168.192.101:8080"));
                        EventLog.append("Control Sevrice is running on TCP Mode...\r\n");
                        break;
                    }

                    case 1:{
                        controlMode = 1;
//                        controladdress.setFocusable(false);
//                        controladdress.setFocusableInTouchMode(false);

                        EventLog.append("Control Sevrice is running on Bluetooth Mode...\r\n");
                        //这里该让用户选择目标蓝牙设备了
                        SelectBluetoothTarget();
                        break;
                    }

                    default:{
                        controlMode = -1;
                        controladdress.setFocusable(false);
                        controladdress.setFocusableInTouchMode(false);
                        Toast.makeText(getApplicationContext(), "暂不支持", Toast.LENGTH_SHORT).show();
                        EventLog.append("It's a feature, not a bug.\r\n");
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //为侧边栏item设置监听事件
        navigationview.setNavigationItemSelectedListener(this);

        //轮流使用EditText焦点
        fpvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fpvAddress.requestFocus();
                controladdress.clearFocus();
            }
        });

        controladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fpvAddress.clearFocus();
                controladdress.requestFocus();
            }
        });

        //初始化Start按钮
        mBtn_Linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventLog.setMovementMethod(new ScrollingMovementMethod());
                EventLog.setText("");
                checkpass = true;
                switch (ControlModeSpinner.getSelectedItem().toString()) {
                    case "TCP": {
                        EventLog.append("Ping TCP Sever...\r\n");

                        //提取IP和端口
                        String[] temp;
                        String host = new String();
                        int port = 0;
                        temp = controladdress.getText().toString().split(":");
                        if (temp.length == 2) {
                            host = temp[0];
                            port = Integer.valueOf(temp[1]).intValue();
                        } else {
                            Toast.makeText(getApplicationContext(), "请检查TCP地址设置", Toast.LENGTH_SHORT).show();
                            EventLog.append("TCP Address error!\r\n");
                            checkpass = false;
                        }

                        //测试连接性
                        final int finalPort = port;
                        final String finalHost = host;
                        new CheckHost(EventLog, host, new CheckHost.OnMainCallBack() {
                            @Override
                            public void onMainCallBack(Boolean checkpass) {
                                if((checkpass) || (!settings.getCheckConfig())) {
                                    fpvActivity fpv = new fpvActivity();

                                    Intent intent = new Intent(MainActivity.this, fpv.getClass());

                                    intent.putExtra("fpvMode", getfpvmode());
                                    intent.putExtra("Address", fpvAddress.getText().toString());

                                    intent.putExtra("ControlMode", "TCP");
                                    intent.putExtra("Host", finalHost);
                                    intent.putExtra("Port", String.valueOf(finalPort));
                                    startActivity(intent);
                                } else {
                                    EventLog.append("Config Check error!");
                                }
                            }
                        }).start();
                        break;
                    }

                    case "蓝牙串口": {

                        if(BluetoothTargetSelected) {
                            //跳转到控制界面
                            if (BluetoothTargetSelected && (controladdress.getText() != "")) {
                                fpvActivity fpv = new fpvActivity();

                                Intent intent = new Intent(MainActivity.this, fpv.getClass());

                                intent.putExtra("fpvMode", getfpvmode());
                                intent.putExtra("Address", fpvAddress.getText().toString());

                                intent.putExtra("ControlMode", "Bluetooth");
                                intent.putExtra("Mac", controladdress.getText().toString());
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    default: {
                        Toast.makeText(getApplicationContext(), "暂不支持。", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });

        navigationview.setNavigationItemSelectedListener(this);
        firstrun = false;
    }

    private String getfpvmode(){
        switch (fpvMode){
            case 0:{
                return "http";
            }

            case 1:{
                //暂且先不做UDP
                //return "UDP";
                return "http";
            }

            case 2:{
                return "Photo";
            }

            default:{
                return "Unknow";
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.QuickStart:{
                Intent intent = new Intent();
                //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.h13studio.com/fpv%E5%9B%BE%E4%BC%A0%E9%81%A5%E6%8E%A7%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B/");
                intent.setData(content_url);
                startActivity(intent);

                break;
            }

            case R.id.Score:{
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }

            case R.id.Aboutme:{
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.h13studio.com/");
                intent.setData(content_url);
                startActivity(intent);
                break;
            }

            case R.id.Update:{
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://www.coolapk.com/apk/com.h13studio.fpv");
                intent.setData(content_url);
                startActivity(intent);
                break;
            }

            case R.id.Settings:{
                AdvancedSettings settings = new AdvancedSettings();
                Intent intent = new Intent(MainActivity.this, settings.getClass());
                startActivity(intent);
                break;
            }

            case R.id.SourceCode:{
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://github.com/h13-0/fpv-Remote-Control");
                intent.setData(content_url);
                startActivity(intent);
                break;
            }

            default:{
                Toast.makeText(getApplicationContext(), "暂不支持。", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    //测试TCP连接性线程
    public static class CheckHost extends Thread {
        private String[] temp;
        private TextView tv;
        private String host;
        private OnMainCallBack mOnMainCallBack;
        private boolean checkpass = true;

        public CheckHost(TextView textview,String Host,OnMainCallBack mainCallBack){
            tv = textview;
            host = Host;
            this.mOnMainCallBack=mainCallBack;
        }

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            PingTask ping = new PingTask(host, 3);
            String Source;
            String ttl, time;
            for (int t = 0; t < 5; t++) {
                Source = ping.Ping();
                if (Source.contains("time=")) {
                    temp = Source.split("ttl=");
                    temp = temp[1].split(" ");
                    ttl = temp[0];
                    time = temp[1];
                    time = time.replace("time=","");
                    tv.append("ping: " + host + ", ttl = " + ttl + ", time = " + time + " ms.\r\n");
                    checkpass = true;
                } else {
                    tv.append(Source + "\r\n");
                    checkpass = false;
                }
            }

            mOnMainCallBack.onMainCallBack((Boolean)(checkpass));
        }

        /**主线程回调接口*/
        public interface OnMainCallBack{
            void onMainCallBack(Boolean Checkpass);
        }

    }

    //选择蓝牙目标
    private boolean SelectBluetoothTarget(){
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
        if (checkpass) {
            // 打开蓝牙权限
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                //弹出对话框，请求打开蓝牙
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
            }
        }

        if (checkpass) {
            int timeused = 0;
            while (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeused++;
                if (timeused > 5) {
                    Toast.makeText(getApplicationContext(), "获取蓝牙权限超时,请重新获取", Toast.LENGTH_SHORT).show();
                    checkpass = false;
                    break;
                }
            }
        }

        //跳转到选择配对设备界面
        if (checkpass) {
            BluetoothActivity bluetoothactivity = new BluetoothActivity();
            Intent i = new Intent(MainActivity.this, bluetoothactivity.getClass());
            startActivityForResult(i, 0);
        }

        return checkpass;
    }



    //选择蓝牙目标之后的回调程序
    //0->bluetooth
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //蓝牙配对界面返回
            case 0: {
                if(resultCode==RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String text =null;
                    if(bundle!=null)
                        text=bundle.getString("Mac");
                    Log.d("result",text);
                    controladdress.setText(text);
//                  controladdress.setFocusable(false);
//                  controladdress.setFocusableInTouchMode(false);
                    //mBtn_Linear.setClickable(false);

                    EventLog.append("Select Bluetooth target at" + text);
                    BluetoothTargetSelected = true;
                }

                break;
            }

            case 1: {
                if(data != null) {
                    if(data.getData() != null) {
                        Uri uri = data.getData();
                        String filePath = FileUtil.getFilePathByUri(this, uri);
                        if (!TextUtils.isEmpty(filePath)) {
                            fpvAddress.setText("file://" + filePath);
                        }
                        break;
                    }
                }
            }
        }

    }

    //从相册选取图片
    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, 1);
    }

    //动态申请读写储存权限
    //先定义
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    //然后通过一个函数来申请
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

