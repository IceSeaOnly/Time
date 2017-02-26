package site.binghai.number2.Application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/9/15.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initCloudChannel(this);
        initBugly();
        initX5Browse();
    }

    private void initX5Browse() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //TbsDownloader.needDownload(getApplicationContext(), false);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                Log.e("app", " onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
                Log.e("app", "onCoreInitFinished");
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d("app","onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                Log.d("app","onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                Log.d("app","onDownloadProgress:"+i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }


    private void initBugly() {
        // 腾讯百灵鸟初始化
        CrashReport.initCrashReport(getApplicationContext(), "1400014720", false);
    }

    private static final String TAG = "Init";
    public static final CloudPushService pushService = PushServiceFactory.getCloudPushService();

    /**
     * 初始化云推送通道,阿里云推送
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                AccountUtil.aliServiceBind_state = false;
                UnBind();
            }
        });
    }
    /**
     * 解绑
     * */

    public static void UnBind(){
        pushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.i("阿里云账号解绑","解绑成功");
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.i("阿里云账号解绑","解绑失败");
            }
        });
    }

    public static void bindAccount(final Context context, String uname) {
        pushService.bindAccount(uname, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.i("阿里云账号绑定","绑定成功，deviceId="+pushService.getDeviceId());
                /** 发送最新的网络恢复信息到服务器*/
                AccountUtil.APP_NET_CONNECT();
                pushService.register(context, new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("阿里云账号注册","注册成功，deviceId="+pushService.getDeviceId());
                        AccountUtil.aliServiceBind_state = true;
                        //FeedBackServer(AccountUtil.userid,pushService.getDeviceId());
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        Log.e("阿里云账号注册","注册失败");
                        AccountUtil.aliServiceBind_state = false;
                    }
                });
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.e("阿里云账号绑定","绑定失败");
                AccountUtil.aliServiceBind_state = false;
            }
        });
    }
    /** 发送最新的绑定信息到服务器*/
    private static void FeedBackServer(Long userid, String deviceId) {
        Map map = new HashMap();
        map.put("deviceId",deviceId);
        HttpUtil.post(Config.UpdateUserDeviceId,SignUtil.CommonUserTokenSIGN(map),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}

