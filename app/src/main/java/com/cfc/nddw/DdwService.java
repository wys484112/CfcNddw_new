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

import java.lang.ref.WeakReference;
import java.text.BreakIterator;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.exception.BmobException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class DdwService extends Service {

    private static final String TAG = "aaa";
    private static final boolean DBG = true;


    private static final String PREF_CONNECTED_MINUTES = "network_connected_minutes";
    private static final String PREF_DAY_MONTH_YEAR_RECORD = "day_month_year_record";


    private SharedPreferences mSharedPreferences;
    private ConnectivityManager mConnectivityManager;



//    public static final String serial = "TestSerial";//Build.SERIAL;
    public static String serial = Build.SERIAL;
    public static int  mConnectedMinutes = 0;
    public static boolean  isConnected = false;
    public static String mUpTime;


    public static String  mDayOfYearNow = " ";
    public static boolean  isUploadData = false;
    private DdwData mDATA = new DdwData();
//    public static String APPID = "52f26b1d55df6b5488d8a0a54c823c56"; //wuyinshengs appid

    public static String APPID = "90a7bac606c1cb143bbe2b9688ef6026"; //cfcs  appid

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Bmob.initialize(this,APPID);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mConnectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectedMinutes = mSharedPreferences.getInt(PREF_CONNECTED_MINUTES, 0);
        connectStatusConfig();
        getUptime();

        monitorNetworkConnectionStatus();
        //任务轮询调度方式  first  handler+
        mHandler = new MyHandler();
        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);
//        mHandler.sendEmptyMessage(EVENT_UPDATE_DISPLAY);


        //任务轮询调度方式  second  rxjava interval
//        startPostData();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private String convert(long t) {
        int s = (int) (t % 60);
        int m = (int) ((t / 60) % 60);
        int h = (int) ((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }

    private String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }
    private String getUptime(){
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        mUpTime=convert(ut);
        return mUpTime;
    }

    public static int getmConnectedMinutes() {
        return mConnectedMinutes;
    }

    public static String getSerial() {
        return serial;
    }

    private void initDdwData(){
        mDATA.setConnected(isNetWorkConnected());
        mDATA.setmConnectedMinutes(getmConnectedMinutes());
        mDATA.setSerial(getSerial());
        mDATA.setmUpTime(getUptime());
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


        //异步获取网络连接状态
        Observable.create(new ObservableOnSubscribe<Boolean>(){
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Log.e("bbb","subscribe   ObservableEmitter==111");

                Boolean isConnected=NetworkUtils.isNetworkConnected(DdwService.this);
                Log.e("bbb","subscribe   ObservableEmitter=="+isConnected);

                e.onNext(isConnected);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.e("bbb","subscribe   aBoolean=="+aBoolean);
            }
        });
        isConnected=NetworkUtils.isNetworkConnected(this);
//
//        if (mConnectivityManager != null) {
//            NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
//            if (info != null && info.isConnected()) {
//                if (info.getState() == NetworkInfo.State.CONNECTED)
//                {
//                    // 当前所连接的网络可用
//                    isConnected=true;
//                }
//            	return;
//            }
//        }
//        isConnected=false;
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

    int aa=0;
    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_UPDATE_STATS:
				Log.e(TAG, "getNowTime==" + TimeUtils.getNowTime());
				mConnectedMinutes = mSharedPreferences.getInt(PREF_CONNECTED_MINUTES, 0);
				String mDayOfYearRecorded = mSharedPreferences.getString(PREF_DAY_MONTH_YEAR_RECORD, " ");
				mDayOfYearNow = TimeUtils.getNowTime();

//				if(mConnectedMinutes<60){
				if (mDayOfYearNow.equals(mDayOfYearRecorded)) {
					if (isNetWorkConnected()) {
						Log.e(TAG,"111");
						//������������ʱ���ۼ�
						Log.e(TAG,"minutesRecorded=="+mConnectedMinutes);
						mConnectedMinutes++;
						mSharedPreferences.edit().putInt(PREF_CONNECTED_MINUTES, mConnectedMinutes).commit();
					}

				} else {
					if (isNetWorkConnected()) {
						// �ϴ����ݵ����������ݿ�
						isUploadData = true;
						Log.e(TAG,"222");
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

    //每隔 1s 执行一次任务，立即执行第一次任务，执行无限次
    private  DisposableObserver<Long> getTimeDemoObserver(){
        return  new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                postData();
            }
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private void startPostData() {
        Log.d(TAG, "startPostData");
        mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private void stopPostData() {
        Log.d(TAG, "stopPostData");
        mCompositeDisposable.dispose();
    }

    private final IBinder mBinder = new ServiceStub(this);


    /*
     * By making this a static class with a WeakReference to the Service, we
     * ensure that the Service can be GCd even when the system process still
     * has a remote reference to the stub.
     */
    static class ServiceStub extends IDdwService.Stub {
        WeakReference<DdwService> mService;

        ServiceStub(DdwService service) {
            mService = new WeakReference<DdwService>(service);
        }

        @Override
        public void startPostData() throws RemoteException {
            mService.get().startPostData();
        }

        @Override
        public void stopPostData() throws RemoteException {
            mService.get().stopPostData();

        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }
    
}