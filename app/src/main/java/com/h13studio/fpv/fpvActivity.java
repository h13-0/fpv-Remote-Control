package com.h13studio.fpv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.rockerlibrary.view.RockerView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class fpvActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private WebView mWebView0;
    private String fpvMode;
    private String Address;
    private String host;
    private int port;
    private String ControlMode;
    private String mac;
    private com.gcssloop.widget.RockerView rockerViewl,rockerViewr;
    private SeekBar seekbarl,seekbarr;
    private MsgObject msgobject;
    private Button ControlBtn1,ControlBtn2,ControlBtn3,ControlBtn4,ControlBtn5,ControlBtn6,ControlBtn7,ControlBtn8;
    private Button LockButton,RefreshButton;
    private TextView PingView,MsgView;
    private TextView valuel,valuer;

    private Settings settings;

    //默认允许触摸和缩放WebView
    private boolean EnableWebviewTouchEvent = true;

    private enum ControlerTypy
    {
        RockerView,
        Seekbar,
        Button;
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载UI
        setContentView(R.layout.fpv_web);

        //隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //隐藏导航栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);

        //找到PingView,MsgView;
        PingView = findViewById(R.id.PingView);
        MsgView = findViewById(R.id.MsgView);

        //获取fpvAddress参数
        Intent intent = getIntent();
        fpvMode = intent.getStringExtra("fpvMode");
        Address = intent.getStringExtra("Address");
        ControlMode = intent.getStringExtra("ControlMode");

        //加载手动刷新按钮
        RefreshButton = findViewById(R.id.refreshbutton);

        //加载LockWebView按钮
        LockButton = findViewById(R.id.lockbutton);

        switch (fpvMode){
            case "http":{
                break;
            }

            case "Photo":{
                //验证储存权限
                verifyStoragePermissions(this);
                break;
            }

            default:{
                break;
            }
        }

        switch(ControlMode) {
            case "TCP": {
                host = intent.getStringExtra("Host");
                port = Integer.valueOf(intent.getStringExtra("Port")).intValue();
                msgobject = new MsgObject(host,port,MsgView);

                //注册监听事件
                RefreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mWebView0.loadUrl(Address);
                        Toast.makeText(getApplicationContext(), "正在刷新http图传...", Toast.LENGTH_SHORT).show();
                    }
                });

                LockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EnableWebviewTouchEvent = !EnableWebviewTouchEvent;
                        if(EnableWebviewTouchEvent){
                            LockButton.setBackgroundResource(R.drawable.ic_baseline_lock_open_24);
                        }else {
                            LockButton.setBackgroundResource(R.drawable.ic_baseline_lock_24);
                        }
                    }
                });

                break;
            }
            case "Bluetooth": {
                mac = intent.getStringExtra("Mac");
                Log.i("Mac",mac);
                msgobject = new MsgObject(mac,MsgView);

                //隐藏LockButton,RefreshButton
                RefreshButton.setVisibility(View.INVISIBLE);
                LockButton.setVisibility(View.INVISIBLE);
                break;
            }

            default:{
                Toast.makeText(getApplicationContext(), "什么鬼", Toast.LENGTH_SHORT).show();

                //隐藏LockButton,RefreshButton
                RefreshButton.setVisibility(View.INVISIBLE);
                LockButton.setVisibility(View.INVISIBLE);

                finish();
            }
        }

        //找到WebView
        mWebView0 = findViewById(R.id.fullweb0);

        //初始化WebView
        webviewinit(mWebView0);

        //禁用或启用WebView触摸事件
        mWebView0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return !EnableWebviewTouchEvent;
            }
        });

        //加载http页面
        mWebView0.loadUrl(Address);

        mWebView0.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                view.loadUrl(url);
                return true;
            }
        });

        //初始化设置对象
        settings = new Settings(getSharedPreferences("Settings",MODE_PRIVATE));

        //找到一堆按钮
        ControlBtn1 = findViewById(R.id.control_btn_1);
        ControlBtn2 = findViewById(R.id.control_btn_2);
        ControlBtn3 = findViewById(R.id.control_btn_3);
        ControlBtn4 = findViewById(R.id.control_btn_4);
        ControlBtn5 = findViewById(R.id.control_btn_5);
        ControlBtn6 = findViewById(R.id.control_btn_6);
        ControlBtn7 = findViewById(R.id.control_btn_7);
        ControlBtn8 = findViewById(R.id.control_btn_8);

        //注册监听事件
        ControlBtn1.setOnClickListener(new OnClick());
        ControlBtn2.setOnClickListener(new OnClick());
        ControlBtn3.setOnClickListener(new OnClick());
        ControlBtn4.setOnClickListener(new OnClick());
        ControlBtn5.setOnClickListener(new OnClick());
        ControlBtn6.setOnClickListener(new OnClick());
        ControlBtn7.setOnClickListener(new OnClick());
        ControlBtn8.setOnClickListener(new OnClick());

        //启动Ping任务
        switch (ControlMode){
            case "TCP":{
                new PingTask(host, 3000, new PingTask.OnMainCallBack() {
                    @Override
                    public void onMainCallBack(final String data) {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                ping(data);
                            }
                        });
                    }
                }).StartPingTask(3000);
                break;
            }

            default:{
                PingView.setText("");
                break;
            }
        }

        //找到左右两个控件的value反馈
        valuel = findViewById(R.id.valuel);
        valuer = findViewById(R.id.valuer);

        //找到RockerView
        rockerViewl = findViewById(R.id.rockerViewl);
        rockerViewr = findViewById(R.id.rockerViewr);

        //找到Seekbar
        seekbarl = findViewById(R.id.SeekBarl);
        seekbarr = findViewById(R.id.SeekBarr);

        if(settings.getModeLeft()){
            //则左边为滑杆
            rockerViewl.setVisibility(View.INVISIBLE);
            seekbarl.setVisibility(View.VISIBLE);

            //注册滑杆事件
            seekbarl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(b) {
                        valuel.setText("value: " + String.valueOf(i));
                        msgobject.SendMsg(ToBinaryData(ControlerTypy.Seekbar, 0, i));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    valuel.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    valuel.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            //则左边为摇杆

            rockerViewl.setVisibility(View.VISIBLE);
            seekbarl.setVisibility(View.INVISIBLE);
            valuel.setVisibility(View.VISIBLE);

            rockerViewl.setListener(new com.gcssloop.widget.RockerView.RockerListener() {
                @Override
                public void callback(int i, int i1, float v) {
                    if(i == com.gcssloop.widget.RockerView.EVENT_ACTION) {
                        //rockerViewr.setVisibility(View.INVISIBLE);
                        if (i1 == -1) {
                            valuel.setText("-1 0");
                            msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView, 0, 0));
                            return;
                        }

                        int angel = 0;
                        if (i1 >= 90) {
                            angel = i1 - 90;
                        } else {
                            angel = 270 + i1;
                        }

                        if (v <= 190) {
                            valuel.setText(String.valueOf(angel) + " " + String.valueOf((int) (v)));
                            msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView, 0, (int) (angel + 65536 * v)));
                        } else {
                            valuel.setText(String.valueOf(angel) + " " + "190");
                            msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView, 0, (int) (angel + 65536 * 190)));
                        }
                    }
                }
            });
        }

        if(settings.getModeRight()) {
            //则右边为滑杆

            //这还有个BUG,不能直接把rockerViewr设置为INVISABLE,否则左边的摇杆绘制不出来。
            rockerViewr.setActivated(false);
            rockerViewr.setRockerRadius(0);
            rockerViewr.setAreaRadius(0);
            seekbarr.setVisibility(View.VISIBLE);

            //注册滑杆事件
            seekbarr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(b) {
                        valuer.setText("value: " + String.valueOf(i));
                        msgobject.SendMsg(ToBinaryData(ControlerTypy.Seekbar, 1, i));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    valuer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    valuer.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            //则右边为摇杆
            rockerViewr.setVisibility(View.VISIBLE);
            seekbarr.setVisibility(View.INVISIBLE);
            valuer.setVisibility(View.VISIBLE);

            rockerViewr.setListener(new com.gcssloop.widget.RockerView.RockerListener() {
                @Override
                public void callback(int i, int i1, float v) {
                    if(i == com.gcssloop.widget.RockerView.EVENT_ACTION) {
                        if (i1 == -1) {
                            valuer.setText("-1 0");
                            msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView, 1, 0));
                            return;
                        }

                        int angel = 0;
                        if (i1 >= 90) {
                            angel = i1 - 90;
                        } else {
                            angel = 270 + i1;
                        }

                        if (v <= 190) {
                            valuer.setText(String.valueOf(angel) + " " + String.valueOf((int) (v)));
                            msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView, 1, (int) (angel + 65536 * v)));
                        } else {
                            valuer.setText(String.valueOf(angel) + " " + "190");
                            msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView, 1, (int) (angel + 65536 * 190)));
                        }
                    }
                }
            });
        }
        //这句也不能删,否则也会影响左边控件绘制...
        rockerViewr.setVisibility(View.VISIBLE);
    }

    //统一处理按钮的OnClick事件
    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.control_btn_1:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,0,1));
                    break;
                }

                case R.id.control_btn_2:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,1,1));
                    break;
                }

                case R.id.control_btn_3:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,2,1));
                    break;
                }

                case R.id.control_btn_4:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,3,1));
                    break;
                }

                case R.id.control_btn_5:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,4,1));
                    break;
                }

                case R.id.control_btn_6:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,5,1));
                    break;
                }

                case R.id.control_btn_7:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,6,1));
                    break;
                }

                case R.id.control_btn_8:{
                    msgobject.SendMsg(ToBinaryData(ControlerTypy.Button,7,1));
                    break;
                }

                default:{
                    break;
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    /***************************************************************/
    //将控件value打包为二进制包
    //  因为单片机的蓝牙串口速率极低,故放弃Json打包方式
    //
    //函数参数:
    //  ControlerTypy -> 标记是哪种控件
    //  ID            -> 同类控件中的ID
    //  value         -> 控件值
    //
    //数据包中数据:
    //'f' 'p' 'v':
    //  均用于标识数据包
    //
    //'ID':
    //  0x0x -> RockerView
    //  0x1x -> Slider
    //  0x2x -> Button
    /***************************************************************/
    private byte[] ToBinaryData(ControlerTypy type,int ID,int value){
        switch (type){
            case RockerView:{
                //'f','ID:0x0x','distance','value','value','p','v' -> distance特性暂不支持
                byte[] data = {0x66, 0x00, 0x00, 0x00, 0x00, 0x70, 0x76};
                data[1] = (byte) ID;
                //MD,位运算好像有bug
                data[2] = (byte) ((value/65535)%256);
                data[3] = (byte) ((value/256)%256);
                data[4] = (byte) (0x0000ff&value);

                Log.d("Byte0", String.format("%x",data[0]));
                Log.d("Byte1", String.format("%x",data[1]));
                Log.d("Byte2", String.format("%x",data[2]));
                Log.d("Byte3", String.format("%x",data[3]));
                Log.d("Byte4", String.format("%x",data[4]));
                Log.d("Byte5", String.format("%x",data[5]));
                Log.d("Byte6", String.format("%x",data[6]));

                return data;
            }

            case Seekbar:{
                //'f','ID:0x1x','value','p','v'
                byte[] data = {0x66, 0x00, 0x00, 0x70, 0x76};
                data[1] = (byte) (ID + 0x10);
                data[2] = (byte) value;

                return data;
            }

            case Button:{
                //'f','ID:0x2x','value','p','v'
                byte[] data = {0x66, 0x00, 0x00, 0x70, 0x76};
                data[1] = (byte) (ID + 0x20);
                data[2] = (byte) value;

                return data;
            }

            default:{
                return null;
            }
        }
    }

    //监听返回键,双击返回才能退出
    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
            finish();
        }
    }

    private void webviewinit(WebView webview){
        //设置WebView
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//启用JavaScript
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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

    private void ping(String string){
        PingView.setText(string);
    }
}