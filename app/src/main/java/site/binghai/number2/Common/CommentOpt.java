package site.binghai.number2.Common;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/11/20.
 */
public class CommentOpt {
    public void PeiZanOpt(boolean checked, int opt, Long entityId, String ctype) {
        Map param = new HashMap();
        param.put("entityId", entityId);
        param.put("action", checked ? 1 : 0);
        param.put("opt", opt);
        param.put("ctype", ctype);
        param.put("username", AccountUtil.nick_name);
        HttpUtil.post(Config.commonPeiZanOpt, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler());
    }

    public void Report_JuBao(Long entityId,String ctype,String otherinfo){
        Map param = new HashMap();
        param.put("entityId", entityId);
        param.put("ctype", ctype);
        param.put("otherinfo",otherinfo);
        HttpUtil.post(Config.Report_JuBao, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler());
    }

    public void DeleteEntity(Long entityId,String ctype){
        Map param = new HashMap();
        param.put("entityId", entityId);
        param.put("ctype", ctype);
        HttpUtil.post(Config.DeleteEntity, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler());
    }

    public void DeleteComment(int type,Long entityId,Long id){
        Map param = new HashMap();
        param.put("entityId", entityId);
        param.put("type", type);
        param.put("id", id);
        HttpUtil.post(Config.DeleteComment, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AccountUtil.setToken(response.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
