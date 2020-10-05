package com.cgh.org.audio.Interface;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.cgh.org.audio.Config.Setup;

public class WebController {
    private WebView webView = null;
    private Object webInterface = null;

    private static final String USER_AGENT = Setup.DEF_USER_AGENT;
    private static final String WEBAPP_NAME = Setup.DEF_WEBAPP_NAME;

    // using default parameter constructor
    public WebController() { /***/ }

    // using custom parameter constructor
    public WebController(WebView _webView, Object _webInterface)
    {
        this.webView = _webView;
        this.webInterface = _webInterface;
    }

    // setting webview properties
    public void Setting()
    {
        this.webView.setVisibility(WebView.INVISIBLE);
        this.webView.setWebChromeClient(new WebChromeClient());
        this.webView.getSettings().setUserAgentString(this.USER_AGENT);
        this.webView.getSettings().setAppCacheEnabled(false);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadsImagesAutomatically(false);
        this.webView.getSettings().setAllowFileAccess(true);
        this.webView.getSettings().setAllowContentAccess(true);
        this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.webView.addJavascriptInterface(this.webInterface, this.WEBAPP_NAME);
    }

    // using webview to load webapp url
    public void Loading(final String data)
    {
        this.webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(data);
            }
        });
    }

    // using webview to call javascript method
    public void Calling(final String data)
    {
        this.webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + data);
            }
        });
    }


    public void setWebView(WebView _webView) { this.webView = _webView; }
    public void setWebInterface(Object _webInterface) { this.webInterface = _webInterface; }
}
