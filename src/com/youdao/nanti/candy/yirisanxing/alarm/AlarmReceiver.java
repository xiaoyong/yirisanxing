package com.youdao.nanti.candy.yirisanxing.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.youdao.nanti.candy.yirisanxing.DatabaseHelper;
import com.youdao.nanti.candy.yirisanxing.R;

public class AlarmReceiver extends BroadcastReceiver {
    
    private static final String TAG = "AlarmReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        Log.v(TAG, "received alarm broadcast");
        
        long questionId = Long.valueOf(intent.getData().getSchemeSpecificPart());
        long alertTime = intent.getLongExtra(Alarm.ALERT_TIME, System.currentTimeMillis());
        
        if (Action.ALERT.equals(intent.getAction())) {
            Log.v(TAG, "action ALERT");

            // next alarm
            // TODO: check whether the time is right
            DatabaseHelper mDbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String selection = "_id=" + String.valueOf(questionId);
            Cursor cursor = db.query("questions", Alarm.columns, selection, null, null, null, null, null);
            cursor.moveToFirst();
            Alarm alarm = new Alarm(cursor); 
            alarm.alert(context);
            
            Log.v(TAG, String.valueOf(alarm.repeat_type));
            
            // notification or alarmclock
            if (alarm.alert_type == QuestionColumns.ALERT_TYPE_NOTIFICATION) {
                Log.v(TAG, "notification alert");
                updateNotification(context, alarm, alertTime);
            } else if (alarm.alert_type == QuestionColumns.ALERT_TYPE_ALARMCLOCK) {
                Log.v(TAG, "alarmclock alert");
                popAlert(context, questionId, alertTime);
            } else {
                Log.e(TAG, "unspecified alert type");
            }
        } else if (Action.DELAY.equals(intent.getAction())) {
            Log.v(TAG, "action DELAY");
            // TODO
            popAlert(context, questionId, alertTime);
        }
    }
        
    protected void popAlert(Context context, long questionId, long time) {

        // KEEP
        // Close dialogs and window shade
        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialogs);

        // popup activity
        Intent alertIntent = new Intent(Action.REVIEW, Uri.parse("question:" + String.valueOf(questionId)));
        alertIntent.putExtra(Alarm.ALERT_TIME, time);
        //TODO: should be the question uri
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(alertIntent);

        /*
        // start beep service
        Intent playAlarm = new Intent(Action.BEEP);
        context.startService(playAlarm);
        */
        
    }
    
    protected void updateNotification(Context context, Alarm alarm, long time) {
        long questionId = alarm.id;
        String question = alarm.question;
        
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notify = new Intent(Action.REVIEW, Uri.parse("question:" + String.valueOf(questionId)));
        PendingIntent pendingNotify = PendingIntent.getActivity(context, 0, notify, 0);

        String title = context.getResources().getString(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_launcher, "", System.currentTimeMillis());
        notification.setLatestEventInfo(context, title, question, pendingNotify);
        notification.flags |= Notification.FLAG_SHOW_LIGHTS
                | Notification.FLAG_ONGOING_EVENT
                | Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        // TODO: custom sounds ?
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                
        nm.notify((int) questionId, notification);
    }
}
