package site.binghai.number2.Messages;

/**
 * Created by Administrator on 2016/10/10.
 */
public interface AfterRefreshFriendList {
    void onFreshSuccess();
    void onFreshFailed(String msg);
}
