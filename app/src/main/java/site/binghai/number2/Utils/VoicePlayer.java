package site.binghai.number2.Utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/11.
 */
public class VoicePlayer {
    private String url;
    private static String last_url;
    private Context context;
    private static RxAudioPlayer mRxAudioPlayer = null;
    private LoadToast recordingToast;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private int record_second = 0;

    public VoicePlayer(String url, Context context) {
        last_url = this.url;
        this.url = url;
        this.context = context;
    }

    public void play(){
        if(last_url != null){
            if(last_url.equals(url) && mRxAudioPlayer.progress() < 100){
                stop.sendMessage(new Message());
                return;
            }
        }
        downloadFile(url);
//        if (FileUtil.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Time")) {
//
//            File mAudioFile = new File(
//                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Time" +
//                            File.separator + url);
//
//            if(mAudioFile != null){
//                playFile(mAudioFile);
//            }else{
//
//            }
//        }
    }

    private void downloadFile(String url) {
        Toast.makeText(context,"即将播放...",Toast.LENGTH_SHORT).show();
        start();
        TxVoiceDownloader downloader = new TxVoiceDownloader();
        downloader.download(context, url, new AfterDownload() {
            @Override
            public void afterDownLoad(Object info) {
                playFile(new File((String) info));
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.arg1 = record_second++;
                        mHandler.handleMessage(message);
                    }
                };
                mTimer = new Timer();
                mTimer.schedule(mTimerTask, 0, 1000);
            }

            @Override
            public void afterDownload(boolean result) {
                if(!result)
                    Toast.makeText(context,"语音缓冲失败，请检查网络...",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void downloadProgress(String url, long totalSize, float progress) {}
        });
    }

    public void playFile(File mAudioFile){
        if(mRxAudioPlayer != null){
            stop.sendMessage(new Message());
        }else
            mRxAudioPlayer = RxAudioPlayer.getInstance();
        
        mRxAudioPlayer.play(PlayConfig.file(mAudioFile).looping(false).build())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        stop.sendMessage(new Message());
                    }
                });
    }
    public void stopPlay(){
        if(mRxAudioPlayer != null) mRxAudioPlayer.stopPlay();
        mRxAudioPlayer = null;
        recordingToast.success();
        record_second = 0;
    }

   private void start(){
       recordingToast = new LoadToast(context);
       recordingToast.setTranslationY(200);
       recordingToast.setText("00:00:00 ").show();
   }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int sec = msg.arg1;
            recordingToast.setText(String.format("%02d", sec / 3600) + ":" + String.format("%02d", sec / 60) + ":" + String.format("%02d", sec % 60) + " ");
        }
    };

    private Handler stop = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopPlay();
        }
    };
}
