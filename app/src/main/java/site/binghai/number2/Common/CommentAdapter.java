package site.binghai.number2.Common;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.List;

import site.binghai.number2.R;
import site.binghai.number2.Utils.TimeFormater;
import site.binghai.number2.Utils.VoicePlayer;

/**
 * Created by Administrator on 2016/12/5.
 */
public class CommentAdapter extends BaseAdapter{
    private List<Comment> cms;
    private Activity activity;
    private LayoutInflater inflater;

    private String TAG = "CommentAdapter";
    public CommentAdapter(List<Comment> cms, Activity activity) {
        this.cms = cms;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return cms.size();
    }

    @Override
    public Comment getItem(int position) {
        return cms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    class VH{
        ImageView avatar;
        TextView username;
        TextView reply_username;
        TextView reply_flag;
        TextView time;
        TextView text;
        TextView voice;
        TextView cm_floor;
    }
    /** 语音、文字同一个adapter*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VH vh = null;
        if(convertView == null){
            vh = new VH();
            convertView = inflater.inflate(R.layout.common_comments_item,null);
            vh.avatar = (ImageView) convertView.findViewById(R.id.cm_avatar);
            vh.username = (TextView) convertView.findViewById(R.id.cm_username);
            vh.reply_username = (TextView) convertView.findViewById(R.id.cm_reply_username);
            vh.reply_flag = (TextView) convertView.findViewById(R.id.cm_reply_flag);
            vh.time = (TextView) convertView.findViewById(R.id.cm_posttime);
            vh.text = (TextView) convertView.findViewById(R.id.cm_text);
            vh.voice = (TextView) convertView.findViewById(R.id.cm_voice);
            vh.cm_floor = (TextView) convertView.findViewById(R.id.cm_floor);
            convertView.setTag(vh);
        }else{
            vh = (VH) convertView.getTag();
        }

        vh.cm_floor.setText((position+1)+"楼");
        com.alibaba.fastjson.JSONObject jname = com.alibaba.fastjson.JSONObject.parseObject(getItem(position).getUsername());
        vh.username.setText(jname.getString("postername"));
        if(jname.getBoolean("single")){
            vh.reply_flag.setText("");
            vh.reply_username.setText("");
        }else{
            vh.reply_flag.setText("回复");
            vh.reply_username.setText(jname.getString("replyname"));
        }

        vh.time.setText(TimeFormater.getSuitableDateTime(getItem(position).getCommitTime()));

        if(getItem(position).getCtype() == Comment.TEXT_COMMENT){
            vh.text.setVisibility(View.VISIBLE);
            vh.voice.setVisibility(View.GONE);
            vh.text.setText(getItem(position).getComments());
        }else{
            vh.text.setVisibility(View.GONE);
            vh.voice.setVisibility(View.VISIBLE);
            vh.voice.setText(SuitableLength(getItem(position).getVoice_long()));
        }
        vh.voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItem(position).getVoiceUrl() != null && getItem(position).getVoiceUrl().length()>0)
                    new VoicePlayer(getItem(position).getVoiceUrl(),activity).play();
            }
        });

        Glide.with(activity)
                .load(getItem(position).getAvatar())
                .centerCrop()
                .placeholder(R.drawable.default_avatar)
                .into(vh.avatar);
        return convertView;
    }

    private String SuitableLength(int voice_long) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < voice_long%10;i++){
            sb.append(" ");
        }
        return sb.append(voice_long).toString();
    }
}
