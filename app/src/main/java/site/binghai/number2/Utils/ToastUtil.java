package site.binghai.number2.Utils;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;
import site.binghai.number2.Common.UpdateCallBack;

/**
 * Created by Administrator on 2016/9/20.
 */
public class ToastUtil {
    private Context context;

    public ToastUtil(Context context) {
        this.context = context;
    }

    public void errorNotice(String errTitle, String errContent){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(errTitle)
                .setContentText(errContent)
                .show();
    }

    public void successNotice(String successTitle,String successContent){
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(successTitle)
                .setContentText(successContent)
                .show();
    }

    public void unDefinedErrro(){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("未定义")
                .setContentText("动作未定义")
                .show();
    }
    public void warningNotice(String title, String desc, String cancel, String confirm, final String cTitle, final String cInfo, final String cButton, final UpdateCallBack callBack){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(desc)
                .setCancelText(cancel)
                .setConfirmText(confirm)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog
                                .showCancelButton(false)
                                .setTitleText(cTitle)
                                .setContentText(cInfo)
                                .setConfirmText(cButton)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        callBack.onUpdate(null);
                    }
                })
                .show();
    }
}
