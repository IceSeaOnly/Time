package site.binghai.number2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import site.binghai.number2.Application.MainApplication;
import site.binghai.number2.LoginLogout.AfterLogOut;
import site.binghai.number2.LoginLogout.LoginResult;
import site.binghai.number2.Utils.AfterAntoLogin;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;
import site.binghai.number2.Utils.UserDB;

/**
 * Created by Administrator on 2016/9/10.
 */
public class AccountUtil {
    public static Long userid = -1L;
    public static int grade = -1;
    public static String interest = "";
    public static Long phone = 0L;
    public static String school = "";
    public static int sex;
    public static String nick_name = "未登录";
    public static String my_avatar = Config.defaultAvatar;
    public static boolean login_state = false; // 登陆状态，默认为未登录
    public static boolean aliServiceBind_state = false; // 阿里服务绑定状态
    public static String token = "there_is_no_token_in_here";

    public static Long getAccountId() {
        return userid;
    }

    /**
     * 使用用户名、密码登录
     */
    public static void Login(final Context context, String uname, String upass, final LoginResult result) {
        Map params = new HashMap();
        params.put("phone", uname);
        params.put("password", upass);

        HttpUtil.post(Config.LoginServer, SignUtil.CommonUserTokenSIGN(params), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                Log.i("登录返回：", resp.toString());
                try {
                    if (!resp.getBoolean("result")) {
                        result.onLoginFailed(resp.getString("reason"));
                        return;
                    }
                    /** 账号密码正确*/
                    JSONObject res = resp.getJSONObject("user");
                    updateLocalInfo(res);
                    login_state = true;
                    MainApplication.bindAccount(context, String.valueOf(userid));
                    result.onLoginSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                    result.onSystemError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                result.onNetFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                result.onNetFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                result.onSystemError();
            }
        });
    }

    /**
     * 通知APP网络恢复
     */
    public static void APP_NET_CONNECT() {
        Log.i("通知服务器网络恢复", "已通知");
        HttpUtil.post(Config.APP_NET_CONNECT, SignUtil.CommonUserTokenSIGN(null), new JsonHttpResponseHandler());
    }

    /**
     * 退出登录
     */
    public static void LogOut(Context context, AfterLogOut after) {
        userid = -1L;
        grade = -1;
        interest = "";
        phone = 0L;
        school = "";
        nick_name = "未登录";
        my_avatar = Config.defaultAvatar;
        login_state = false; // 登陆状态，默认为未登录
        aliServiceBind_state = false; // 阿里服务绑定状态
        MainApplication.UnBind();
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("PASSWORD", "");
        editor.commit();
        after.afterLogOut();
    }


    /**
     * 更新本地信息
     */
    private static void updateLocalInfo(JSONObject res) {
        try {
            userid = res.getLong("userid");
            grade = res.getInt("grade");
            interest = res.getString("interest");
            phone = res.getLong("phone");
            school = com.alibaba.fastjson.JSONObject.parseObject(res.getString("school")).getString("schoolName");
            sex = res.getInt("sex");
            nick_name = res.getString("nickname");
            my_avatar = res.getString("avatar");
            token = res.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setToken(String token) {
        AccountUtil.token = token;
    }
}
