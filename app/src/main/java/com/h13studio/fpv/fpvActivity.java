package com.h13studio.fpv;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.rockerlibrary.view.RockerView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Array;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


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
    private RockerView rockerViewl,rockerViewr;
    private MsgObject msgobject;
    private Button ControlBtn1,ControlBtn2,ControlBtn3,ControlBtn4,ControlBtn5,ControlBtn6,ControlBtn7,ControlBtn8;
    private Button RefreshButton;
    private TextView PingView,MsgView;

    private enum ControlerTypy
    {
        RockerView,
        Slider,
        Button;
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        switch(ControlMode) {
            case "TCP": {
                host = intent.getStringExtra("Host");
                port = Integer.valueOf(intent.getStringExtra("Port")).intValue();
                msgobject = new MsgObject(host,port,MsgView);
                break;
            }
            case "Bluetooth": {
                mac = intent.getStringExtra("Mac");
                Log.i("Mac",mac);
                msgobject = new MsgObject(mac,MsgView);
                break;
            }
            default:{
                Toast.makeText(getApplicationContext(), "什么鬼", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        //找到WebView
        mWebView0 = findViewById(R.id.fullweb0);

        //设置WebView
        WebSettings webSettings = mWebView0.getSettings();
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

        //加载手动刷新按钮
        RefreshButton = findViewById(R.id.refreshbutton);

        //根据模式选择对应事件
        switch(ControlMode) {
            case "TCP": {
                //注册监听事件
                RefreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mWebView0.loadUrl(Address);
                        Toast.makeText(getApplicationContext(), "正在刷新http图传...", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }

            default:{
                RefreshButton.setVisibility(View.INVISIBLE);
                break;
            }
        }

        //启动Ping任务
        switch (ControlMode){
            case "TCP":{
                new PingTask(host, 3000, new PingTask.OnMainCallBack() {
                    @Override
                    public void onMainCallBack(String data) {
                        PingView.setText(data);
                    }
                }).StartPingTask(3000);
                break;
            }

            default:{
                PingView.setText("");
                break;
            }
        }

        //找到rockerViewl,rockerViewr
        rockerViewl = findViewById(R.id.rockerViewl);
        rockerViewr = findViewById(R.id.rockerViewr);

        //注册监听事件
        rockerViewl.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView,0,(int)angle));
            }

            @Override
            public void onFinish() {
            }
        });

        rockerViewr.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                msgobject.SendMsg(ToBinaryData(ControlerTypy.RockerView,1,(int)angle));
            }

            @Override
            public void onFinish() {

            }
        });
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
                //'f','ID:0','value','value','distance','p','v' -> distance特性暂不支持
                byte[] data = {0x66, 0x00, 0x00, 0x00, 0x00, 0x70, 0x76};
                data[1] = (byte) ID;
                //MD,位运算好像有bug
                data[2] = (byte) ((value/65535)%265);
                data[3] = (byte) ((value/256)%256);
                data[4] = (byte) (0x0000ff&value);

                Log.i("angle",String.valueOf(value));
                Log.i("data", String.format("0 %x",data[0]));
                Log.i("data", String.format("1 %x",data[1]));
                Log.i("data", String.format("2 %x",data[2]));
                Log.i("data", String.format("3 %x",data[3]));
                Log.i("data", String.format("4 %x",data[4]));
                Log.i("data", String.format("5 %x",data[5]));
                Log.i("data", String.format("6 %x",data[6]));
                return data;
            }

            case Slider:{
                //'f','ID:1','value','p','v'
                byte[] data = {0x66, 0x00, 0x00, 0x70, 0x76};
                data[1] = (byte) (ID + 0x10);
                data[2] = (byte) value;
                Log.i("data", String.format("0 %x",data[0]));
                Log.i("data", String.format("1 %x",data[1]));
                Log.i("data", String.format("2 %x",data[2]));
                Log.i("data", String.format("3 %x",data[3]));
                Log.i("data", String.format("4 %x",data[4]));
                return data;
            }

            case Button:{
                //'f','ID:2','value','p','v'
                byte[] data = {0x66, 0x00, 0x00, 0x70, 0x76};
                data[1] = (byte) (ID + 0x20);
                data[2] = (byte) value;
                Log.i("data", String.format("0 %x",data[0]));
                Log.i("data", String.format("1 %x",data[1]));
                Log.i("data", String.format("2 %x",data[2]));
                Log.i("data", String.format("3 %x",data[3]));
                Log.i("data", String.format("4 %x",data[4]));
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
}