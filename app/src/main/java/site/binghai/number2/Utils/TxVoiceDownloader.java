package site.binghai.number2.Utils;

import android.content.Context;
import android.util.Log;

import com.tencent.download.Downloader;
import com.tencent.download.Downloader.DownloadListener;
import com.tencent.download.core.DownloadResult;

/**
 * Created by Administrator on 2016/9/19.
 * 腾讯云下载器
 */
public class TxVoiceDownloader {
    public void download(final Context context, String url, final AfterDownload afterDownload){
        final Downloader downloader = new Downloader(context, "10066161",null);
        downloader.enableHTTPRange(true);
        downloader.enableKeepAlive(true);

        DownloadListener listener = new DownloadListener() {
            @Override
            public void onDownloadCanceled(String s) {
                afterDownload.afterDownload(false);
            }

            @Override
            public void onDownloadFailed(String s, DownloadResult downloadResult) {
                afterDownload.afterDownload(false);
            }

            @Override
            public void onDownloadSucceed(String s, DownloadResult downloadResult) {
                Log.i("语音下载完毕",downloadResult.getPath());
                afterDownload.afterDownLoad(downloadResult.getPath());
            }

            @Override
            public void onDownloadProgress(String s, long l, float v) {
                afterDownload.downloadProgress(s,l,v);
            }
        };
        downloader.download(url,listener);
    }

}
