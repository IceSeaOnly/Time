package site.binghai.number2.Messages;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/26.
 */
public class MessageAdapter extends BaseAdapter {
    private ArrayList<MessageEntityImpl> msgs;
    private Context context;
    private LayoutInflater layoutInflater;
    private Activity activity;

    public MessageAdapter(ArrayList<MessageEntityImpl> msgs, Activity activity) {
        this.msgs = msgs;
        this.activity = activity;
        context = activity;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public MessageEntityImpl getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return msgs.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return msgs.get(position).getView(context,layoutInflater,convertView,position);
    }
}
