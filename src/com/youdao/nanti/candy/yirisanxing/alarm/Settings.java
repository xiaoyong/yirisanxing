package com.youdao.nanti.candy.yirisanxing.alarm;

import android.media.RingtoneManager;

public class Settings {
    
    public static final String PATH = "preferences";
    
    public static final String BEEP_TIMEOUT = "com.youdao.nanti.candy.yirisanxing.key.TIMEOUT_SECONDS";
    public static final int BEEP_TIMEOUT_DEFAULT = 120;
    
    public static final String RINGTONE = "com.youdao.nanti.candy.yirisanxing.key.RINGTONE";
    public static final int RINGTONE_DEFAULT = RingtoneManager.TYPE_ALARM;

    public static final String DELAY_INTERVAL = "com.youdao.nanti.candy.yirisanxing.key.DELAY_INTERVAL";
    public static final long DELAY_INTERVAL_DEFAULT = 15 * 60;
    
    public void export() {
        
    }
}
