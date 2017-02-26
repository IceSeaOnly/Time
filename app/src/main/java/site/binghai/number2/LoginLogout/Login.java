package site.binghai.number2.LoginLogout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import cn.pedant.SweetAlert.SweetAlertDialog;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.R;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.MD5;
import site.binghai.number2.Utils.ToastUtil;
import site.binghai.number2.WebView.WebViewActivity;
import site.binghai.number2.Welcome_AD.WelcomeAd;

public class Login extends Activity {

    private EditText phone;
    private EditText password;
    private TextView forgot_password;
    private Button btn_login;
    private Button btn_reg;
    private RelativeLayout bottom_part;
    private SweetAlertDialog pDialog = null;

    /**
     * 记住密码存储
     */
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitView();
        InitAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** 首个生命周期*/
        if(!WelcomeAd.init){
            WelcomeAd.init = true;
            startActivity(new Intent(Login.this,WelcomeAd.class));
        }else{
            baseLogic();
        }
    }

    private void baseLogic(){
        String info = getIntent().getStringExtra("info");
        if (info != null) {
            new ToastUtil(this).successNotice("提示", info);
        }

        /** 存储用户信息的实例，仅供本程序读写*/
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        /** 如果用，自动填入用户名*/
        phone.setText(sp.getString("USERNAME", ""));

        if (info != null)
            if (info.contains("被迫下线")){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("PASSWORD", "");
                editor.commit();
            }

        /** 自动登录设置*/
        if (sp.getString("PASSWORD", "").length() > 1) {
            password.setText(sp.getString("PASSWORD", ""));
            doLogin();
        }


    }
    private void InitAction() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().length() < 1 || password.getText().toString().length() < 1) {
                    new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("啊哦...")
                            .setContentText("用户名/密码不可为空!")
                            .show();
                } else {
                    password.setText(MD5.encryption(password.getText().toString()));
                    doLogin();
                }
            }
        });

        /** 注册逻辑*/
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, WebViewActivity.class);
                i.putExtra("url", Config.RegServer + Config.randomSign());
                startActivityForResult(i, 0);
            }
        });
    }

    /**
     * 登录逻辑
     */

    private void doLogin() {
        onProgressing();
        final String u = phone.getText().toString();
        final String p = password.getText().toString();
        password.setText("");
        AccountUtil.Login(Login.this, u, p, new LoginResult() {
            @Override
            public void onLoginFailed(String msg) {
                pDismiss();
                new ToastUtil(Login.this).errorNotice("登录失败", msg);
            }

            @Override
            public void onNetFailed() {
                pDismiss();
                new ToastUtil(Login.this).errorNotice("登录失败", "请检查网络是否正常!");
            }

            @Override
            public void onLoginSuccess() {
                pDismiss();
                /** 登录成功，保存信息*/
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USERNAME", u);
                editor.putString("PASSWORD", p);
                editor.commit();
                finish();
            }

            @Override
            public void onSystemError() {
                pDismiss();
                new ToastUtil(Login.this).errorNotice("登录失败", "系统错误");
            }
        });
    }

    private void pDismiss() {
        pDialog.dismiss();
    }

    public void onProgressing() {
        pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在登录...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            new ToastUtil(this).errorNotice("出错了", data.getStringExtra("msg"));
        }
    }

    private void InitView() {
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.login);
        btn_reg = (Button) findViewById(R.id.register);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        bottom_part = (RelativeLayout) findViewById(R.id.bottom_part);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) pDismiss();
    }

    private int presse_time = 0;

    @Override
    public void onBackPressed() {
        if (presse_time++ > 0) {
            exitApp = true;
            finish();
        } else {
            Toast.makeText(Login.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean exitApp = false;
}
