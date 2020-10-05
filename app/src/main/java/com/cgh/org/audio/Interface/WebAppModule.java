package com.cgh.org.audio.Interface;


import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.cgh.org.audio.Config.Setup;

public class WebAppModule extends FragmentActivity {

    public static final String TAG = "WebAppModule";
    private WebView webView = null;
    private Object webInterface = null;

    private WebController web = null;

    private static final String WEBAPP_URL = Setup.DEF_WEBAPP_URL;

    private static String data = new String();

    // using default parameter constructor
    public WebAppModule() { /***/}

    // using custom parameter constructor
    public WebAppModule(WebView _webView, Object _webInterface) {
        this.webView = _webView;
        this.webInterface = _webInterface;
    }

    // setting webview properties
    public void LOAD_WEBAPP_SET() {
        this.web = new WebController();
        this.web.setWebView(this.webView);
        this.web.setWebInterface(this.webInterface);
        this.web.Setting();
    }

    // using webview to load webapp url
    public void LOAD_WEBAPP_URL() {
        this.web.Loading(this.WEBAPP_URL);
    }

    // using webview to call javascript method
    public void CallWebAppFunction(int id) {
        if (id == this.webView.getId()) {
            this.web.Calling(this.data);
        }
    }

    // using webview to show outdoor localization view
    public void WebShow() {
        this.webView.setVisibility(View.VISIBLE);
    }

    public void setWebView(WebView _webView) {
        this.webView = _webView;
    }

    public void setWebInterface(Object _webInterface) {
        this.webInterface = _webInterface;
    }

    public void setData(String _data) {
        this.data = _data;
    }

    public void setWebChromeClient(WebChromeClient MyWebChromeClient){
        this.webView.setWebChromeClient(MyWebChromeClient);
    };

    public void setWebViewClient(WebViewClient MyWebViewClient){
        this.webView.setWebViewClient(MyWebViewClient);
    }
}
