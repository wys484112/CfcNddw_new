package com.cfc.nddw;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;

public class NDdwInfo extends BmobObject {
    private String serialNum;
    private int dayRunTime;

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public int getDayRunTime() {
        return dayRunTime;
    }

    public void setDayRunTime(int dayRunTime) {
        this.dayRunTime = dayRunTime;
    }

}