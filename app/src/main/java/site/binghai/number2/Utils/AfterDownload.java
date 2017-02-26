package site.binghai.number2.Utils;

/**
 * Created by Administrator on 2016/9/19.
 */
public interface AfterDownload {
    public void afterDownLoad(Object info);
    public void afterDownload(boolean result);
    public void downloadProgress(String url, long totalSize, float progress);
}
