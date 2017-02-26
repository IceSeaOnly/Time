package site.binghai.number2.LoginLogout;

/**
 * Created by Administrator on 2016/9/20.
 */
public interface LoginResult {
    public void onLoginFailed(String msg);
    public void onNetFailed();
    public void onLoginSuccess();
    public void onSystemError();
}
