package com.youdao.nanti.candy.yirisanxing.alarm;

import android.provider.BaseColumns;

public class QuestionColumns implements BaseColumns {
    // This class cannot be instantiated
    private QuestionColumns() {}
    
    public static final String TABLE_NAME = "questions";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "modified DESC";
    
    
    // enums
    public static final int REPEAT_TYPE_DAY = 1;
    public static final int REPEAT_TYPE_WEEK = 2;
    
    public static final int ALERT_TYPE_NOTIFICATION = 1;
    public static final int ALERT_TYPE_ALARMCLOCK = 2;

}
