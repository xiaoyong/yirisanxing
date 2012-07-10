package com.youdao.nanti.candy.yirisanxing;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yirisanxing.db";
    private static final int DATABASE_VERSION = 3;
    private static final String QUESTIONS_TABLE_CREATE = "CREATE TABLE questions ("
            + "_id INTEGER PRIMARY KEY, "
            + "question TEXT, "
            + "is_enabled INTEGER, "
            + "repeat_type INTEGER, "
            + "interval INTEGER, "
            + "days_of_week TEXT, "
            + "alert_type INTEGER, "
            + "hour INTEGER, "
            + "minute INTEGER, "
            + "created INTEGER, "
            + "updated INTEGER"
            + ");";
    private static final String OPTIONS_TABLE_CREATE = "CREATE TABLE options ("
            + "_id INTEGER PRIMARY KEY, "
            + "question_id INTEGER REFERENCES questions(_id) ON DELETE CASCADE, "
            + "option TEXT, "
            + "value INTEGER, "
            + "is_enabled INTEGER, "
            + "UNIQUE (question_id, option)"
            + ");";
    private static final String REVIEWS_TABLE_CREATE = "CREATE TABLE reviews ("
            + "_id INTEGER PRIMARY KEY, "
            + "question_id INTEGER REFERENCES questions(_id) ON DELETE CASCADE, "
            + "option_id INTEGER REFERENCES options(_id), "
            + "reviewed INTEGER, "
            + "created INTEGER, "
            + "comment TEXT"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUESTIONS_TABLE_CREATE);
        db.execSQL(OPTIONS_TABLE_CREATE);
        db.execSQL(REVIEWS_TABLE_CREATE);
        
        // Add a demo question
        ContentValues values = new ContentValues();
        
        values.put("question", "今天按时吃早饭了吗？");
        values.put("is_enabled", 0);
        values.put("repeat_type", 2);
        values.put("interval", 1);
        values.put("days_of_week", "1,2,3,4,5,6,7");
        values.put("alert_type", 2);
        values.put("hour", 22);
        values.put("minute", 0);
        long t = System.currentTimeMillis();
        values.put("created", t);
        values.put("updated", t);
        long questionId = db.insert("questions", null, values);
        
        // Add demo options
        values.clear();
        values.put("question_id", questionId);
        values.put("option", "有");
        values.put("value", 1);
        values.put("is_enabled", 1);
        db.insert("options", null, values);
        
        values.clear();
        values.put("question_id", questionId);
        values.put("option", "没有");
        values.put("value", 0);
        values.put("is_enabled", 1);
        db.insert("options", null, values);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS questions");
        db.execSQL("DROP TABLE IF EXISTS options");
        db.execSQL("DROP TABLE IF EXISTS reviews");
        
        // Create tables again
        onCreate(db);
    }
}
