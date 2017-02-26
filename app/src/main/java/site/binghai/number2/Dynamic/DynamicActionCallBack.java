package site.binghai.number2.Dynamic;

/**
 * Created by Administrator on 2016/11/28.
 */
public interface DynamicActionCallBack {
    void postComment(Long dynamicId);

    void DataChanged();

    void postReplyComment(Long dynamicId, Long replyid, String replyname);

    void playVoiceCall(String url);
}
