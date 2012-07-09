package com.youdao.nanti.candy.yirisanxing;


import ua.com.vassiliev.androidfilebrowser.FileBrowserActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.youdao.nanti.candy.yirisanxing.alarm.Action;
import com.youdao.nanti.candy.yirisanxing.alarm.Alarm;

public class YiRiSanXingActivity extends Activity {
    
    private WebView myWebView;
    private JavaScriptInterface jsInterface;
    
    private WebView bPanel; //bottom panel
    private Handler handler = new Handler();
    
    private String mAction;
    
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
            
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Toast.makeText(getBaseContext(), "openfile", Toast.LENGTH_LONG).show();
            
            }
            
        });
       
        jsInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(jsInterface, "Android");
        myWebView.addJavascriptInterface(this, "Activity");
        
        // Load a web page
//        myWebView.loadUrl("file:///android_asset/index.html?id=2");
        //myWebView.loadUrl("file:///android_asset/test_xiaoyong.html");
        myWebView.loadUrl("file:///android_asset/historyList.html");
        //myWebView.loadUrl("file:///android_asset/chart_demo.html");
        
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

        String sId = String.valueOf(id);
        String sTime = String.valueOf(time);
        // Load a web page
        //myWebView.loadUrl("file:///android_asset/activeItem.html?id=" + sId + "&time=" + sTime);
        myWebView.loadUrl("file:///android_asset/activeItem.html");
        
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
            if (mAction.equals(Action.MAIN)) {
                long id = Long.valueOf(intent.getData().getSchemeSpecificPart());
                long time = intent.getLongExtra(Alarm.ALERT_TIME, System.currentTimeMillis());
                loadReview(id, time);
            } else if (mAction.equals(Action.REVIEW)) {
                //jsInterface.queueReview()
            }
        }
        mAction = intent.getAction();
    }
        
    @Override
    public void onBackPressed() {
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
                String newDir = data.getStringExtra(ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.returnDirectoryParameter);
                myWebView.loadUrl("javascript:getPath('"+newDir+"')");
            } else {//if(resultCode == this.RESULT_OK) {
                Toast.makeText(
                        this, 
                        "Received NO result from file browser",
                        Toast.LENGTH_LONG).show(); 
            }//END } else {//if(resultCode == this.RESULT_OK) {
        }//if (requestCode == REQUEST_CODE_PICK_DIR) {
        
        if (requestCode == REQUEST_CODE_PICK_FILE) {
            if(resultCode == this.RESULT_OK) {
                String newFile = data.getStringExtra(ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.returnFileParameter);
                myWebView.loadUrl("javascript:getPath('"+newFile+"')");
            } else {//if(resultCode == this.RESULT_OK) {
                Toast.makeText(
                        this, 
                        "Received NO result from file browser",
                        Toast.LENGTH_LONG).show(); 
            }//END } else {//if(resultCode == this.RESULT_OK) {
        }//if (requestCode == REQUEST_CODE_PICK_FILE) {
        
        super.onActivityResult(requestCode, resultCode, data);
    }
}
