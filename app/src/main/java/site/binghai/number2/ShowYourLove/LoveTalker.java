package site.binghai.number2.ShowYourLove;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.ShowYourLove.ShowYourLoveEntity;
import site.binghai.number2.Utils.AfterDownload;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/11/15.
 */
public class LoveTalker {

    private AfterDownload after;

    public LoveTalker(AfterDownload after) {
        this.after = after;
    }

    public void next(Long start_timestamp,Integer size){
        Map<String,Object> ps = new HashMap<>();
        ps.put("start_timestamp",start_timestamp);
        ps.put("size",size);
        HttpUtil.post(Config.getNextShowYourLove, SignUtil.CommonUserTokenSIGN(ps),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                com.alibaba.fastjson.JSONObject resp = com.alibaba.fastjson.JSONObject.parseObject(response.toString());
                String arr = resp.getString("entity");
                ArrayList<ShowYourLoveEntity> data = (ArrayList<ShowYourLoveEntity>) com.alibaba.fastjson.JSONArray.parseArray(arr,ShowYourLoveEntity.class);
                after.afterDownLoad(data);
                Log.i("LoveTalker",response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                after.afterDownload(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                after.afterDownload(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                after.afterDownload(false);
            }
        });
    }

}
