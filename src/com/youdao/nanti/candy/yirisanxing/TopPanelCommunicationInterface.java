package com.youdao.nanti.candy.yirisanxing;

import android.os.Handler;
import android.webkit.WebView;

/**
 * This class is used to communicate with the javaScript function in the topPanel,
 * calling from java code layer.
 * @author wavefancy@gmail.com
 *
 */
public class TopPanelCommunicationInterface {
    private Handler handler;
    private WebView webView;
    private static String URL;
    
    public TopPanelCommunicationInterface(Handler handler, WebView webView) {
        this.handler = handler;
        this.webView = webView;
    }
    
//    public void CallTop() {
//        handler.post(new Runnable() {        
//            @Override
//            public void run() {
//                webView.loadUrl("javascript:testJavaCall('action from bottom panel')");
//            }
//        });
//    }
    
    /**
     * Call top panel to load the specific URL.
     * @param URL
     */
    public void topLoad(String URL) {
        TopPanelCommunicationInterface.URL = URL;
        //webView.loadUrl("javascript:topLoad('"+TopPanelCommunicationInterface.URL+"')");
        handler.post(new Runnable() {    
            @Override
            public void run() {        
                webView.loadUrl("javascript:topLoad('"+TopPanelCommunicationInterface.URL+"')");
            }
        });
    }
    
    public void adjustFocus(final String id) {
        // id can only be 1, 2, 3 or 4
        handler.post(new Runnable() {    
            @Override
            public void run() {        
                webView.loadUrl("javascript:adjustFocus(" + id + ");");
            }
        });
    }
}
