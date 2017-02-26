package site.binghai.number2.Utils;

import android.content.Context;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/9/6.
 */
public class ScreenUtil {
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

}
