package com.youdao.nanti.candy.yirisanxing.alarm;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

public class Alarm {
	
    public static final String[] columns = new String[] {
		"_id",
		"question", 
		"is_enabled", 
    	"repeat_type", 
    	"interval", 
    	"days_of_week", 
    	"hour", 
    	"minute", 
    	"alert_type",
	};
    
	public static final String ALERT_TIME = "com.youdaonanti.candy.choicediary.key.alert_time";
	
    // Public fields
    public long id;
    public String question;
    public int enabled;
    public int repeat_type;
    public int interval;
    public int days_of_week;
    public int hour;
    public int minute;
    public int alert_type;
    	
	public Alarm(Cursor cursor) {
		//cursor.moveToFirst();
		
		id = cursor.getLong(0);
		question = cursor.getString(1);
		enabled = cursor.getInt(2);
	    repeat_type = cursor.getInt(3);
	    interval = cursor.getInt(4);
	    days_of_week = cursor.getInt(5);
	    hour = cursor.getInt(6);
	    minute = cursor.getInt(7);
	    alert_type = cursor.getInt(8);

	}
	
	public void testAlert(Context context) {
		long time = System.currentTimeMillis() + 3000;
		
        Intent intent = new Intent(Action.ALERT, Uri.parse("alarm:" + String.valueOf(id)));
        //Intent intent = new Intent(Action.DELAY_ALERT, Uri.parse("alarm:" + String.valueOf(id)));
        intent.putExtra(ALERT_TIME, time);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, sender);
	}

	public void alert(Context context) {
		if (enabled == 0) { return; }

		long time = computeNextTime(System.currentTimeMillis());
		
        Intent intent = new Intent(Action.ALERT, Uri.parse("alarm:" + String.valueOf(id)));
        intent.putExtra(ALERT_TIME, time);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, sender);
	}
	
	public void delay(Context context, long time) {
		if (enabled == 0) { return; }
		if (alert_type != 2) { return; }
		
		SharedPreferences preferences = context.getSharedPreferences(Settings.PATH, 0);
		long delayInterval = preferences.getLong(Settings.DELAY_INTERVAL, Settings.DELAY_INTERVAL_DEFAULT);

		long delayTime = System.currentTimeMillis() + delayInterval;
		
        Intent intent = new Intent(Action.DELAY, Uri.parse("alarm:" + String.valueOf(id)));
        intent.putExtra(ALERT_TIME, time);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, delayTime, sender);
	}
	
	// accept a input so that this can be used when filling missed reviews
	public long computeNextTime(long time) {
	    Date now = new Date(time);
	    Date set = new Date(now.getYear(), now.getMonth(), now.getDate(), hour, minute);
	    long nextTime;
	    if (now.before(set)) {
	    	nextTime = set.getTime();
	    } else {
	    	nextTime = nextSetTime(set.getTime());
	    }
	    return nextTime;
	}

	protected long nextSetTime(long settime) {
		
		Calendar c =  Calendar.getInstance();
		c.setTimeInMillis(settime);
		switch (repeat_type) {
			case 1:
				return settime + interval * 24 * 60 * 60 * 1000;
				//return time + interval * 1000;
			case 2:
				DaysOfWeek dow = new DaysOfWeek(days_of_week);
				int days = dow.getNextAlarm(c);
				return settime + days * 24 * 60 * 60 * 1000;
		}
		return settime;
	}
		
	public void saveAlarm() {
		
	}
	
	
	// This class is from the DeskClock
    public static final class DaysOfWeek {
        /*
         * Days of week code as a single int.
         * 0x00: no day
         * 0x01: Monday
         * 0x02: Tuesday
         * 0x04: Wednesday
         * 0x08: Thursday
         * 0x10: Friday
         * 0x20: Saturday
         * 0x40: Sunday
         */

        // Bitmask of all repeating days
        private int mDays;

        DaysOfWeek(int days) {
            mDays = days;
        }

        private boolean isSet(int day) {
            return ((mDays & (1 << day)) > 0);
        }

        public void set(int day, boolean set) {
            if (set) {
                mDays |= (1 << day);
            } else {
                mDays &= ~(1 << day);
            }
        }

        public void set(DaysOfWeek dow) {
            mDays = dow.mDays;
        }

        public int getCoded() {
            return mDays;
        }

        // Returns days of week encoded in an array of booleans.
        public boolean[] getBooleanArray() {
            boolean[] ret = new boolean[7];
            for (int i = 0; i < 7; i++) {
                ret[i] = isSet(i);
            }
            return ret;
        }

        public boolean isRepeatSet() {
            return mDays != 0;
        }

        /**
         * returns number of days from today until next alarm
         * @param c must be set to today
         */
        public int getNextAlarm(Calendar c) {
            if (mDays == 0) {
                return -1;
            }

            int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;

            int day = 0;
            int dayCount = 0;
            for (; dayCount < 7; dayCount++) {
                day = (today + dayCount) % 7;
                if (isSet(day)) {
                    break;
                }
            }
            return dayCount;
        }
    }
	
}
