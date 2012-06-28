package com.youdao.nanti.candy.yirisanxing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class YiRiSanXingActivity extends Activity {
	
	private WebView myWebView;
	private JavaScriptInterface jsInterface;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myWebView = (WebView) findViewById(R.id.webview);
        // Enable JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        //hidden scroll bar.
        myWebView.setHorizontalScrollBarEnabled(false);
       // myWebView.setVerticalScrollBarEnabled(false);
        
        //add key board.
        myWebView.requestFocusFromTouch();
        
		        
		//enable javaScript alert.
        final Context myApp = this;
        myWebView.setWebChromeClient(new WebChromeClient(){
			
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
        jsInterface.open();
        myWebView.addJavascriptInterface(jsInterface, "Android");

        // Load a web page
        myWebView.loadUrl("file:///android_asset/index.html?id=2");
        //myWebView.loadUrl("file:///android_asset/myindex.html");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}