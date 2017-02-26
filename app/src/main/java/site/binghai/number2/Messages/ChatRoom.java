package site.binghai.number2.Messages;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.maplejaw.library.MutilEmoticonKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Common.CommentOpt;
import site.binghai.number2.Common.UpdateCallBack;
import site.binghai.number2.MyNetStateReceiver;
import site.binghai.number2.R;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.MessageDB;
import site.binghai.number2.Utils.SignUtil;
import site.binghai.number2.Utils.TimeFormater;
import site.binghai.number2.Utils.ToastUtil;
import site.binghai.number2.Utils.VibratorUtil;

public class ChatRoom extends AppCompatActivity {

    private CheckBox toolbox_btn_face;
    private CheckBox toolbox_btn_more;
    private EditText chatroom_edittext;
    private Button toolbox_btn_send;
    private MutilEmoticonKeyboard mEmoticonKeyboard;
    private boolean face_box_closed = true;
    private View flag_line;
    private ArrayList<MessageEntityImpl> msgs;
    private ListView chatroom_message_list;

    /**
     * 当前聊天窗聊天对象id
     */
    public static Long cur_friendid = -1L;
    public static MessageDB messageDBInstance;
    public static ArrayList<MessageEntityImpl> nowMsgs;
    public static MessageAdapter nowMadpter;
    private SweetAlertDialog pDialog;

