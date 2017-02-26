package site.binghai.number2.Dynamic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/1.
 * 推荐用户适配器
 */
public class DynamicAdapter extends BaseAdapter{

    private ArrayList<DynamicEntity> entities;
    private Activity activity;
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private DynamicActionCallBack callBack;

    public DynamicAdapter(ArrayList<DynamicEntity> entities, Activity activity,DynamicActionCallBack callBack) {
        this.entities = entities;
        this.activity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.callBack = callBack;
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return entities.get(position).getId();
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        return entities.get(position).makeConvertView(position,convertView,mInflater,activity, callBack);
    }
}
