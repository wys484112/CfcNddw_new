package com.cfc.nddw;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;      
import java.text.ParseException;      
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;      
import java.util.Date;
import java.util.TimeZone;      
public class ServiceUtils {

	/**
	 * �жϷ����Ƿ���
	 * 
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String ServiceName) {
		if (("").equals(ServiceName) || ServiceName == null)
			return false;
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.contains(ServiceName)) {
				return true;
			}
		}
		return false;
	}
}