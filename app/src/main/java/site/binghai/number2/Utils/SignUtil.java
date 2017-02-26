package site.binghai.number2.Utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecuritySignature;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import site.binghai.number2.AccountUtil;

/**
 * Created by Administrator on 2016/9/23.
 */
public class SignUtil {


    private static SecuritySignature securitySignature;

    public static String getSign(String str) {
        try {
            Log.i("MD5=", MD5.encryption(str.trim()));
            return securitySignature.sign(MD5.encryption(str.trim()), Config.ali_securityKey);
        } catch (JAQException e) {
            e.printStackTrace();
        }
        return "NULL";
    }

    public static boolean InitAliSecurity(Context context) {
        securitySignature = new SecuritySignature(context);
        return true;
    }

    public static RequestParams CommonUserTokenSIGN(Map<String, Object> ps) {
        com.alibaba.fastjson.JSONObject param = new com.alibaba.fastjson.JSONObject();

        param.put("userid", AccountUtil.userid);
        param.put("token", AccountUtil.token);
        if (ps != null)
            for (Map.Entry<String, Object> p : ps.entrySet()) {
                param.put(p.getKey(), p.getValue());
            }

        String SIGN = SignUtil.getSign(param.toString());
        RequestParams p = new RequestParams();
        p.add("data", param.toString());
        p.add("SIGN", SIGN);
        return p;
    }
}
