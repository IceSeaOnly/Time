package site.binghai.number2.Utils;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/11/26.
 */
public class Process {

    private static SweetAlertDialog pDialog;
    public static void onProcessing(Context context, String s, boolean b) {
        if (pDialog != null) dismiss();
        pDialog= new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(s);
        pDialog.setCancelable(b);
        pDialog.show();
    }

    public static void dismiss() {
        if (pDialog != null){
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
