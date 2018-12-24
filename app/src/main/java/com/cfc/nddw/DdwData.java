package com.cfc.nddw;

import android.os.Build;

public class DdwData {
    private  int  mConnectedMinutes = 0;
    private  boolean  isConnected = false;
    private   String serial;
    private  String  mUpTime;

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

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    public String getmUpTime() {
        return mUpTime;
    }

    public void setmUpTime(String mUpTime) {
        this.mUpTime = mUpTime;
    }
}
