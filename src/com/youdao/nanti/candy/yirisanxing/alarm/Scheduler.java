package com.youdao.nanti.candy.yirisanxing.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.youdao.nanti.candy.yirisanxing.DatabaseHelper;

public class Scheduler {
    
    public final static String TAG = "Scheduler";
    
    // this will also set the alarms
    public static void fillMissedReviews(Context context) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        Cursor cursor = db.query(QuestionColumns.TABLE_NAME, Alarm.columns, "is_enabled=1", null, null, null, null);
        
        long now = System.currentTimeMillis();
        
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(0);
            Alarm a = new Alarm(cursor);

            Cursor c = db.query("reviews", new String[] {"created"}, "question_id=" + String.valueOf(id), null, null, null, "created DESC");
            c.moveToFirst();
            long recent = a.computeNextTime(c.getLong(0));
            c.close();
            
            while (recent < now) {        
                ContentValues values = new ContentValues();
                values.put("question_id", id);
                values.put("created", recent);

                db.insert("reviews", null, values);
                
                recent = a.computeNextTime(recent);
            }
            a.alert(context);
        }
        
        cursor.close();
        db.close();
    }
    
    // set alarms without check missed reviews
    public static void setAlarms(Context context) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        Cursor cursor = db.query(QuestionColumns.TABLE_NAME, Alarm.columns, "is_enabled=1", null, null, null, null);
        
        Log.v(TAG, "set total " + String.valueOf(cursor.getCount()) + " alarms");
        //Toast.makeText(context, "set total " + String.valueOf(cursor.getCount()) + " alarms", Toast.LENGTH_LONG).show();
        //Toast.makeText(context, context.getPackageName(), Toast.LENGTH_LONG).show();
        
        //cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //Toast.makeText(context, "hour:" + String.valueOf(cursor.getInt(6)) + " minute:" + String.valueOf(cursor.getInt(7)) , Toast.LENGTH_LONG).show();
            Alarm a = new Alarm(cursor);
            a.alert(context);
        }
        
        cursor.close();
        mDbHelper.close();

    }
    
    /* This method is in alarmreceiver now
    public static void updateNotification(Context context) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        
        Cursor cursor = db.query("reviews", new String[] {"_id"}, "reviewed IS NULL", null, null, null, null);
        int number = cursor.getCount();
        
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (number > 0) {
            Intent notify = new Intent(Action.REVIEW);
            PendingIntent pendingNotify = PendingIntent.getActivity(context, 0, notify, 0);

            String title = context.getResources().getString(R.string.app_name);
            Notification n = new Notification(R.drawable.ic_launcher, "", System.currentTimeMillis());
            n.setLatestEventInfo(context, title, "���� " + String.valueOf(number) + " ��������Ҫ�ش�", pendingNotify);
            n.flags |= Notification.FLAG_SHOW_LIGHTS
                    | Notification.FLAG_ONGOING_EVENT
                    | Notification.FLAG_AUTO_CANCEL;
            n.defaults |= Notification.DEFAULT_LIGHTS;
            n.number = number;
            nm.notify(0, n);
        } else {
            nm.cancel(0);
        }
    }
    */
}
