package site.binghai.number2.Messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Administrator on 2016/9/26.
 */
public interface MessageEntityImpl {
    public static int TEXT_MSG = 0;
    public static int PIC_MSG = 1;
    public static int SYS_MSG = 2;

    String getTime();
    int getMessageTye();
    String getAvatarUrl();
    Long getId();
    String getTextMsg();
    String getPicMsgUrl();
    boolean isMyMsg();
    boolean isFarFromNow();
    Long getFriendId();
    Long getTimeStamp();
    View getView(Context context, LayoutInflater inflater,View convertView,int position);
}
