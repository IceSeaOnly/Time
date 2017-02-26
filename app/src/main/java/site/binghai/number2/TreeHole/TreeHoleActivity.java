package site.binghai.number2.TreeHole;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import site.binghai.number2.R;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.ToastUtil;

public class TreeHoleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_hole);


    }

    /** 打开界面下载的第一个语音*/
    private void DownLoadData() {
        RequestParams param = new RequestParams();
        HttpUtil.post(Config.DownLoadLastestTreeHoleVoice,param,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if(response.getBoolean("result")){
                        JSONObject voice = response.getJSONObject("voice");
                        InitData(voice.getInt("id"),
                                voice.getLong("owner_id"),
                                voice.getString("owner_avatar_url"),
                                voice.getString("voice_url"),
                                voice.getInt("voice_long"));
                    }else {
                        new ToastUtil(TreeHoleActivity.this).errorNotice("错误","网络异常，请检查网络后重试!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void InitData(int id,Long fid,String avatar,String vurl,int vlong) {
    }

}
