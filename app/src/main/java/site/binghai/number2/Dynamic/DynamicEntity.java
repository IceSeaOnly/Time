package site.binghai.number2.Dynamic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Administrator on 2016/9/5.
 */
public interface DynamicEntity {
    // 返回值为 0 的时候为推荐用户的对象
    // 返回值为 1 的时候为用户的动态信息
    // 返回值为 2 的时候为广告推广
    public int getEntityType();
    public String getName();
    public Long getId();
    public Object getData();
    public View makeConvertView(int position, View convertView, LayoutInflater mInflater, Activity activity, DynamicActionCallBack callBack);
}
