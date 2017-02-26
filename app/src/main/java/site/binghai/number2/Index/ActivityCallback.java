package site.binghai.number2.Index;

/**
 * Created by Administrator on 2016/10/23.
 * 用于一个activity回调另一个
 */
public interface ActivityCallback {
    public void onError(String msg);
    public void onSuccess(String msg,Long dynamicId);
}
