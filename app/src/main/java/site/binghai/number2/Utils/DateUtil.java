package site.binghai.number2.Utils;

/**
 * Created by Administrator on 2016/9/26.
 */
public class DateUtil {
    public static boolean far_from_now(Long time){
        Long now = System.currentTimeMillis();
        return now-time>300000;
    }
}
