package site.binghai.number2.Application;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Index.MainActivity;
import site.binghai.number2.Messages.AfterRefreshFriendList;
import site.binghai.number2.Messages.ChatRoom;
import site.binghai.number2.Messages.FriendTool;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/9/15.
 */
public class IMessageReceviver extends com.alibaba.sdk.android.push.MessageReceiver {

    public static MainActivity mainActivity;

    public static void doMessage(final Context context, CPushMessage cPushMessage) {
        final String content = cPushMessage.getContent();
        Log.i("收到消息:", content);
        JSONObject msg = null;
        String type = null;
        String data = null;
        try {
            msg = new JSONObject(content);
            type = msg.getString("type");
            data = msg.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * TODO 检查是否是强制下线消息
         * */


        /** 是否是聊天消息*/
        if (isChatMsg(type)) {
            /** 判断消息是否已读*/
            boolean hasRead = ChatRoom.HandleNewMsg(data);
            /** 未读消息刷新界面*/
            if (!hasRead && mainActivity != null) {
                if (!friendInMyContacts(data)) { // 该好友尚未添加到本地列表
                    FriendTool.RefreshFriendsList(mainActivity, new AfterRefreshFriendList() {
                        @Override
                        public void onFreshSuccess() {}

                        @Override
                        public void onFreshFailed(String msg) {}
                    });
                }
                mainActivity.RefreshMessage(0);
            }

            try {
                confirmReceivedMessage(msg.getLong("messageKey"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** 判断是否是我的好友*/
    private static boolean friendInMyContacts(String data) {
        try {
            JSONObject msg = new JSONObject(data);
            Long friendid = msg.getLong("mid");
            return FriendTool.myfriendsInContacts.contains(friendid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 确认消息送达
     */
    private static void confirmReceivedMessage(Long messageKey) {
        Map ps = new HashMap();
        ps.put("messageKey", messageKey);
        HttpUtil.post(Config.IHaveReceivedMsg, SignUtil.CommonUserTokenSIGN(ps), new JsonHttpResponseHandler());
    }

    private static boolean isChatMsg(String type) {
        if (type == null) return false;
        return type.trim().equals("chatmsg");
    }

    private static boolean offLineByForce(String type, String data) {

        if (type == null || data == null) return false;

        if (type.trim().equals("sys:offlinebyforce")) {
            return data.equals(AccountUtil.token);
        }
        return false;
    }

    /**
     * 消息接收回调
     * <p/>
     * 用于接收服务端推送的消息。
     * 消息不会弹窗，而是回调该方法。
     * 参数
     * <p/>
     * context 上下文环境
     * message CPushMessage类型，可以获取消息Id、消息标题和内容。
     */
    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        super.onMessage(context, cPushMessage);
        doMessage(context, cPushMessage);
    }


    /**
     * 打开通知时会回调该方法。
     * 参数
     * <p/>
     * context 上下文环境
     * title 通知标题
     * summary 通知内容
     * extraMap 通知额外参数
     */
    @Override
    protected void onNotificationOpened(Context context, String s, String s1, String s2) {
        super.onNotificationOpened(context, s, s1, s2);
    }


    /**
     * 通知删除回调
     * <p/>
     * 删除通知时回调该方法。
     * 参数
     * <p/>
     * context 上下文环境
     * messageId 删除通知的Id
     */

    @Override
    protected void onNotificationRemoved(Context context, String s) {
        super.onNotificationRemoved(context, s);
    }

    /**
     * 通知接收回调
     * <p/>
     * 客户端接收到通知后，回调该方法。
     * 可获取到并处理通知相关的参数。
     * 参数
     * <p/>
     * context 上下文环境
     * title 通知标题
     * summary 通知内容
     * extraMap 通知额外参数
     */
    @Override
    protected void onNotification(Context context, String s, String s1, Map<String, String> map) {
        super.onNotification(context, s, s1, map);
    }

    @Override
    public void onHandleCall(Context context, Intent intent) {
        super.onHandleCall(context, intent);
    }

}
