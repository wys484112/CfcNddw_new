package com.cfc.nddw;

import android.util.Log;      
import java.text.ParseException;      
import java.text.SimpleDateFormat;      
import java.util.Calendar;      
import java.util.Date;
import java.util.TimeZone;      
      
      
public class TimeUtils {      
      
    /**   
     * ��ȡ��ǰʱ��   
     * @return   
     */      
    public static String getNowTime(){      
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd");      
        Date date = new Date(System.currentTimeMillis());      
        return simpleDateFormat.format(date);      
    }      
    /**   
     * ��ȡʱ���   
     *   
     * @return ��ȡʱ���   
     */      
    public static String getTimeString() {      
        SimpleDateFormat df = new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss");      
        Calendar calendar = Calendar.getInstance();      
        return df.format(calendar.getTime());      
    }      
    /**   
     * ʱ��ת��Ϊʱ���   
     * @param time:��Ҫת����ʱ��   
     * @return   
     */      
    public static String dateToStamp(String time)  {      
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      
        Date date = null;      
        try {      
            date = simpleDateFormat.parse(time);      
        } catch (ParseException e) {      
            e.printStackTrace();      
        }      
        long ts = date.getTime();      
        return String.valueOf(ts);      
    }      
      
    /**   
     * ʱ���ת��Ϊ�ַ���   
     * @param time:ʱ���   
     * @return   
     */      
    public static String times(String time) {  
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��");  
        @SuppressWarnings("unused")  
        long lcc = Long.valueOf(time);  
        int i = Integer.parseInt(time);  
        String times = sdr.format(new Date(i * 1000L));  
        return times;  
  
    }     
    /**   
     *��ȡ������ĳһСʱ��ʱ��   
     * @param hour hour=-1Ϊ��һ��Сʱ��hour=1Ϊ��һ��Сʱ   
     * @return   
     */      
    public static String getLongTime(int hour){      
        Calendar c = Calendar.getInstance(); // ��ʱ�����ں�ʱ��      
        int h; // ��Ҫ���ĵ�Сʱ      
        h = c.get(Calendar.HOUR_OF_DAY) - hour;      
        c.set(Calendar.HOUR_OF_DAY, h);      
        SimpleDateFormat df = new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss");      
        Log.v("time",df.format(c.getTime()));      
        return df.format(c.getTime());      
    }    
    /**  
     * �Ƚ�ʱ���С  
     * @param str1��Ҫ�Ƚϵ�ʱ��  
     * @param str2��Ҫ�Ƚϵ�ʱ��  
     * @return  
     */    
    public static boolean isDateOneBigger(String str1, String str2) {    
        boolean isBigger = false;    
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss");    
        Date dt1 = null;    
        Date dt2 = null;    
        try {    
            dt1 = sdf.parse(str1);    
            dt2 = sdf.parse(str2);    
        } catch (ParseException e) {    
            e.printStackTrace();    
        }    
        if (dt1.getTime() > dt2.getTime()) {    
            isBigger = true;    
        } else if (dt1.getTime() < dt2.getTime()) {    
            isBigger = false;    
        }    
        return isBigger;    
    }    
     
  /** 
  * ����ʱ�� ---> UTCʱ�� 
  * @return 
  */  
 public static String Local2UTC(){  
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
     sdf.setTimeZone(TimeZone.getTimeZone("gmt"));  
     String gmtTime = sdf.format(new Date());  
     return gmtTime;  
 }  
  
  /** 
  * UTCʱ�� ---> ����ʱ�� 
  * @param utcTime   UTCʱ�� 
  * @return 
  */  
 public static String utc2Local(String utcTime) {  
     SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//UTCʱ���ʽ  
     utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));  
     Date gpsUTCDate = null;  
     try {  
         gpsUTCDate = utcFormater.parse(utcTime);  
     } catch (ParseException e) {  
         e.printStackTrace();  
     }  
     SimpleDateFormat localFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//����ʱ���ʽ  
     localFormater.setTimeZone(TimeZone.getDefault());  
     String localTime = localFormater.format(gpsUTCDate.getTime());  
     return localTime;  
 } 
}