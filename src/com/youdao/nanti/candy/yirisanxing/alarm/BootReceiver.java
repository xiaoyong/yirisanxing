package com.youdao.nanti.candy.yirisanxing.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public final static String TAG = "BootReceiver";

    /**
     * Sets alarm on ACTION_BOOT_COMPLETED.  Resets alarm on
     * TIME_SET, TIMEZONE_CHANGED
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Boot received", Toast.LENGTH_LONG).show();
        Log.v(TAG, "Boot received");
                 
    	Scheduler.setAlarms(context);
    }
}
