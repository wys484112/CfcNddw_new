/**
 * Copyright (c) 2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

package com.cfc.nddw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;

import java.text.BreakIterator;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.exception.BmobException;

public class DdwService extends Service {

    private static final String TAG = "DdwService";
    private static final boolean DBG = true;


    private static final String PREF_CONNECTED_MINUTES = "network_connected_minutes";
    private static final String PREF_DAY_MONTH_YEAR_RECORD = "day_month_year_record";


    private SharedPreferences mSharedPreferences;
    private ConnectivityManager mConnectivityManager;


//    public static final String serial = "TestSerial";//Build.SERIAL;
    public static final String serial = Build.SERIAL;
    public static int  mConnectedMinutes = 0;
    public static boolean  isConnected = false;

    public static String  mDayOfYearNow = " ";
    public static boolean  isUploadData = false;
    private DdwData mDATA = new DdwData();
//    public static String APPID = "52f26b1d55df6b5488d8a0a54c823c56"; //wuyinshengs appid

    public static String APPID = "90a7bac606c1cb143bbe2b9688ef6026"; //cfcs  appid

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Bmob.initialize(this,APPID);


        mHandler = new MyHandler();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mConnectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectedMinutes = mSharedPreferences.getInt(PREF_CONNECTED_MINUTES, 0);
        monitorNetworkConnectionStatus();
        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);
        mHandler.sendEmptyMessage(EVENT_UPDATE_DISPLAY);

        return START_STICKY;
    }

    private void initDdwData(){
        mDATA.setConnected(isConnected);
        mDATA.setmConnectedMinutes(mConnectedMinutes);
    }

    private void postData(){
        initDdwData();
        RxBus.get().post(mDATA);
    }
    private void monitorNetworkConnectionStatus() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    connectStatusConfig();
                    postData();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }
    

    private void connectStatusConfig() {
        if (mConnectivityManager != null) {
            NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
            	isConnected=true;
            	return;
            }
        }
        isConnected=false;
    }
    private boolean isNetWorkConnected() {
    	return isConnected;
    }

    private static final int EVENT_UPDATE_STATS = 500;
    private static final int EVENT_UPDATE_DISPLAY = 501;

    private Handler mHandler;

    private void uploadData(int minutes){
        NDdwInfo  mInfo = new NDdwInfo();
        mInfo.setSerialNum(serial);
        mInfo.setDayRunTime(minutes);
        mInfo.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    mConnectedMinutes = 1;
                    mSharedPreferences.edit().putInt(PREF_CONNECTED_MINUTES, 1).commit();
                    Log.e("aaa","添加数据成功，返回objectId为："+objectId);
                }else{
                    Log.e("aaa","创建数据失败：" + e.getMessage());
                }
            }
        });
    }

    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_UPDATE_STATS:
				Log.e("aa", "getNowTime==" + TimeUtils.getNowTime());
				mConnectedMinutes = mSharedPreferences.getInt(PREF_CONNECTED_MINUTES, 0);
				String mDayOfYearRecorded = mSharedPreferences.getString(PREF_DAY_MONTH_YEAR_RECORD, " ");
				mDayOfYearNow = TimeUtils.getNowTime();

//				if(mConnectedMinutes<60){
				if (mDayOfYearNow.equals(mDayOfYearRecorded)) {
					if (isNetWorkConnected()) {
						Log.e("aa","111");
						//������������ʱ���ۼ�
						Log.e("aa","minutesRecorded=="+mConnectedMinutes);
						mConnectedMinutes++;
						mSharedPreferences.edit().putInt(PREF_CONNECTED_MINUTES, mConnectedMinutes).commit();
					}

				} else {
					if (isNetWorkConnected()) {
						// �ϴ����ݵ����������ݿ�
						isUploadData = true;
						Log.e("aa","222");
						//��������ʱ�仹ԭ
                        uploadData(mConnectedMinutes);
                        mSharedPreferences.edit().putString(PREF_DAY_MONTH_YEAR_RECORD, mDayOfYearNow).commit();
					}

				}
//                postData();
				sendEmptyMessageDelayed(EVENT_UPDATE_STATS, 60000);
				break;
                case EVENT_UPDATE_DISPLAY:
                    postData();
                    sendEmptyMessageDelayed(EVENT_UPDATE_DISPLAY, 1000);
                break;

            }
        }
    }
    
}