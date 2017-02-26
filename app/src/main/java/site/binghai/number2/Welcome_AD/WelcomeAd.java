package site.binghai.number2.Welcome_AD;

import android.app.Activity;
import android.os.Bundle;

import android.view.Window;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;
import site.binghai.number2.R;
import site.binghai.number2.Utils.PictureUtil;

public class WelcomeAd extends Activity {

    /** 在一次app生命周期内只显示一次*/
    public static boolean init = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_ad_begin);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 5000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.gc();
        finish();
    }
}
