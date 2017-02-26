package site.binghai.number2.ShowYourLove;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Common.Comment;
import site.binghai.number2.Common.CommentAdapter;
import site.binghai.number2.Common.CommentOpt;
import site.binghai.number2.Common.PostCommonComment;
import site.binghai.number2.Index.ActivityCallback;
import site.binghai.number2.R;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.ScreenUtil;
import site.binghai.number2.Utils.SetListViewHeight;
import site.binghai.number2.Utils.SignUtil;
import site.binghai.number2.Utils.TimeFormater;
import site.binghai.number2.Utils.VoicePlayer;

import static site.binghai.number2.Utils.Process.dismiss;
import static site.binghai.number2.Utils.Process.onProcessing;

public class ShowYourLoveDetailActivity extends AppCompatActivity {

    private TextView username;
    private TextView love_simple_desc;
    private TextView lovedetail_posttime;
    private TextView lovedetail_postday;
    private TextView lovedetail_postmonth;
    private TextView lovedetail_maintext;
    private ShineButton love_good;
    private ImageView lovedetail_topost;
    private TextView lovedetail_commentsum;
    private ImageView lovedetail_bigimg;
    private ImageView lovedetail_avatar;
    private ListView lovedetail_comments;
    private ArrayList<Comment> comments = new ArrayList<>();
    private CommentAdapter adapter = null;
    private ShowYourLoveEntity entity;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            lovedetail_commentsum.setText("("+comments.size()+")");
            SetList();
        }
    };
    private static String TAG = "ShowYourLoveDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_your_love_entitydetail);
        setTitle("表白内容");
        username = (TextView) findViewById(R.id.lovedetail_username);
        love_simple_desc = (TextView) findViewById(R.id.love_simple_desc);
        lovedetail_posttime = (TextView) findViewById(R.id.lovedetail_posttime);
        lovedetail_postday = (TextView) findViewById(R.id.lovedetail_postday);
        lovedetail_postmonth = (TextView) findViewById(R.id.lovedetail_postmonth);
        lovedetail_maintext = (TextView) findViewById(R.id.lovedetail_maintext);
        lovedetail_commentsum = (TextView) findViewById(R.id.lovedetail_csum);
        love_good = (ShineButton) findViewById(R.id.love_good);
        lovedetail_topost = (ImageView) findViewById(R.id.lovedetail_topost);
        lovedetail_bigimg = (ImageView) findViewById(R.id.lovedetail_bigimg);
        lovedetail_avatar = (ImageView) findViewById(R.id.lovedetail_avatar);
        lovedetail_comments = (ListView) findViewById(R.id.lovedetail_comments);

        adapter = new CommentAdapter(comments,this);
        lovedetail_comments.setAdapter(adapter);
        InitData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.only_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionBar_add:
                postComment(entity.getId(), null,null, Comment.FOR_SHOW_YOUR_LOVE);
                return true;
        }
        return true;
    }

    private void InitData() {
        entity = (ShowYourLoveEntity) getIntent().getSerializableExtra("obj");
        username.setText(entity.isNameMask()?"匿名":entity.getUsername());
        love_simple_desc.setText(entity.getShort_desc());
        lovedetail_posttime.setText(TimeFormater.format_hh_mm(entity.getPostTime()));
        lovedetail_postday.setText(TimeFormater.format_dd(entity.getPostTime()));
        lovedetail_postmonth.setText(TimeFormater.format_Month(entity.getPostTime()));
        lovedetail_maintext.setText(entity.getText());
        love_good.setChecked(entity.isIlike());
        if(entity.getPicUrl() == null || entity.getPicUrl().length()<1){
            lovedetail_bigimg.setVisibility(View.GONE);
        }else{
            lovedetail_bigimg.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(entity.getPicUrl())
                    .placeholder(R.drawable.fang)
                    .centerCrop()
                    .into(lovedetail_bigimg);
        }
        Glide.with(this)
                .load(entity.getAvatarImg())
                .placeholder(R.drawable.default_avatar)
                .centerCrop()
                .into(lovedetail_avatar);

        lovedetail_topost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(entity.getId(), null,null, Comment.FOR_SHOW_YOUR_LOVE);
            }
        });
        LoadComments(entity.getId());
        /** 长按*/
        lovedetail_comments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final ArrayList<DialogMenuItem>items = new ArrayList<DialogMenuItem>();
                if(comments.get(position).getUserid().equals(AccountUtil.userid)){
                    items.add(new DialogMenuItem("删除",R.mipmap.ic_winstyle_delete));
                }else{
                    items.add(new DialogMenuItem("回复",R.mipmap.copy_itme_icon));
                    items.add(new DialogMenuItem("举报",R.mipmap.ic_winstyle_delete));
                }
                final NormalListDialog normalListDialog = new NormalListDialog(ShowYourLoveDetailActivity.this,items);
                normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int p, long id) {
                        if(items.get(p).mOperName.equals("回复")){
                            postComment(entity.getId(), comments.get(position).getUserid(),comments.get(position).getUsername(), Comment.FOR_SHOW_YOUR_LOVE);
                        }else if(items.get(p).mOperName.equals("删除")){
                            new CommentOpt().DeleteComment(Comment.FOR_SHOW_YOUR_LOVE,entity.getId(),comments.get(position).getId());
                            comments.remove(position);
                            handler.sendMessage(new Message());
                        }else if(items.get(p).mOperName.equals("举报")){
                            new CommentOpt().Report_JuBao(comments.get(position).getId(),"common_comment","举报评论，原因不明");
                        }
                        normalListDialog.dismiss();
                    }
                });
                normalListDialog.isTitleShow(false);
                normalListDialog.show();
                return true;
            }
        });
        /** 点击*/
        lovedetail_comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(comments.get(position).getCtype() == Comment.VOICE_COMMENT){
                    new VoicePlayer(comments.get(position).getVoiceUrl(),ShowYourLoveDetailActivity.this).play();
                }else{
                    if(comments.get(position).getUserid().equals(AccountUtil.userid)){
                        postComment(entity.getId(), null,null, Comment.FOR_SHOW_YOUR_LOVE);
                    }else{
                        postComment(entity.getId(), comments.get(position).getUserid(),comments.get(position).getUsername(), Comment.FOR_SHOW_YOUR_LOVE);
                    }
                }
            }
        });
    }

    private void LoadComments(Long id) {
        //onProcessing(ShowYourLoveDetailActivity.this,"加载中",false);
        Map param = new HashMap();
        param.put("entityId",id);
        param.put("entityType",Comment.FOR_SHOW_YOUR_LOVE);
        param.put("commentsSize",-1);
        HttpUtil.post(Config.getComments, SignUtil.CommonUserTokenSIGN(param),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject rs) {
                super.onSuccess(statusCode, headers, rs);
                //dismiss();

                try {
                    if(rs.getBoolean("result")){
                        String data = rs.getString("entity");
                        comments.clear();
                        comments.addAll(com.alibaba.fastjson.JSONObject.parseArray(data,Comment.class));
                        handler.sendMessage(new Message());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void SetList() {
        SetListViewHeight.set(lovedetail_comments);
    }

    public void postComment(final Long entityId, Long replyid, String replyname, int etype){
        PostCommonComment pcfd = new PostCommonComment();
        pcfd.InitFirst(entityId,replyid,replyname,etype,new ActivityCallback(){
            @Override
            public void onError(String msg) {}
            @Override
            public void onSuccess(String msg, Long dynamicId) {
                Toast.makeText(ShowYourLoveDetailActivity.this,msg,Toast.LENGTH_SHORT).show();
                LoadComments(entity.getId());
            }
        });
        pcfd.show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
    }
}
