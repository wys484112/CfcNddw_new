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



import java.util.List;

public class DdwReceiver extends BroadcastReceiver {

    private static final String TAG = "DdwReceiver";
    private static final boolean DBG = true;
    private static final String BOOT_COMPLETE_FLAG = "boot_complete";
    private static final String MANUAL_REGISTRATION_FLAG = "manual";
    private static final String RETRY_FLAG = "retry";
    private static final String DDS_SWITCHED_FLAG = "dds_switched";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DBG) {
            Log.d(TAG, "onReceived action:" + intent.getAction());
        }
        Intent i = new Intent(context, DdwService.class);
        String receivedAction = intent.getAction();
        if (receivedAction.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // start service to do the work.
            if (DBG) {
                Log.d(TAG, "Action boot completed received..");
            }
            i.putExtra(BOOT_COMPLETE_FLAG, true);
            context.startService(i);
        }
    }
}