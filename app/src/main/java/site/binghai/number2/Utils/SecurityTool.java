package site.binghai.number2.Utils;

import android.content.Context;

import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.alibaba.wireless.security.open.simulatordetect.ISimulatorDetectComponent;

/**
 * Created by Administrator on 2016/9/20.
 */
public class SecurityTool {

    /** 判断是否是模拟器环境，需传入ApplicationContext*/
    public static boolean isSimulator(Context applicationContext) {

        boolean bRet = false;
        SecurityGuardManager manager =null;
        try{
            manager = SecurityGuardManager.getInstance(applicationContext);
            ISimulatorDetectComponent simulatorDetectComp = manager.getSimulatorDetectComp();
            bRet = simulatorDetectComp.isSimulator();
        }catch(SecException e) {
            e.printStackTrace();
        }
        return bRet;
    }
    /**
     * 解密
     *
     * @param ssoToken 字符串
     * @return String 返回加密字符串
     */
    public static String decrypt(String ssoToken) {
        try {
            String name = new String();
            java.util.StringTokenizer st = new java.util.StringTokenizer(ssoToken, "%");
            while (st.hasMoreElements()) {
                int asc = Integer.parseInt((String) st.nextElement()) - 27;
                name = name + (char) asc;
            }

            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密
     *
     * @param ssoToken 字符串
     * @return String 返回加密字符串
     */
    public static String encrypt(String ssoToken) {
        try {
            byte[] _ssoToken = ssoToken.getBytes("ISO-8859-1");
            String name = new String();
            // char[] _ssoToken = ssoToken.toCharArray();
            for (int i = 0; i < _ssoToken.length; i++) {
                int asc = _ssoToken[i];
                _ssoToken[i] = (byte) (asc + 27);
                name = name + (asc + 27) + "%";
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
