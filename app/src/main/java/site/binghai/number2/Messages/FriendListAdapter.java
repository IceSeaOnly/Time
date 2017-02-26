package site.binghai.number2.Messages;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maplejaw.library.bean.EmojiconTextView;

import java.util.ArrayList;

import site.binghai.number2.R;
import site.binghai.number2.Utils.DpOrSp2PxUtil;


/**
 * Created by Administrator on 2016/9/24.
 */
public class FriendListAdapter extends BaseAdapter {

    private ArrayList<Friend> friends;
    private Context context;
    private LayoutInflater layoutInflater;

    public FriendListAdapter(ArrayList<Friend> friends, Context context) {
        this.friends = friends;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friends.get(position).getFriendid();
    }

    private class ViewClass{
        ImageView friend_avatar;
        TextView friend_name;
        EmojiconTextView chat_message;
        TextView chat_time;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewClass vc = null;
        if(convertView == null){
            vc = new ViewClass();
            convertView = layoutInflater.inflate(R.layout.message_chatlist_item,null);
            vc.friend_avatar = (ImageView) convertView.findViewById(R.id.friend_avatar);
            vc.friend_name = (TextView) convertView.findViewById(R.id.friend_name);
            vc.chat_message = (EmojiconTextView) convertView.findViewById(R.id.chat_message);
            vc.chat_time = (TextView) convertView.findViewById(R.id.chat_time);

            convertView.setTag(vc);
        }else{
            vc = (ViewClass) convertView.getTag();
        }

        Glide.with(context.getApplicationContext())
                .load(friends.get(position).getAvatar())
                .centerCrop()
                .placeholder(R.drawable.default_avatar)
                .into(vc.friend_avatar);

        vc.friend_name.setText(friends.get(position).getName());

        vc.chat_message.setText(friends.get(position).getCur_message());
        vc.chat_message.setEmojiconSize(DpOrSp2PxUtil.dp2pxConvertInt(context,20));

        vc.chat_time.setText(friends.get(position).getCur_time());

        return convertView;
    }
}
