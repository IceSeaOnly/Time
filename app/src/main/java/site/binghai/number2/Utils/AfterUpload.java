package site.binghai.number2.Utils;

/**
 * Created by Administrator on 2016/9/18.
 */
public interface AfterUpload {
    public void afterUpload(String url);
    public void uploadProgress(long total, long send);
}
