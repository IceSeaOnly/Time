package site.binghai.number2.Messages;

/**
 * Created by Administrator on 2016/9/27.
 */
public interface MessageHolderImpl {
    int getMsgType();
    boolean isMyMsg();
    boolean setEmojiSize(int pxSize);
}
