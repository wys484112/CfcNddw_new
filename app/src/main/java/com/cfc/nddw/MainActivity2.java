package com.cfc.nddw;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.datatype.BmobTableSchema;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;

public class MainActivity2 extends BaseActivity {

    TextView txtSerial;
    TextView txtConnectedStatus;
    TextView txtTime;
    TextView txtUpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        RxBus.get().register(this);
        mHandler = new MyHandler();
        txtTime = (TextView) findViewById(R.id.time);
        txtSerial = (TextView) findViewById(R.id.serial);
        txtConnectedStatus = (TextView) findViewById(R.id.networkconnected);
        txtUpTime = (TextView) findViewById(R.id.uptime);

        if(!ServiceUtils.isServiceRunning(this,"DdwService")){

            Intent i = new Intent(this, DdwService.class);
            startService(i);
        }

//        Person p2 = new Person();
//        p2.setName("lucky");
//        p2.setAddress("aaa");
//        p2.save(new SaveListener<String>() {
//            @Override
//            public void done(String objectId,BmobException e) {
//                if(e==null){
//                    Log.e("aaa","添加数据成功，返回objectId为："+objectId);
//                }else{
//                    Log.e("aaa","创建数据失败：" + e.getMessage());
//                }
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
//        mHandler.removeMessages(EVENT_UPDATE_STATS);

    }

    private String convert(long t) {
        int s = (int)(t % 60);
        int m = (int)((t / 60) % 60);
        int h = (int)((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }
    private String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    void updateTimes() {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        Log.e("aa", convert(ut));
        txtTime.setText(getString(R.string.connectedtime)+DdwService.mConnectedMinutes+getString(R.string.minutes));
        txtSerial.setText(getString(R.string.serialnumber)+DdwService.serial);
        txtConnectedStatus.setText(getString(R.string.networkconnectedstatus)+(DdwService.isConnected?getString(R.string.connected):getString(R.string.closed)));
        txtUpTime.setText(getString(R.string.uptime)+convert(ut));

//        mUptime.setSummary(convert(ut));
    }

    @Subscribe
    void updateTimes(DdwData data) {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        Log.e("aaAAAA", convert(ut));
        txtTime.setText(getString(R.string.connectedtime)+data.getmConnectedMinutes()+getString(R.string.minutes));
        txtSerial.setText(getString(R.string.serialnumber)+data.getSerial());
        txtConnectedStatus.setText(getString(R.string.networkconnectedstatus)+(data.isConnected()?getString(R.string.connected):getString(R.string.closed)));
        txtUpTime.setText(getString(R.string.uptime)+convert(ut));

//        mUptime.setSummary(convert(ut));
    }

    private static final int EVENT_UPDATE_STATS = 500;

    private Handler mHandler;

    private  class MyHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case EVENT_UPDATE_STATS:
                    updateTimes();
                    sendEmptyMessageDelayed(EVENT_UPDATE_STATS, 1000);
                    break;

            }
        }
    }




}
