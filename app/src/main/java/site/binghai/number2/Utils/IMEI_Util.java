package site.binghai.number2.Utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Administrator on 2016/9/22.
 */
public class IMEI_Util {
    public static String getIMEI(Context context){
        TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);
        return TelephonyMgr.getDeviceId();
    }
}
