package site.binghai.number2.Messages;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.MessageDB;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/10/10.
 */
public class FriendTool {

    public static ArrayList<Long> myfriendsInContacts = new ArrayList<>();

    private static void RefreshFriendsList(final MessageDB messageDB , final AfterRefreshFriendList after){
        HttpUtil.post(Config.refreshAllFriends, SignUtil.CommonUserTokenSIGN(null),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                JSONArray response = null;
                try {
                    response = resp.getJSONArray("friends");
                    messageDB.clearFriends();
                    myfriendsInContacts.clear();
                    for(int i = 0;i < response.length();i++){
                        try {
                            JSONObject f = response.getJSONObject(i);
                            myfriendsInContacts.add(f.getLong("userid"));
                            messageDB.InsertORupdateFriend(AccountUtil.userid,f.getLong("userid"),f.getString("avatar"),f.getString("nickname"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    AccountUtil.setToken(resp.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                after.onFreshSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                after.onFreshFailed(errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                after.onFreshFailed(errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                after.onFreshFailed(throwable.toString());
            }
        });
    }

    public static void RefreshFriendsList(Context context, final AfterRefreshFriendList after){
        final MessageDB messageDB = new MessageDB(context,"MessageDatabase",null,1);
        RefreshFriendsList(messageDB,after);
    }
}