    /**
     * 处理到达的新消息
     */
    public static boolean HandleNewMsg(String msg_data) {
        JSONObject msg = null;
        TextMessage textMessage = null;
        try {
            msg = new JSONObject(msg_data);
            Long fid = msg.getLong("mid");// 对方的“我的id”
            Long mid = AccountUtil.userid;
            String content = msg.getString("content");
            String avatar = msg.getString("avatar");
            Long timestamp = msg.getLong("timestamp");
            Long msgid = messageDBInstance.insertMessage(
                    mid,
                    fid,
                    content,
                    timestamp,
                    true,
                    MessageEntityImpl.TEXT_MSG,
                    false);
            textMessage = new TextMessage(msgid, content, "", avatar, true, timestamp, fid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (textMessage != null && textMessage.getFriendId().equals(cur_friendid)) {
            /** 新消息来自正在聊天的对象，更新聊天窗口*/
            nowMsgs.add(textMessage);
            nowMadpter.notifyDataSetChanged();
        } else {
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        cur_friendid = -1L;
        nowMsgs = null;
        nowMadpter = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        /**
         * 不弹出软键盘
         * */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InitView();
        InitAction();
        InitData();
        GotoListBottom();

        setTitle("正在和" + hName + "聊天...");
    }

    private void GotoListBottom() {
        chatroom_message_list.setSelection(msgs.size() - 1);
    }

    /**
     * 未加载完毕状态下的默认头像
     */
    private String hAvatar = "http://118.192.140.147/data/f_91175231.png";
    MessageAdapter madpter;
    private String hName;

    private void InitData() {

        String hid = getIntent().getStringExtra("hid");
        hAvatar = getIntent().getStringExtra("havatar");
        hName = getIntent().getStringExtra("hname");

        String mid = String.valueOf(AccountUtil.userid);
        msgs = messageDBInstance.getAllMessage(mid, hid, AccountUtil.my_avatar, hAvatar);
        madpter = new MessageAdapter(msgs, ChatRoom.this);
        chatroom_message_list.setAdapter(madpter);

        cur_friendid = Long.parseLong(hid);
        nowMsgs = msgs;
        nowMadpter = madpter;
    }

    private void InitAction() {
        /**
         * 表情库开关控件
         * */
        toolbox_btn_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (face_box_closed)
                    open_face_box();
                else
                    close_face_box();
            }
        });

        toolbox_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = chatroom_edittext.getText().toString();
                if (msg.length() > 0) {
                    chatroom_edittext.setText("");
                    sendMsg(msg);
                }
            }
        });

        chatroom_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                close_face_box();
            }
        });

        chatroom_message_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                ArrayList<DialogMenuItem>items = new ArrayList<DialogMenuItem>();
                items.add(new DialogMenuItem("复制内容",R.mipmap.copy_itme_icon));
                if(msgs.get(position).isMyMsg())
                    items.add(new DialogMenuItem("撤回消息",R.mipmap.callback_item_icon));
                else
                    items.add(new DialogMenuItem("举报",R.mipmap.ic_winstyle_delete));
                final NormalListDialog normalListDialog = new NormalListDialog(ChatRoom.this,items);
                normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int p, long id) {
                        if(p == 0)
                            onClickCopy(position);
                        else{
                            if(msgs.get(position).isMyMsg())
                                callBackMsg(position);
                            else{
                                JuBao(msgs.get(position));
                            }
                        }
                        normalListDialog.dismiss();
                    }
                });
                normalListDialog.isTitleShow(false);
                normalListDialog.show();
                return true;
            }
        });
    }

    /** 举报*/
    private void JuBao(final MessageEntityImpl messageEntity) {
        new ToastUtil(ChatRoom.this)
                .warningNotice("举报",
                        "你确定要举报这条表白吗?",
                        "我点错了",
                        "我要举报",
                        "结果",
                        "举报成功，我们处理之后将会告知您处理结果，感谢您自觉维护Time环境！",
                        "好的",
                        new UpdateCallBack() {
                            @Override
                            public void onUpdate(Object info) {
                                new CommentOpt().Report_JuBao(messageEntity.getId(),"chatmsg",AccountUtil.userid+";"+messageEntity.getTextMsg());
                            }
                        });

    }

    private void pDismiss() {
        pDialog.dismiss();
    }

    public void onProgressing(String msg) {
        pDialog = new SweetAlertDialog(ChatRoom.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(msg);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * 消息撤回
     */
    private void callBackMsg(final int position) {
        Long stp = msgs.get(position).getTimeStamp();
        onProgressing("尝试撤回...");
        Map param = new HashMap();
        param.put("hid", cur_friendid);
        param.put("timestamp", stp);
        HttpUtil.post(Config.CallBackChatMsg, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pDismiss();
                try {
                    if (response.getBoolean("result")) {
                        messageDBInstance.deleteMsg(msgs.get(position).getId());
                        msgs.remove(position);
                        madpter.notifyDataSetChanged();
                        new ToastUtil(ChatRoom.this).successNotice("操作完成", response.getString("info"));
                    } else
                        new ToastUtil(ChatRoom.this).errorNotice("操作不成功", response.getString("reason"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDismiss();
                Toast.makeText(ChatRoom.this, "服务器错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDismiss();
                Toast.makeText(ChatRoom.this, "服务器错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDismiss();
                Toast.makeText(ChatRoom.this, "服务器错误", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onClickCopy(int p) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(msgs.get(p).getTextMsg());
        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送消息
     */
    private void sendMsg(String msg) {
        /** 检查网络状况*/
        if (!MyNetStateReceiver.NET_STATE) {
            new ToastUtil(this).errorNotice("出错了", "网络异常，请检查网络！");
            return;
        }
        Long ts = System.currentTimeMillis();
        /** 存到本地数据库*/
        Long id = messageDBInstance.insertMessage(AccountUtil.userid, cur_friendid, msg, ts, false, MessageEntityImpl.TEXT_MSG, false);
        Log.i("ChatRoom","消息本地id="+id);
        /** 准备发送到服务器*/
        TextMessage message = new TextMessage(id, msg, TimeFormater.format_hh_mm(ts), AccountUtil.my_avatar, false, ts, cur_friendid);
        /** 发送到服务器*/
        PostMsgToServer(message);

        msgs.add(message);
        madpter.notifyDataSetChanged();
        GotoListBottom();
    }

    /**
     * 发送到服务器
     */
    private void PostMsgToServer(TextMessage message) {
        Map msg = new HashMap();
        msg.put("mid", AccountUtil.userid);
        msg.put("fid", message.getFriendId());
        /** 我对该好友的称呼*/
        msg.put("fname", AccountUtil.nick_name);
        msg.put("content", message.getTextMsg());
        msg.put("avatar", message.getAvatarUrl());
        msg.put("timestamp", message.getTimeStamp());

        HttpUtil.post(Config.postMsg, SignUtil.CommonUserTokenSIGN(msg), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
//                Toast.makeText(ChatRoom.this,"发送成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(ChatRoom.this, "发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(ChatRoom.this, "发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(ChatRoom.this, "发送失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitView() {
        toolbox_btn_face = (CheckBox) findViewById(R.id.toolbox_btn_face);
        toolbox_btn_more = (CheckBox) findViewById(R.id.toolbox_btn_more);
        chatroom_edittext = (EditText) findViewById(R.id.chatroom_edittext);
        toolbox_btn_send = (Button) findViewById(R.id.toolbox_btn_send);
        mEmoticonKeyboard = (MutilEmoticonKeyboard) this.findViewById(R.id.mEmoticonKeyboard);
        flag_line = findViewById(R.id.flag_line);
        chatroom_message_list = (ListView) findViewById(R.id.chatroom_message_list);

        /**
         * 绑定输入框
         * */
        mEmoticonKeyboard.setupWithEditText(chatroom_edittext);
        close_face_box();
    }

    @Override
    public void onBackPressed() {
        if (!face_box_closed) {
            close_face_box();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                closeInputBoard();
            }
            if (isFaceBoxShouldClosed(ev))
                close_face_box();
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 关闭键盘
     */
    private void closeInputBoard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken()
                        , InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 关闭表情框
     */
    private void close_face_box() {
        mEmoticonKeyboard.setVisibility(View.GONE);
        face_box_closed = true;
        toolbox_btn_face.setChecked(false);
    }

    /**
     * 开启表情框
     */
    private void open_face_box() {
        closeInputBoard();
        mEmoticonKeyboard.setVisibility(View.VISIBLE);
        face_box_closed = false;
        toolbox_btn_face.setChecked(true);
    }

    /**
     * 点击输入框之外的地方关闭键盘
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据下划线的位置判断是否关闭表情框
     */
    private boolean isFaceBoxShouldClosed(MotionEvent event) {
        int[] leftTop = {0, 0};
        flag_line.getLocationInWindow(leftTop);
        if (event.getY() < leftTop[1])
            return true;
        return false;
    }

}
