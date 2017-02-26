package site.binghai.number2.ShowYourLove;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;

import site.binghai.number2.Common.CommentOpt;
import site.binghai.number2.Common.NetBGLoader;
import site.binghai.number2.Common.UpdateCallBack;
import site.binghai.number2.R;
import site.binghai.number2.Utils.TimeFormater;

/**
 * Created by Administrator on 2016/11/18.
 */
public class LoveAdapter extends BaseAdapter {
    private ArrayList<ShowYourLoveEntity>entities;
    private Activity activity;
    private LayoutInflater inflater;

    public LoveAdapter(ArrayList<ShowYourLoveEntity> entities, Activity activity) {
        this.entities = entities;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public ShowYourLoveEntity getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return entities.get(position).getId();
    }

    class ViewHolder{
        ImageView love_avatar;
        TextView love_username;
        TextView love_posttime;
        TextView main_text;
        ShineButton love_good;
        ImageView love_to_comment;
        TextView love_goodSum;
        TextView love_commentSum;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.show_your_love_entity,null);
            vh.love_avatar = (ImageView) convertView.findViewById(R.id.love_avatar);
            vh.love_username = (TextView) convertView.findViewById(R.id.love_username);
            vh.love_posttime = (TextView) convertView.findViewById(R.id.love_posttime);
            vh.main_text = (TextView) convertView.findViewById(R.id.main_text);
            vh.love_good = (ShineButton) convertView.findViewById(R.id.love_good);
            vh.love_to_comment = (ImageView) convertView.findViewById(R.id.love_to_comment);
            vh.love_goodSum = (TextView) convertView.findViewById(R.id.love_goodSum);
            vh.love_commentSum = (TextView) convertView.findViewById(R.id.love_commentSum);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        if(getItem(position).getBg().length()>5){
            new NetBGLoader(activity,getItem(position).getBg(),convertView).load();
        }else{
            convertView.setBackgroundResource(R.color.white);
        }
        Glide.with(activity)
                .load(getItem(position).getAvatarImg())
                .centerCrop()
                .placeholder(R.drawable.default_welcome)
                .into(vh.love_avatar);

        vh.love_username.setText(getItem(position).isNameMask()?"匿名":getItem(position).getUsername());
        vh.love_posttime.setText(TimeFormater.getSuitableDateTime(getItem(position).getPostTime()));
        vh.main_text.setText(getItem(position).getText());
        vh.love_goodSum.setText(String.valueOf(getItem(position).getGoodNumber()));
        vh.love_commentSum.setText(String.valueOf(getItem(position).getCommentNumber()));
        vh.love_good.setChecked(getItem(position).isIlike());
        vh.love_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I_like(getItemId(position));
            }
        });
        vh.love_to_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I_comment(getItemId(position));
            }
        });
        return convertView;
    }

    private void I_comment(long itemId) {
    }

    private void I_like(long itemId) {
        new CommentOpt().PeiZanOpt(true,0,itemId,"showyourlove");
        updateCallBack.onUpdate(itemId);
    }

    private UpdateCallBack updateCallBack;

    public void setUpdateCallBack(UpdateCallBack updateCallBack) {
        this.updateCallBack = updateCallBack;
    }
}
