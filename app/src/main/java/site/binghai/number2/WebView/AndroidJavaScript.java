package site.binghai.number2.WebView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Messages.AfterRefreshFriendList;
import site.binghai.number2.Messages.FriendTool;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.MessageDB;
import site.binghai.number2.Utils.SignUtil;
import site.binghai.number2.Utils.UserDB;

/**
 * Created by Administrator on 2016/9/19.
 */
public class AndroidJavaScript {
    private Context context;
    private Activity activity;

    public AndroidJavaScript(Activity a) {
        activity = a;
        context = a;
    }

    /**
     * 拨打电话的方法
     */
    @JavascriptInterface
    public void call(String num) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + num));
        context.startActivity(intent);
    }

    /**
     * 关闭浏览器
     */

    @JavascriptInterface
    public void finish() {
        activity.finish();
    }

    /**
     * 成功提示并关闭浏览器
     */

    @JavascriptInterface
    public void finish_success(String msg) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("完成")
                .setContentText(msg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        activity.finish();
                    }
                })
                .show();
    }

    /**
     * 成功错误并关闭浏览器
     */

    @JavascriptInterface
    public void finish_error(String msg) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("错误")
                .setContentText(msg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        activity.finish();
                    }
                })
                .show();
    }

    /**
     * 仅提示成功
     * */
    @JavascriptInterface
    public void notice_success(String msg) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("提示")
                .setContentText(msg)
                .show();
    }

    /**
     * 仅提示错误
     * */
    @JavascriptInterface
    public void notice_error(String msg) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("提示")
                .setContentText(msg)
                .show();
    }

    /**
     * 刷新好友列表后关闭页面
     * */
    @JavascriptInterface
    public void refresh_friends_list(){
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("添加成功，正在刷新好友列表...");
        pDialog.setCancelable(false);
        pDialog.show();

        FriendTool.RefreshFriendsList(context, new AfterRefreshFriendList() {
            @Override
            public void onFreshSuccess() {
                pDialog.dismiss();
                activity.finish();
            }

            @Override
            public void onFreshFailed(String msg) {
                pDialog.dismiss();
                Toast.makeText(context,"刷新失败，请稍后重试",Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });

    }
}
