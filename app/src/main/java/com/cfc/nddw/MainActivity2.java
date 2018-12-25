package com.cfc.nddw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity2 extends BaseActivity {
    private static final boolean DBG = true;
    private static final String TAG = "aaa";

    @BindView(R.id.networkconnected)
    TextView txtConnectedStatus;
    @BindView(R.id.serial)
    TextView txtSerial;
    @BindView(R.id.time)
    TextView txtTime;
    @BindView(R.id.uptime)
    TextView txtUpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        RxBus.get().register(this);
        mHandler = new MyHandler();

        mToken = ServiceUtils.bindToService(this, osc);
        if (mToken == null) {
            // something went wrong
            Toast.makeText(this, "service error!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //handle style to update display
//        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        ServiceUtils.stopPostData();
        ServiceUtils.unbindFromService(mToken);


        //handle style to update display
//        mHandler.removeMessages(EVENT_UPDATE_STATS);

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

    void updateTimes() {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        if (DBG)
            Log.e(TAG, convert(ut));
        txtTime.setText(getString(R.string.connectedtime) + DdwService.mConnectedMinutes + getString(R.string.minutes));
        txtSerial.setText(getString(R.string.serialnumber) + DdwService.serial);
        txtConnectedStatus.setText(getString(R.string.networkconnectedstatus) + (DdwService.isConnected ? getString(R.string.connected) : getString(R.string.closed)));
        txtUpTime.setText(getString(R.string.uptime) + convert(ut));

//        mUptime.setSummary(convert(ut));
    }

    @Subscribe
    public void updateTimes(DdwData data) {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        if (DBG)
            Log.e(TAG, convert(ut));
        txtTime.setText(getString(R.string.connectedtime) + data.getmConnectedMinutes() + getString(R.string.minutes));
        txtSerial.setText(getString(R.string.serialnumber) + data.getSerial());
        txtConnectedStatus.setText(getString(R.string.networkconnectedstatus) + (data.isConnected() ? getString(R.string.connected) : getString(R.string.closed)));
        txtUpTime.setText(getString(R.string.uptime) + data.getmUpTime());

//        mUptime.setSummary(convert(ut));
    }

    private static final int EVENT_UPDATE_STATS = 500;

    private Handler mHandler;

    private class MyHandler extends Handler {


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

    private ServiceUtils.ServiceToken mToken;
    private IDdwService mService = null;
    private ServiceConnection osc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IDdwService.Stub.asInterface(obj);

            if (mService != null) {
                try {
                    mService.startPostData();
                } catch (RemoteException ex) {
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            if (DBG)
                Log.e(TAG,"onServiceDisconnected");
            mService = null;
        }
    };
}
