package site.binghai.number2.Dynamic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;

import site.binghai.number2.AccountUtil;
import site.binghai.number2.Common.Comment;
import site.binghai.number2.Common.CommentOpt;
import site.binghai.number2.R;
import site.binghai.number2.Utils.MyListView;
import site.binghai.number2.Utils.ScreenUtil;
import site.binghai.number2.Utils.SetListViewHeight;
import site.binghai.number2.Utils.TimeFormater;
import site.binghai.number2.Utils.VoicePlayer;

/**
 * Created by Administrator on 2016/9/6.
 * 用户动态类
 */
public class DynamicContentEntity implements DynamicEntity {
    @Override
    public int getEntityType() {
        return 1;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Object getData() {
        return null;
    }

    private Long id;
    private String avatar_url;
    private String username;
    private String main_pic_url;
    private String text_for_pic;
    private Long comments;
    private Long good;
    private Long pei;
    private boolean pei_checked_state;
    private boolean good_checked_state;
    private ArrayList<Comment> allComments;
    private Long postTime;
    private Long pDelteTime;

    public DynamicContentEntity(Long id, String avatar_url, String username, String main_pic_url, String text_for_pic, Long comments, Long good, Long pei, boolean pei_checked_state, boolean good_checked_state, ArrayList<Comment> cms) {
        this.id = id;
        this.avatar_url = avatar_url;
        this.username = username;
        this.main_pic_url = main_pic_url;
        this.text_for_pic = text_for_pic;
        this.comments = comments;
        this.good = good;
        this.pei = pei;
        this.pei_checked_state = pei_checked_state;
        this.good_checked_state = good_checked_state;
        this.allComments = cms;
    }

    class ContentView {
        TextView dynmic_time;
        ImageView dynamic_avatar;
        TextView dynamic_username;
        ImageView dynamic_pic;
        ShineButton dynamic_say_pei;
        ShineButton dynamic_say_good;
        ImageView dynamic_to_comments;
        TextView dynamic_text_for_pic;
        TextView dynamic_comments_good_pei_number;
        MyListView dynamic_comments_list;
        ImageView voice_flag;
    }


    @Override
    public View makeConvertView(int position, View convertView,
                                LayoutInflater mInflater, final Activity activity,
                                final DynamicActionCallBack callBack) {
        ContentView cv = null;
        if (convertView == null || !convertView.getTag().getClass().isInstance(DynamicContentEntity.class)) {
            cv = new ContentView();
            convertView = mInflater.inflate(R.layout.dynamic_dynamic, null);
            cv.voice_flag = (ImageView) convertView.findViewById(R.id.voice_flag);
            cv.dynmic_time = (TextView) convertView.findViewById(R.id.dynmic_time);
            cv.dynamic_avatar = (ImageView) convertView.findViewById(R.id.dynamic_avatar);
            cv.dynamic_username = (TextView) convertView.findViewById(R.id.dynamic_username);
            cv.dynamic_pic = (ImageView) convertView.findViewById(R.id.dynamic_pic);
            cv.dynamic_say_pei = (ShineButton) convertView.findViewById(R.id.dynamic_say_pei);
            cv.dynamic_say_good = (ShineButton) convertView.findViewById(R.id.dynamic_say_good);
            cv.dynamic_to_comments = (ImageView) convertView.findViewById(R.id.dynamic_to_comments);
            cv.dynamic_text_for_pic = (TextView) convertView.findViewById(R.id.dynamic_text_for_pic);
            cv.dynamic_comments_good_pei_number = (TextView) convertView.findViewById(R.id.dynamic_comments_good_pei_number);
            cv.dynamic_comments_list = (MyListView) convertView.findViewById(R.id.dynamic_comments_list);

            convertView.setTag(cv);
        } else {
            cv = (ContentView) convertView.getTag();
        }

        cv.dynmic_time.setText(TimeFormater.getSuitableDateTime(postTime));
        if (good_checked_state)
            cv.dynamic_say_good.setChecked(true);
        if (pei_checked_state)
            cv.dynamic_say_pei.setChecked(true);

        cv.dynamic_username.setText(username);

        Glide.with(activity)
                .load(getAvatar_url())
                .placeholder(R.drawable.default_avatar)
                .centerCrop()
                .into(cv.dynamic_avatar);

        Glide.with(activity)
                .load(getMain_pic_url())
                .placeholder(R.drawable.time_logo)
                .centerCrop()
                .into(cv.dynamic_pic);

        int W = ScreenUtil.getScreenWidth(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(W, W);
        cv.dynamic_pic.setLayoutParams(params);

        cv.dynamic_say_pei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pei++;
                pei_state_change();
                callBack.DataChanged();
            }
        });

