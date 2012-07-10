package com.youdao.nanti.candy.yirisanxing;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import ua.com.vassiliev.androidfilebrowser.FileBrowserActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.youdao.nanti.candy.yirisanxing.alarm.Action;
import com.youdao.nanti.candy.yirisanxing.alarm.Alarm;

public class YiRiSanXingActivity extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    
    private WebView myWebView;
    private JavaScriptInterface jsInterface;
    
    private WebView bPanel; //bottom panel
    private Handler handler = new Handler();
    
    private String mAction;
    
    private Queue<ReviewHint> reviewQueue = new LinkedList<ReviewHint>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myWebView = (WebView) findViewById(R.id.webview);
        
        Intent intent = getIntent();
        mAction = intent.getAction();
        if (mAction.equals("android.intent.action.MAIN")) {
            loadMain();
        } else if (mAction.equals(Action.REVIEW)) {
            long id = Long.valueOf(intent.getData().getSchemeSpecificPart());
            long time = intent.getLongExtra(Alarm.ALERT_TIME, System.currentTimeMillis());
            loadReview(id, time);
        }
        
    }
    
    private void loadMain() {
        // Enable JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        //hidden scroll bar.
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.setVerticalScrollBarEnabled(false);
        
        //add key board.
        myWebView.requestFocusFromTouch();
        
             
        //enable javaScript alert.
        final Context myApp = this;
        myWebView.setWebChromeClient(new WebChromeClient(){
            
            // Handle javaScript alert in webview.
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                    final android.webkit.JsResult result) {
                new AlertDialog.Builder(myApp)
                    .setTitle("JavaScript Dialog")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, 
                            new AlertDialog.OnClickListener(){
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                result.confirm();
                            }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
                
                return true;
            }
            
            /*
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Toast.makeText(getBaseContext(), "openfile", Toast.LENGTH_LONG).show();
            }
            */
            
        });
       
        jsInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(jsInterface, "Android");
        myWebView.addJavascriptInterface(this, "Activity");
        
        // Load a web page
