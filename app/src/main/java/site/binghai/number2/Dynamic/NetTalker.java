package site.binghai.number2.Dynamic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/10/25.
 * 网络交流器
 */
public class NetTalker {
    private DynamicIndexGetCallback callback;
    private DynamicEntityLoader loader;
    private Context context;

    public NetTalker(Context context, DynamicIndexGetCallback callback, DynamicEntityLoader loader) {
        this.callback = callback;
        this.context = context;
        this.loader = loader;
    }

    public void getDynamicEntities(List<Long> dynamicIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dynamicIds.size(); i++) {
            if (i == 0)
                sb.append(dynamicIds.get(i));
            else sb.append(":" + dynamicIds.get(i));
        }

        Map param = new HashMap();
        param.put("dlist", sb.toString());

        HttpUtil.post(Config.getDynamicEntities, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("下载动态实体成功", response.toString());
                super.onSuccess(statusCode, headers, response);
                ArrayList<DynamicContentEntity> entities = TransferJsonToDynamicContentEntity(response);
                loader.loadDynamicData(entities);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(context, "获取动态失败，请检查网络", Toast.LENGTH_SHORT).show();
                loader.loadFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(context, "获取动态失败，请检查网络", Toast.LENGTH_SHORT).show();
                loader.loadFailed();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(context, "获取动态失败，请检查网络", Toast.LENGTH_SHORT).show();
                loader.loadFailed();
            }
        });
    }

    private ArrayList<DynamicContentEntity> TransferJsonToDynamicContentEntity(JSONArray json) {
        return (ArrayList<DynamicContentEntity>)
                com.alibaba.fastjson.JSONArray.parseArray(json.toString(), DynamicContentEntity.class);
    }

    public void getDynamicIndexs() {

        HttpUtil.post(Config.getDynamicIndex, SignUtil.CommonUserTokenSIGN(null), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("下载动态索引成功", response.toString());
                super.onSuccess(statusCode, headers, response);
                ArrayList<Long> indexs = new ArrayList();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        indexs.add(response.getLong(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.indexDownloadComplete(indexs);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("测试下载动态索引", "失败");
                Toast.makeText(context, "获取动态失败，请检查网络", Toast.LENGTH_SHORT).show();
                callback.downloadFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("测试下载动态索引", "失败");
                Toast.makeText(context, "获取动态失败，请检查网络", Toast.LENGTH_SHORT).show();
                callback.downloadFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("测试下载动态索引", "失败");
                Toast.makeText(context, "获取动态失败，请检查网络", Toast.LENGTH_SHORT).show();
                callback.downloadFailed();
            }
        });
    }
}