        cv.dynamic_say_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                good++;
                good_state_change();
                callBack.DataChanged();
            }
        });


        cv.dynamic_to_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.postComment(getId());
            }
        });

        /** 消息抽取*/
        final JSONObject dt = JSONObject.parseObject(text_for_pic);
        cv.dynamic_text_for_pic.setText(dt.getString("text"));
        if(dt.getString("voice") != null){
            cv.voice_flag.setVisibility(View.VISIBLE);
        }else{
            cv.voice_flag.setVisibility(View.INVISIBLE);
        }
        cv.dynamic_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dt.getString("voice") != null) callBack.playVoiceCall(dt.getString("voice"));
            }
        });
        cv.dynamic_comments_good_pei_number.setText(comments + "评 " + good + "赞 " + pei + "呸");

        DynamicCommentListAdapter dcladapter = new DynamicCommentListAdapter(allComments, activity);
        cv.dynamic_comments_list.setAdapter(dcladapter);
        SetListViewHeight.set(cv.dynamic_comments_list);

        /** 点击评论时*/
        cv.dynamic_comments_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (allComments.get(position).getCtype() == Comment.TEXT_COMMENT) {
                    String name = allComments.get(position).getUsername();
                    JSONObject obj = JSONObject.parseObject(name);
                    if(AccountUtil.userid != obj.getLong("posterid"))
                        callBack.postReplyComment(getId(), obj.getLong("posterid"), obj.getString("postername"));
                    else
                        callBack.postComment(getId());
                } else {
                    new VoicePlayer(allComments.get(position).getVoiceUrl(), activity).play();
                }
            }
        });

        /** 长按评论时*/
        cv.dynamic_comments_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Comment com = allComments.get(position);
                final ArrayList<DialogMenuItem>items = new ArrayList<DialogMenuItem>();
                if(com.getUserid().equals(AccountUtil.userid)){
                    items.add(new DialogMenuItem("删除",R.mipmap.ic_winstyle_delete));
                }else{
                    items.add(new DialogMenuItem("回复",R.mipmap.copy_itme_icon));
                    items.add(new DialogMenuItem("举报",R.mipmap.ic_winstyle_delete));
                }
                final NormalListDialog normalListDialog = new NormalListDialog(activity,items);
                normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int p, long id) {
                        if(items.get(p).mOperName.equals("回复")){
                            callBack.postReplyComment(getId(), com.getUserid(), com.getUsername());
                        }else if(items.get(p).mOperName.equals("删除")){
                            new CommentOpt().DeleteComment(Comment.FOR_DYNAMIC,getId(),com.getId());
                            allComments.remove(position);
                            callBack.DataChanged();
                        }else if(items.get(p).mOperName.equals("举报")){
                            new CommentOpt().Report_JuBao(com.getId(),"common_comment","举报评论，原因不明");
                        }
                        normalListDialog.dismiss();
                    }
                });
                normalListDialog.isTitleShow(false);
                normalListDialog.show();
                return true;
            }
        });
        return convertView;
    }


    private void pei_state_change() {
        pei_checked_state = true;
        good_checked_state = false;
        new CommentOpt().PeiZanOpt(pei_checked_state, 0, getId(), "dynamic");
    }

    private void good_state_change() {
        good_checked_state = true;
        pei_checked_state = false;
        new CommentOpt().PeiZanOpt(good_checked_state, 1, getId(), "dynamic");
    }


    /**
     * defaults
     */
    public DynamicContentEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMain_pic_url() {
        return main_pic_url;
    }

    public void setMain_pic_url(String main_pic_url) {
        this.main_pic_url = main_pic_url;
    }

    public String getText_for_pic() {
        return text_for_pic;
    }

    public void setText_for_pic(String text_for_pic) {
        this.text_for_pic = text_for_pic;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getGood() {
        return good;
    }

    public void setGood(Long good) {
        this.good = good;
    }

    public Long getPei() {
        return pei;
    }

    public void setPei(Long pei) {
        this.pei = pei;
    }

    public boolean isPei_checked_state() {
        return pei_checked_state;
    }

    public void setPei_checked_state(boolean pei_checked_state) {
        this.pei_checked_state = pei_checked_state;
    }

    public boolean isGood_checked_state() {
        return good_checked_state;
    }

    public void setGood_checked_state(boolean good_checked_state) {
        this.good_checked_state = good_checked_state;
    }

    public ArrayList<Comment> getAllComments() {
        return allComments;
    }

    public void setAllComments(ArrayList<Comment> allComments) {
        this.allComments = allComments;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    public Long getpDelteTime() {
        return pDelteTime;
    }

    public void setpDelteTime(Long pDelteTime) {
        this.pDelteTime = pDelteTime;
    }
}
