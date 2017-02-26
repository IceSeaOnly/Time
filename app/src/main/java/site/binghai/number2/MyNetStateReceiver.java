package site.binghai.number2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import site.binghai.number2.Index.MainActivity;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;


public class MyNetStateReceiver extends BroadcastReceiver {

    public MyNetStateReceiver() {
    }

    /** 默认网络状态可用*/
    public static boolean NET_STATE = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            Log.i("网络状态侦听:", "不可用");
            NET_STATE = false;
        } else {
            Log.i("网络状态侦听:", "可用");
            if(!NET_STATE)
                HttpUtil.post(Config.APP_NET_CONNECT, SignUtil.CommonUserTokenSIGN(null),new JsonHttpResponseHandler());
            NET_STATE = true;
        }
    }
}
