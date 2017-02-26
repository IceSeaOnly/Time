package site.binghai.number2.Utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * Created by Administrator on 2016/9/11.
 */
public class VibratorUtil {
    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity
                .getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
}
