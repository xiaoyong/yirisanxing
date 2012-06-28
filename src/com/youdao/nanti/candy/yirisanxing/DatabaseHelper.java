package com.youdao.nanti.candy.yirisanxing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yirisanxing.db";
    private static final int DATABASE_VERSION = 2;
    private static final String QUESTIONS_TABLE_CREATE = "CREATE TABLE questions ("
            + "_id INTEGER PRIMARY KEY, "
            + "question TEXT, "
            + "is_enabled INTEGER, "
            + "repeat_type INTEGER, "
            + "interval INTEGER, "
            + "days_of_week INTEGER, "
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
            + "UNIQUE (question_id, option)"
            + ");";
    private static final String REVIEWS_TABLE_CREATE = "CREATE TABLE reviews ("
            + "_id INTEGER PRIMARY KEY, "
            + "question_id INTEGER REFERENCES questions(_id) ON DELETE CASCADE, "
            + "option_id INTEGER REFERENCES options(_id), "
            + "reviewd INTEGER, "
            + "created INTEGER, "
            + "is_reviewed INTEGER"
            + ");";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUESTIONS_TABLE_CREATE);
        db.execSQL(OPTIONS_TABLE_CREATE);
        db.execSQL(REVIEWS_TABLE_CREATE);

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
