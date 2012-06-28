package com.youdao.nanti.candy.yirisanxing.alarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BeepService extends Service {
	
	private static final String TAG = "BeepService";
	
    private boolean mPlaying = false;
        
    private Vibrator mVibrator;
    private TelephonyManager mTelephonyManager;
    //private PowerManager.WakeLock mWakeLock;
    SharedPreferences preferences;
    private MediaPlayer mMediaPlayer;
    
    private static final long[] sVibratePattern = new long[] {500, 1500};
    private int timeout;
    private int ringtone;

    
    // when time out, stop beep
    // Internal messages
    private static final int KILLER = 1000;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KILLER:
                    stopSelf();
                    break;
            }
        }
    };

    // when in call, stop beep
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String ignored) {
        	if (state != TelephonyManager.CALL_STATE_IDLE) {
        		stopSelf();
        	}
        }
    };

    @Override
    public void onCreate() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		// do not acquire wake lock, in case of misoperation
		/*
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "wake lock");
        mWakeLock.acquire();
        */
        preferences = getSharedPreferences(Settings.PATH, 0);
        timeout = preferences.getInt(Settings.BEEP_TIMEOUT, Settings.BEEP_TIMEOUT_DEFAULT);
        ringtone = preferences.getInt(Settings.RINGTONE, Settings.RINGTONE_DEFAULT);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// KEEP
    	// No intent, tell the system not to restart us.
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        
        if (!mPlaying) {
            play();
        }
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
    private void play() {
        // stop() checks to see if we are already playing.
        stop();
            
        //TODO: uri in preferences ?
        Uri alert = RingtoneManager.getDefaultUri(ringtone);

        try {
            //´´½¨media player
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                        mMediaPlayer.setLooping(true);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
              }
        } catch (Exception ex) {
        	//TODO : exception
        	Log.e(TAG, "play ringtone failed");
        	Toast.makeText(this, "play ringtone failed", Toast.LENGTH_LONG).show();
        }
        
            
        /* Start the vibrator after everything is ok with the media player */
        mVibrator.vibrate(sVibratePattern, 0);

        mPlaying = true;
        //mStartTime = System.currentTimeMillis();
        enableKiller();
    }
    
    
    public void stop() {
        if (mPlaying) {
            mPlaying = false;

            // Stop audio playing
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            // Stop vibrator
            mVibrator.cancel();
        }
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    @Override
    public void onDestroy() {
        stop();
        // Stop listening for callstate.
        mTelephonyManager.listen(mPhoneStateListener, 0);
        //mWakeLock.release();
    }
    
    private void enableKiller() {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(KILLER), 1000 * timeout);
    }

}
