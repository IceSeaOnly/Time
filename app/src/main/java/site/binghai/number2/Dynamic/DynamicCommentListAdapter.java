package site.binghai.number2.Dynamic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

import site.binghai.number2.Common.Comment;
import site.binghai.number2.R;

/**
 * Created by Administrator on 2016/9/6.
 */
public class DynamicCommentListAdapter extends BaseAdapter {
    private ArrayList<Comment> cms;
    private Context context;
    private LayoutInflater mInflater;

    public DynamicCommentListAdapter(ArrayList<Comment> cms,Context context) {
        this.cms = cms;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cms.size();
    }

    @Override
    public Object getItem(int position) {
        return cms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ListViewItem{
        public TextView dynamic_comments_list_item_username;
        public TextView dynamic_comments_list_item_comment;
        public TextView reply_type;
        public TextView reply_name;
        public ImageView voice_flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewItem lvi = new ListViewItem();
            convertView = mInflater.inflate(R.layout.dynamic_comments_item,null);
            lvi.dynamic_comments_list_item_username =
                    (TextView) convertView.findViewById(R.id.dynamic_comments_list_item_username);
            lvi.dynamic_comments_list_item_comment =
                    (TextView) convertView.findViewById(R.id.dynamic_comments_list_item_comment);
            lvi.reply_type = (TextView) convertView.findViewById(R.id.reply_type);
            lvi.voice_flag = (ImageView) convertView.findViewById(R.id.voice_flag);
        lvi.reply_name = (TextView) convertView.findViewById(R.id.dynamic_comments_list_item_replay_username_reply);


        String sn = cms.get(position).getUsername();
        JSONObject obj = JSON.parseObject(sn);
        lvi.dynamic_comments_list_item_username.setText(obj.getString("postername"));
        if(obj.getBoolean("single")){
            lvi.reply_name.setText("");
            lvi.reply_type.setText("");
        }
        else{
            lvi.reply_name.setText(obj.getString("replyname"));
            lvi.reply_type.setText("回复");
        }
        if(cms.get(position).getCtype() == Comment.VOICE_COMMENT){
            lvi.dynamic_comments_list_item_comment.setText("   "+cms.get(position).getVoice_long()+"'");
        }else{
            lvi.voice_flag.setVisibility(View.GONE);
            lvi.dynamic_comments_list_item_comment.setText(cms.get(position).getComments());
        }
        return convertView;
    }
}
