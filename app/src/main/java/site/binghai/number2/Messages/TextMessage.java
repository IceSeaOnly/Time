package site.binghai.number2.Messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;

import site.binghai.number2.R;
import site.binghai.number2.Utils.DateUtil;
import site.binghai.number2.Utils.DpOrSp2PxUtil;
import site.binghai.number2.Utils.TimeFormater;

/**
 * Created by Administrator on 2016/9/26.
 */
public class TextMessage implements MessageEntityImpl {
    private Long id;
    private String content;
    private String date;
    private String avatar;
    private boolean mtype; // 区分谁发的，“我”的为false
    private Long timestamp;
    private Long friendid;

    public TextMessage(Long id, String content, String date, String avatar, boolean mtype, Long timestamp, Long friendId) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.avatar = avatar;
        this.mtype = mtype;
        this.timestamp = timestamp;
        this.friendid = friendId;
    }

    @Override
    public String getTime() {
        return date;
    }

    @Override
    public int getMessageTye() {
        return MessageEntityImpl.TEXT_MSG;
    }

    @Override
    public String getAvatarUrl() {
        return avatar;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTextMsg() {
        return content;
    }

    @Override
    public String getPicMsgUrl() {
        return "";
    }

    @Override
    public boolean isMyMsg() {
        return !mtype;
    }

    @Override
    public boolean isFarFromNow() {
        return DateUtil.far_from_now(timestamp);
    }

    @Override
    public Long getFriendId() {
        return friendid;
    }

    @Override
    public Long getTimeStamp() {
        return timestamp;
    }


    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView, int position) {
        TextMessageHolder vs = null;
        if (convertView == null || ((MessageHolderImpl) convertView.getTag()).getMsgType() != MessageEntityImpl.TEXT_MSG) {
            convertView = isMyMsg() ? inflater.inflate(R.layout.chatroom_message_right, null) : inflater.inflate(R.layout.chatroom_message_left, null);
            vs = new TextMessageHolder(convertView, mtype);
            convertView.setTag(vs);
        } else {
            vs = (TextMessageHolder) convertView.getTag();
            if (vs.isMyMsg() != isMyMsg()) {
                convertView = isMyMsg() ? inflater.inflate(R.layout.chatroom_message_right, null) : inflater.inflate(R.layout.chatroom_message_left, null);
                vs = new TextMessageHolder(convertView, mtype);
                convertView.setTag(vs);
            }
        }

        if (isFarFromNow()) {
            vs.msg_date.setVisibility(View.VISIBLE);
            vs.msg_date.setText(TimeFormater.getSuitableDateTime(getTimeStamp()));
        } else {
            /**
             * 隐藏时间
             * */
            vs.msg_date.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(getAvatarUrl())
                .centerCrop()
                .placeholder(R.drawable.default_avatar)
                .into(vs.avatar);

        vs.msg_content.setText(getTextMsg());
        vs.setEmojiSize(DpOrSp2PxUtil.dp2pxConvertInt(context, 25));
        return convertView;
    }
}
