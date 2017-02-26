package site.binghai.number2.Utils;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.impl.FileUploadTask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/9/11.
 */
public class FileUtil {
    public static boolean isFolderExists(String strFolder)
    {
        File file = new File(strFolder);

        if (!file.exists())
        {
            if (file.mkdir())
            {
                return true;
            }
            else
                return false;
        }
        return true;
    }

    /**
     * 获取OSS SIGN
     * */
    public static void VoiceUpload(final Context context, final File mAudioFile, final AfterUpload afterupload) {
        String uriAPI = Config.VOICE_SIGN_SERVER+"?fileid="+mAudioFile.getName();
        Log.i("FileId = ",mAudioFile.getName());

        HttpUtil.get(uriAPI, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String SIGN = new String(responseBody);
                Log.i("获取voice上传SIGN",SIGN);
                uploadVoiceFile(context,mAudioFile,SIGN,afterupload);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("获取voice上传SIGN","失败");
            }
        });
    }

    /**
     * 上传语音到 OSS
     * */
    public static void uploadVoiceFile(Context context, File mAudioFile, String SIGN, final AfterUpload afterupload){
        UploadManager fileUploadMgr = null;
        fileUploadMgr = new UploadManager(context, "10066161", Const.FileType.File,null);
        FileUploadTask task = new FileUploadTask(
                "timevoice",
                mAudioFile.getAbsolutePath(),
                "/voices/"+mAudioFile.getName(),
                "voices",
                true,
                new IUploadTaskListener(){
            @Override
            public void onUploadFailed(int errorCode, String errorMsg) {
                Log.i("语音上传","失败："+errorMsg);
            }

            @Override
            public void onUploadProgress(long totalSize, long sendSize) {
                afterupload.uploadProgress(totalSize,sendSize);
                Log.i("语音上传", String.valueOf((int)sendSize/totalSize)+"%");
            }

            @Override
            public void onUploadStateChange(ITask.TaskState state) {
                // 上传状态变化
            }

            @Override
            public void onUploadSucceed(FileInfo result) {
                Log.i("语音上传","成功");
                afterupload.afterUpload(result.url);
            }});
        task.setAuth(SIGN);
        fileUploadMgr.upload(task);
    }


}
