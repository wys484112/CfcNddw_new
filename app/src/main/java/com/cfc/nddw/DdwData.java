package com.cfc.nddw;

import android.os.Build;

public class DdwData {
    private  int  mConnectedMinutes = 0;
    private  boolean  isConnected = false;
    private  final String serial = Build.SERIAL;

    public int getmConnectedMinutes() {
        return mConnectedMinutes;
    }

    public void setmConnectedMinutes(int mConnectedMinutes) {
        this.mConnectedMinutes = mConnectedMinutes;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getSerial() {
        return serial;
    }
}
