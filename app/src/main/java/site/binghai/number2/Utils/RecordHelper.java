package site.binghai.number2.Utils;


import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.piasy.rxandroidaudio.AudioRecorder;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import me.shaohui.bottomdialog.BaseBottomDialog;
import site.binghai.number2.R;

/**
 * Created by Administrator on 2016/11/13.
 */
public class RecordHelper extends BaseBottomDialog {

    private ImageView record_help_button;
    private AfterUpload afterupload;
    private float fx;

    /**
     * 记录手指的x坐标
     */

    @Override
    public int getLayoutRes() {
        return R.layout.record_help;
    }

    @Override
    public void bindView(View v) {
        record_help_button = (ImageView) v.findViewById(R.id.record_help_button);
        InitAction();
    }

    private void InitAction() {
        record_help_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dismiss();
                    if (Math.abs(event.getX() - fx) > 10)
                        return stop_recording(2);
                    return stop_recording(0);
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    record_second = 0;
                    Long f_s = 0L;
                    Long f_e = 0L;
                    fx = event.getX();

                    if (mAudioRecorder == null) {
                        mAudioRecorder = AudioRecorder.getInstance();
                        if (FileUtil.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Time")) {
                            VibratorUtil.Vibrate(getActivity(), 150);
                            record_start = System.currentTimeMillis();

                            /**
                             * 生成文件名
                             * */
                            record_fileName = MD5.encryption(System.currentTimeMillis() + IMEI_Util.getIMEI(getContext())) + ".voice";

                            mAudioFile = new File(
                                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Time" +
                                            File.separator + record_fileName);
                            mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                                    MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                                    mAudioFile);
                            f_s = System.currentTimeMillis();
                            mAudioRecorder.startRecord();
                        }
                        f_e = System.currentTimeMillis();

                        /**
                         * 此处代码用来判断录音权限是否自动赋权，如果不是则不能录音
                         * */
                        if (f_e - f_s > 1000) {
                            stop_recording(1);
                            new ToastUtil(getContext()).successNotice("温馨提示", "为了软件更好的工作，请确认您已经永久允许我们的软件使用录音功能。");
                            return true;
                        }
                        /**
                         * 提示录音时间
                         * */
                        recordingToast = new LoadToast(getActivity());
                        recordingToast.setTranslationY(200);
                        recordingToast.setText("00:00:00 ").show();
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
                }
                return true;
            }
        });
    }

    private File mAudioFile;
    private long record_start = 0;
    private long record_stop = 0;
    private LoadToast recordingToast;
    private AudioRecorder mAudioRecorder;
    private String record_fileName;
    private int record_second = 0; // 录音时长
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int sec = msg.arg1;
            recordingToast.setText(String.format("%02d", sec / 3600) + ":" + String.format("%02d", sec / 60) + ":" + String.format("%02d", sec % 60) + " ");
        }
    };


    private boolean stop_recording(int model) {

        if (mAudioRecorder != null) {
            mAudioRecorder.stopRecord();
            mAudioRecorder = null;
        }

        if (model == 1) return true;


        mTimerTask.cancel();

        if (model == 2) {
            recordingToast.error();
            Toast.makeText(getContext(), "已取消", Toast.LENGTH_SHORT).show();
            return true;
        }

        record_stop = System.currentTimeMillis();
        if (record_stop - record_start < 1000) {
            Toast.makeText(getContext(), "录制时间太短", Toast.LENGTH_SHORT).show();
            recordingToast.error();
            return true;
        }
        recordingToast.success();
        upLoadRecordFile(mAudioFile);
        return true;
    }

    /**
     * 上传录音文件
     */
    private void upLoadRecordFile(File mAudioFile) {
        FileUtil.VoiceUpload(getContext(), mAudioFile, afterupload);
    }

    public AfterUpload getAfterupload() {
        return afterupload;
    }

    public void setAfterupload(AfterUpload afterupload) {
        this.afterupload = afterupload;
    }

    public int getVoiceLenth() {
        return record_second;
    }
}
