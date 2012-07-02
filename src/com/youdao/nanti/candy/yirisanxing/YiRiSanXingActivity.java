package com.youdao.nanti.candy.yirisanxing;


import com.youdao.nanti.candy.yirisanxing.alarm.Action;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class YiRiSanXingActivity extends Activity {
	
	private WebView myWebView;
	private JavaScriptInterface jsInterface;
	
	private WebView bPanel; //bottom panel
	private Handler handler = new Handler();
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myWebView = (WebView) findViewById(R.id.webview);
        
        Intent intent = getIntent();
        if (intent.getAction().equals("android.intent.action.MAIN")) {
        	loadMain();
        } else if (intent.getAction().equals(Action.REVIEW)) {
        	loadReview();
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
					.setTitle("javaScript dialog")
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
			
			
		});

        
        jsInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(jsInterface, "Android");
        
        
        
        // Load a web page
//        myWebView.loadUrl("file:///android_asset/index.html?id=2");
        //myWebView.loadUrl("file:///android_asset/test_xiaoyong.html");
        myWebView.loadUrl("file:///android_asset/addNewItem.html");
        
        //////-----------For bottom panel-----------------------------/////////
        
        bPanel = (WebView) findViewById(R.id.bPanel);
        bPanel.loadUrl("file:///android_asset/bottomPanel.html");
        WebSettings bPanelSettings = bPanel.getSettings();
        bPanelSettings.setJavaScriptEnabled(true);
        //hidden scroll bar.
        bPanel.setHorizontalScrollBarEnabled(false);
        bPanel.setVerticalScrollBarEnabled(false);
        
        //bind top panel communication interface.
        bPanel.addJavascriptInterface(new TopPanelCommunicationInterface(handler, myWebView), "top");
        
        //////-----------bottom panel END-----------------------------/////////
    }
    
    private void loadReview() {
        // Enable JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        //hidden scroll bar.
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.setVerticalScrollBarEnabled(true);
        
        //add key board.
        myWebView.requestFocusFromTouch();
        
        jsInterface = new JavaScriptInterface(this);
        jsInterface.open();
        myWebView.addJavascriptInterface(jsInterface, "Android");
        
        
        // Load a web page
//        myWebView.loadUrl("file:///android_asset/index.html?id=2");
        //myWebView.loadUrl("file:///android_asset/test_xiaoyong.html");
        myWebView.loadUrl("file:///android_asset/index.html");
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
    public void onBackPressed() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this); 
    	builder.setMessage("Are you sure you want to exit?") 
               .setCancelable(false) 
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
                   public void onClick(DialogInterface dialog, int id) { 
                	   finish();
                   }                    
               })                               
               .setNegativeButton("No", new DialogInterface.OnClickListener() {                 
            	   public void onClick(DialogInterface dialog, int id) {                      
            		   dialog.cancel();                 
            	   }             
               });      
    	AlertDialog alert = builder.create(); 
    	alert.show();        	
    }
}
