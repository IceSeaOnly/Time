package site.binghai.number2.Messages;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maplejaw.library.bean.EmojiconTextView;
import site.binghai.number2.R;

/**
 * Created by Administrator on 2016/9/27.
 */
public class TextMessageHolder implements MessageHolderImpl {
    TextView msg_date;
    EmojiconTextView msg_content;
    ImageView avatar;
    boolean mine;

    public TextMessageHolder(View convertView,boolean mine) {
        this.mine = mine;
        avatar = (ImageView) (isMyMsg()?convertView.findViewById(R.id.chatroom_my_avatar):convertView.findViewById(R.id.chatroom_his_avatar));
        msg_content = (EmojiconTextView) (isMyMsg()?convertView.findViewById(R.id.chatroom_my_msg):convertView.findViewById(R.id.chatroom_his_msg));
        msg_date = (TextView) convertView.findViewById(R.id.chatroom_time_betweent_msg);
    }

    @Override
    public int getMsgType() {
        return MessageEntityImpl.TEXT_MSG;
    }

    @Override
    public boolean isMyMsg() {
        return !mine;
    }

    @Override
    public boolean setEmojiSize(int pxSize) {
        msg_content.setEmojiconSize(pxSize+5);
        msg_content.setTextSize(17);
        return false;
    }
}
