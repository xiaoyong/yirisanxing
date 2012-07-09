package com.youdao.nanti.candy.yirisanxing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.youdao.nanti.candy.yirisanxing.alarm.Action;
import com.youdao.nanti.candy.yirisanxing.alarm.Alarm;

/** Database access interface for JavaScript */
public class JavaScriptInterface {
    
    Context mContext;
    
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Gson gs = new Gson();
    
    private enum Actions {
        CREATE,
        GETALLLIST,
        GETITEMBYID,
        DELITEMBYID,
        VIEWREVIEWLISTBYID,
        INSERTREVIEW
    }
    
    /** Instantiate the interface and set the context */
    public JavaScriptInterface(Context c) {
        mContext = c;
        dbHelper = new DatabaseHelper(c);
    }

    /** Show a toast from the web page */
    /**
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
    */
    
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys = ON;");
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /** Question related JavaScript interfaces */
    public String processQuestion(String questionString, String actionString) {
        Question question;
        Actions action = Actions.valueOf(actionString.toUpperCase());
        int arows = 0;
        System.out.println("action: " + action);
        
        switch (action) {
        case CREATE:
            question = gs.fromJson(questionString, Question.class);
            //System.out.println(gs.toJson(question));
            long inputId = question.getId();
            long questionId = -1;
            if (inputId < 0) { // Create question
                questionId = createQuestion(question);
            } else if (inputId > 0) { // Update question
                arows = updateQuestion(question);
            }
            for (Option option : question.getOptions()) {
                option.setQuestionId(questionId);
                //System.out.println("option: " + gs.toJson(option));
                processOption(option);
            }
            if ((inputId < 0 && questionId > 0) || (inputId > 0 && arows > 0)) {
                return "0";
            } else {
                return "-1";
            }
        case GETALLLIST:
            return gs.toJson(getAllQuestions());
        case GETITEMBYID:
            return gs.toJson(getQuestionByID(Long.parseLong(questionString)));
        case DELITEMBYID:
            arows = deleteQuestion(Long.parseLong(questionString));
            if (arows > 0) {
                return "0";
            } else {
                return "-1";
            }
        case VIEWREVIEWLISTBYID:
            return gs.toJson(getAllReviews(Long.parseLong(questionString)));
        case INSERTREVIEW:
            Review review = gs.fromJson(questionString, Review.class);
            long reviewId = createReview(review);
            if (reviewId > 0)
                return "0";
            else {
                return "-1";
            }
        default:
            break;
        }
        
        return "0";
        
    }
    private Question getQuestionByID(long questionId) {
        Cursor cursor = database.query("questions", null, "_id = " + questionId, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        
        Question question = new Question(cursor);
        question.setOptions(getAllOptions(question.getId()));
        
        // Make sure to close the cursor
        cursor.close();
        return question;
    }
    
    private List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<Question>();

        Cursor cursor = database.query("questions", null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Question question = new Question(cursor);
            question.setOptions(getAllOptions(question.getId()));
            questions.add(question);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return questions;
    }
    
    private List<Option> getAllOptions(long questionId) {
        List<Option> options = new ArrayList<Option>();

        Cursor cursor = database.query("options", null, "question_id = " + questionId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Option option = new Option(cursor);
            options.add(option);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return options;
    }
    
    private List<Review> getAllReviews(long questionId) {
        List<Review> reviews = new ArrayList<Review>();

        Cursor cursor = database.query("reviews", null, "question_id = " + questionId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = new Review(cursor);
            reviews.add(review);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return reviews;
    }
    
    private long processOption(Option option) {
        if (option.getId() < 0) {
            //System.out.println("Creating new option...");
            return createOption(option);
        } else if (option.getId() > 0) {
            return updateOption(option);
        }
        return -1;
    }
    
    /** CRUD operations */
    private long createQuestion(Question question) {
        ContentValues values = questionToContentValues(question);
        long questionId = database.insert("questions", null, values);

        setAlarm(questionId);
        return questionId;
        
    }
    private long createOption(Option option) {
        ContentValues values = optionToContentValues(option);
        //System.out.println("option: " + gs.toJson(values));

        return database.insert("options", null, values);
    }
    private long createReview(Review review) {
        ContentValues values = reviewToContentValues(review);
        //mute();
        return database.insert("reviews", null, values);
    }
    private int updateQuestion(Question question) {
        return database.update("questions", questionToContentValues(question), "_id = " + question.getId(), null);
    }
    private int updateOption(Option option) {
        return database.update("options", optionToContentValues(option), "_id = " + option.getId(), null);
    }
    private int updateReview(Review review) {
        return database.update("reviews", reviewToContentValues(review), "_id = " + review.getId(), null);
    }
    private int deleteQuestion(long id) {
        return database.delete("questions", "_id = " + id, null);
    }
    private int deleteOption(long id) {
        return database.delete("options", "_id = " + id, null);
    }
    private int deleteReview(long id) {
        return database.delete("reviews", "_id = " + id, null);
    }
    
    /** Object converting helpers */
    private ContentValues questionToContentValues(Question question) {
        ContentValues values = new ContentValues();
    
        if (question.getId() > 0) {
            values.put("_id", question.getId());
        }
        values.put("question", question.getQuestion());
        values.put("is_enabled", question.getIsEnabled() ? 1 : 0);
        values.put("repeat_type", question.getRepeatType());
        values.put("interval", question.getInterval());
        values.put("days_of_week", question.getDaysOfWeek());
        values.put("alert_type", question.getAlertType());
        values.put("hour", question.getHour());
        values.put("minute", question.getMinute());
        values.put("created", question.getCreated());
        values.put("updated", question.getUpdated());
        
        return values;
    }
    
    private ContentValues optionToContentValues(Option option) {
        ContentValues values = new ContentValues();

        if (option.getId() > 0) {
            values.put("_id", option.getId());
        }
        values.put("question_id", option.getQuestionId());
        values.put("option", option.getOption());
        values.put("value", option.getValue());
        
        return values;
    }
    
    private ContentValues reviewToContentValues(Review review) {
        ContentValues values = new ContentValues();

        if (review.getId() > 0 ) {
            values.put("_id", review.getId());
        }
        values.put("question_id", review.getQuestionId());
        values.put("option_id", review.getOptionId());
        values.put("reviewed", review.getReviewed());
        values.put("created", review.getCreated());
        values.put("comment", review.getComment());
        
        return values;
    }
    
    
    // alarm related
    public void setAlarm(long questionId) {
        // TODO: test or release
        Cursor cursor = database.query("questions", Alarm.columns, "_id=" + String.valueOf(questionId), null, null, null, null);
        cursor.moveToFirst();
        Alarm alarm = new Alarm(cursor);
        //alarm.alert(mContext);
        alarm.testAlert(mContext);
        cursor.close();
    }
    
    public void delay(long questionId, long time) {
        Cursor cursor = database.query("questions", Alarm.columns, "_id=" + String.valueOf(questionId), null, null, null, null);
        cursor.moveToFirst();
        Alarm alarm = new Alarm(cursor);
        alarm.delay(mContext, time);
        cursor.close();
    }
    
    public void mute() {
        mContext.stopService(new Intent(Action.BEEP));
    }
        
}
