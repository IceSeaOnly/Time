package site.binghai.number2.WebView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import site.binghai.number2.R;


public class WebViewActivity extends Activity {
    private WebView wv;
    private SweetAlertDialog pDialog = null;
    private final Set<String> offlineResources = new HashSet<>();
    private String TAG = "WebView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        fetchOfflineResources();
        String url = getIntent().getStringExtra("url");
        InitWebView();
        //加载远程网页
        if (null != savedInstanceState) {
            wv.restoreState(savedInstanceState);
        } else {
            wv.loadUrl(url);
        }
        InitClient();
    }


    private void fetchOfflineResources() {
        AssetManager am = getAssets();
        try {
            String[] res = am.list("offline_res");
            if(res != null) {
                Collections.addAll(offlineResources, res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void InitWebView() {
        wv = (WebView) findViewById(R.id.my_web_view);
        WebSettings settings = wv.getSettings();
        // 设置支持javascript
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);//设置是否显示缩放工具
        settings.setSupportZoom(false);//设置是否支持缩放
        settings.setDefaultFontSize(15);
        settings.setSupportZoom(false); // 设置是否支持变焦

        // 增加接口方法,让html页面调用
        AndroidJavaScript aj = new AndroidJavaScript(WebViewActivity.this);
        wv.addJavascriptInterface(aj, "binghai");
    }

    private boolean web_err = false;

    private void InitClient() {


        wv.setWebViewClient(new WebViewClient(){
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                int lastSlash = url.lastIndexOf("/");
                if(lastSlash != -1) {
                    String suffix = url.substring(lastSlash + 1);
                    if(offlineResources.contains(suffix)) {
                        String mimeType;
                        if(suffix.endsWith(".js")) {
                            mimeType = "application/x-javascript";
                        } else if(suffix.endsWith(".css")) {
                            mimeType = "text/css";
                        } else {
                            mimeType = "text/html";
                        }
                        Log.i(TAG,suffix+" is replaced as local.");
                        try {
                            InputStream is = getAssets().open("offline_res/" + suffix);
                            return new WebResourceResponse(mimeType, "UTF-8", is);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (pDialog != null) {
                    pDialog.dismiss();
                    pDialog = null;
                }
            }
        });


        wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (pDialog == null) {
                    pDialog = new SweetAlertDialog(WebViewActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("加载中...");
                    pDialog.setCancelable(true);
                    pDialog.show();
                }

                if (web_err) {
                    Intent intent = new Intent();
                    intent.putExtra("msg", "请检查网络链接是否正常!");
                    setResult(-1, intent);
                    web_err = false;
                    finish();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

                nowTitlt = title;
                if (title.length() > 0 && title.contains("找不到网页"))
                    web_err = true;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        wv.saveState(outState);
    }

    private String nowTitlt = "";
    private String[] blackNames = {"注册"};
    @Override
    public void onBackPressed() {
        boolean cat = false;
        for(int i = 0;i < blackNames.length;i++){
            if(nowTitlt.contains(blackNames[i])){
                cat = true;
                break;
            }
        }
        if(cat)
            Toast.makeText(WebViewActivity.this,"点击左上角返回登录",Toast.LENGTH_SHORT).show();
        else super.onBackPressed();
    }



    @Override
    protected void onDestroy() {
        wv.stopLoading();
        wv = null;
        if (pDialog != null)
            pDialog.dismiss();
        super.onDestroy();
    }
}
