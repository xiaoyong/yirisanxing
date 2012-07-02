package com.youdao.nanti.candy.yirisanxing;

import java.util.ArrayList;
import java.util.List;

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
        UPDATE,
        DELETE
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
        dbHelper.close();
    }

    /** Question related JavaScript interfaces */
    public int processQuestion(String questionString, String actionString) {
        Question question = gs.fromJson(questionString, Question.class);
        //System.out.println(gs.toJson(question));
        Actions action = Actions.valueOf(actionString.toUpperCase());
        System.out.println("action: " + action);
        switch (action) {
        case CREATE:
            long questionId = createQuestion(question);
            for (Option option : question.getOptions()) {
                option.setQuestionId(questionId);
                //System.out.println("option: " + gs.toJson(option));
                processOption(option);
            }
            break;
        case UPDATE:
            updateQuestion(question);
            for (Option option : question.getOptions()) {
                processOption(option);
            }
            break;
        case DELETE:
            deleteQuestion(question.getId());
            break;
        default:
            break;
        }
        return 0;
    }
    
    public String getAllQuestions() {
        List<Question> questions = new ArrayList<Question>();

        Cursor cursor = database.query("questions", null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Question question = cursorToQuestion(cursor);
            question.setOptions(getAllOptions(question.getId()));
            questions.add(question);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return gs.toJson(questions);
    }
    
    private List<Option> getAllOptions(long questionId) {
        List<Option> options = new ArrayList<Option>();

        Cursor cursor = database.query("options", null, "question_id = " + questionId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Option option = cursorToOption(cursor);
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
            Review review = cursorToReview(cursor);
            reviews.add(review);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return reviews;
    }
    
    /** Review related JavaScript interfaces */
    public int processReview(String reviewString, String actionString) {
        Review review = gs.fromJson(reviewString, Review.class);
        Actions action = Actions.valueOf(actionString.toUpperCase());
        
        switch (action) {
        case CREATE:
            createReview(review);
            break;
        case UPDATE:
            updateReview(review);
            break;
        case DELETE:
            deleteReview(review.getId());
            break;
        default:
            break;
        }
        return 0;
    }
    
    private void processOption(Option option) {
        if (option.getId() < 0) {
            //System.out.println("Creating new option...");
            createOption(option);
        } else if (option.getId() > 0) {
            updateOption(option);
        }
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
    private Question cursorToQuestion(Cursor cursor) {
        Question question = new Question();
	
        question.setId(cursor.getLong(0));
        question.setQuestion(cursor.getString(1));
        question.setIsEnabled((int) cursor.getLong(2));
        question.setRepeatType((int) cursor.getLong(3));
        question.setInterval((int) cursor.getLong(4));
        question.setDaysOfWeek((int) cursor.getLong(5));
        question.setAlertType((int) cursor.getLong(6));
        question.setHour((int) cursor.getLong(7));
        question.setMinute((int) cursor.getLong(8));
        question.setCreated((int) cursor.getLong(9));
        question.setUpdated((int) cursor.getLong(10));

        return question;
    }
    
    private Option cursorToOption(Cursor cursor) {
        Option option = new Option();
    
        option.setId(cursor.getLong(0));
        option.setQuestionId(cursor.getLong(1));
        option.setOption(cursor.getString(2));
        option.setValue((int) cursor.getLong(3));

        return option;
    }
    
    private Review cursorToReview(Cursor cursor) {
        Review review = new Review();
    
        review.setId(cursor.getLong(0));
        review.setQuestionId(cursor.getLong(1));
        review.setOptionId(cursor.getLong(2));
        review.setReviewed((int) cursor.getLong(3));
        review.setCreated((int) cursor.getLong(4));
        review.setIsReviewed((int) cursor.getLong(5));

        return review;
    }
    
    private ContentValues questionToContentValues(Question question) {
        ContentValues values = new ContentValues();
	
        if (question.getId() > 0) {
            values.put("_id", question.getId());
        }
        values.put("question", question.getQuestion());
        values.put("is_enabled", question.getIsEnabled());
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
        values.put("createdd", review.getCreated());
        values.put("is_reviewed", review.getIsReviewed());
        
        return values;
    }
    
    
    // alarm related
	public void setAlarm(long questionId) {
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
