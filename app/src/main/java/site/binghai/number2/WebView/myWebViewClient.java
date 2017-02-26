package site.binghai.number2.WebView;

import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * webView视图客户端
 * @author Administrator
 *
 */
class myWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //让新打开的网页在当前webview显示
        view.loadUrl(url);
        return true;
    }

}