//        myWebView.loadUrl("file:///android_asset/index.html?id=2");
        //myWebView.loadUrl("file:///android_asset/test_xiaoyong.html");
        myWebView.loadUrl("file:///android_asset/historyList.html");
        //myWebView.loadUrl("file:///android_asset/charting.html?id=1");
        
        //////-----------For bottom panel-----------------------------/////////
        
        bPanel = (WebView) findViewById(R.id.bPanel);
        bPanel.loadUrl("file:///android_asset/bottomPanel.html");
        WebSettings bPanelSettings = bPanel.getSettings();
        bPanelSettings.setJavaScriptEnabled(true);
        //hidden scroll bar.
        bPanel.setHorizontalScrollBarEnabled(false);
        bPanel.setVerticalScrollBarEnabled(false);
        
        //bind top panel communication interface.
        bPanel.addJavascriptInterface(new TopPanelCommunicationInterface(handler, myWebView), "TopInterface");
        
        //////-----------bottom panel END-----------------------------/////////
    }
    
    private void loadReview(long id, long time) {
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Enable JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        //hidden scroll bar.
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.setVerticalScrollBarEnabled(true);
        
        //add key board.
        myWebView.requestFocusFromTouch();
        
        jsInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(jsInterface, "Android");
        myWebView.addJavascriptInterface(this, "Activity");

        String sId = String.valueOf(id);
        String sTime = String.valueOf(time);
        myWebView.loadUrl("file:///android_asset/activeItem.html?id=" + sId + "&time=" + sTime);
        //myWebView.loadUrl("file:///android_asset/activeItem.html");
        
        bPanel = (WebView) findViewById(R.id.bPanel);
        bPanel.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        jsInterface.open();
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        jsInterface.close();
        super.onPause();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(Action.REVIEW)) {
            long id = Long.valueOf(intent.getData().getSchemeSpecificPart());
            long time = intent.getLongExtra(Alarm.ALERT_TIME, System.currentTimeMillis());
            if (mAction.equals(Action.MAIN)) {
                loadReview(id, time);
            } else if (mAction.equals(Action.REVIEW)) {
                //jsInterface.queueReview()
                queueReview(id, time);
            }
        }
        mAction = intent.getAction();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && !myWebView.canGoBack()) {
            confirmExit();
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            myWebView.loadUrl("file:///android_asset/searchPanel.html");
        }
        
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private void confirmExit() {
        // lock back when review from alarm clock
        if (mAction.equals(Action.REVIEW)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_message)
               .setCancelable(false)
               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       finish();
                   }
               })
               .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {        
                   public void onClick(DialogInterface dialog, int id) {              
                       dialog.cancel();   
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    // queue related
    public class ReviewHint {
        public long id;
        public long time;
        public ReviewHint(long id, long time) {
            this.id = id;
            this.time = time;
        }        
    }
    
    // called at activity
    private void queueReview(long id, long time) {
        reviewQueue.add(new ReviewHint(id, time));
    }
    
    // called at webview
    public void nextReview() {
        if (!reviewQueue.isEmpty()) {
            ReviewHint reviewHint = reviewQueue.remove();
            String sId = String.valueOf(reviewHint.id);
            String sTime = String.valueOf(reviewHint.time);
            myWebView.loadUrl("file:///android_asset/activeItem.html?id=" + sId + "&time=" + sTime);
        } else {
            // TODO: gets weird exit when you review mannually
            finish();
        }
    }
    
    // import/export related
    // TODO: should be in webChromeClient??
    private final int REQUEST_CODE_PICK_DIR = 1;
    private final int REQUEST_CODE_PICK_FILE = 2;

    
    public void chooseFolder() {
        Intent fileExploreIntent = new Intent(
                FileBrowserActivity.INTENT_ACTION_SELECT_DIR,
                null,
                this,
                FileBrowserActivity.class
                );
        startActivityForResult(
                fileExploreIntent,
                REQUEST_CODE_PICK_DIR
                );
    }
    
    public void chooseFile() {
        Intent fileExploreIntent = new Intent(
                ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                null,
                this,
                ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.class
                );
        startActivityForResult(
                fileExploreIntent,
                REQUEST_CODE_PICK_FILE
                );
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_DIR) {
            if(resultCode == this.RESULT_OK) {
                //String basePath = getApplicationContext().getFilesDir().getAbsolutePath();
                //String fromPath = basePath + "/databases/yirisanxing.db";
                String fromPath = "/data/data/com.youdao.nanti.candy.yirisanxing/databases/yirisanxing.db";
                String dirPath = data.getStringExtra(ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.returnDirectoryParameter);
                String toPath = dirPath + "/yirisanxing.db.backup";
                if(copy(fromPath, toPath)) {
                    Toast.makeText(this, "export success!", Toast.LENGTH_LONG).show();
                }
                //myWebView.loadUrl("javascript:getPath('"+newDir+"')");
            } else {
                Toast.makeText(this, "Received NO result from file browser", Toast.LENGTH_LONG).show(); 
            }
        }
        
        if (requestCode == REQUEST_CODE_PICK_FILE) {
            if(resultCode == this.RESULT_OK) {
                String filePath = data.getStringExtra(ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.returnFileParameter);
                String toPath = "/data/data/com.youdao.nanti.candy.yirisanxing/databases/yirisanxing.db";
                try {
                    SQLiteDatabase validateDb = SQLiteDatabase.openDatabase(filePath, null, 0);
                    validateDb.close();
                } catch (SQLiteException e) {
                    Toast.makeText(this, "Import database failed: selected file is not a proper database file!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(copy(filePath, toPath)) {
                    Toast.makeText(this, "import success!", Toast.LENGTH_LONG).show();
                }
                //myWebView.loadUrl("javascript:getPath('"+newFile+"')");
            } else {
                Toast.makeText(this, "Received NO result from file browser", Toast.LENGTH_LONG).show(); 
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private boolean copy(String fromPath, String toPath) {
        File from = new File(fromPath);
        File to = new File(toPath);
        if (!from.exists()) {
            Toast.makeText(this, "Failed: database file missing!", Toast.LENGTH_LONG).show();
            return false;
        }
        // TODO: use action to.
        try {
            InputStream in = new FileInputStream(from);    
            OutputStream out = new FileOutputStream(to);
            byte[] buf = new byte[1024];    
            int len;    
            while ((len = in.read(buf)) != -1)
            {    
                out.write(buf, 0, len);    
            }
    
            in.close();
            out.close();
            return true;
        }
        catch (FileNotFoundException e)    
        {
            e.printStackTrace();
            Toast.makeText(this, "file error", Toast.LENGTH_LONG).show();
            return false;
        }
        catch (IOException e)    
        {
            e.printStackTrace();
            Toast.makeText(this, "io error", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    
    // UI related interface
    public void closeActivity() {
        finish();
    }
    
    public void pickTime() {
        new TimePickerDialog(this, this, 22, 00, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //myWebView.loadUrl("javascript:setTime('" + String.valueOf(hourOfDay) + "', '" + String.valueOf(minute) + "')");
        class JsThread implements Runnable {
            private int mHour;
            private int mMinute;
            JsThread(int hour, int minute) {
                mHour = hour;
                mMinute = minute;
            }
            
            @Override
            public void run() {        
                myWebView.loadUrl("javascript:setTime('" + String.valueOf(mHour) + "', '" + String.valueOf(mMinute) + "')");
            }
        }

        handler.post(new JsThread(hourOfDay, minute));

    }
    
    public void pickDate() {
        new DatePickerDialog(this, this, 2012, 1, 1).show();
    }
    
    @Override
    public void  onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        
    }

    
}
