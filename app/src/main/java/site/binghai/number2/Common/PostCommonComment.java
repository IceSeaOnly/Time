package site.binghai.number2.Common;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import me.shaohui.bottomdialog.BaseBottomDialog;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Index.ActivityCallback;
import site.binghai.number2.R;
import site.binghai.number2.Utils.AfterUpload;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.RecordHelper;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/10/23.
 */
public class PostCommonComment extends BaseBottomDialog {
    @Override
    public int getLayoutRes() {
        return R.layout.post_common_comment;
    }

    private Long entityId;
    private EditText pcc_edit_comment;
    private ImageView pcc_record_to_comment;
    private TextView pcc_commit_comment;
    private Long replyid;
    private String replyname;
    private int etype;


    public void InitFirst(Long dynamicId, Long replyid, String replyname, int etype, ActivityCallback callback) {
        this.entityId = dynamicId;
        this.replyid = replyid;
        this.replyname = replyname;
        this.etype = etype;
        this.callback = callback;
    }

    @Override
    public void bindView(View v) {
        pcc_edit_comment = (EditText) v.findViewById(R.id.pcc_edit_comment);
        pcc_record_to_comment = (ImageView) v.findViewById(R.id.pcc_record_to_comment);
        pcc_commit_comment = (TextView) v.findViewById(R.id.pcc_commit_comment);

        if (replyname != null)
            pcc_edit_comment.setHint("回复" + replyname);
        else
            pcc_edit_comment.setHint("桥豆麻袋，我要写什么来着...?");

        pcc_record_to_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecordHelper rh = new RecordHelper();
                rh.setAfterupload(new AfterUpload() {
                    @Override
                    public void afterUpload(String url) {
                        PostComment(replyid,replyname,"", url, rh.getVoiceLenth(), "voice");
                    }

                    @Override
                    public void uploadProgress(long total, long send) {
                    }
                });
                rh.show(getFragmentManager());
                dismiss();
            }
        });
        /** 发送评论*/
        pcc_commit_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = pcc_edit_comment.getText().toString();
                if (comment.length() == 0) {
                    dismiss();
                    return;
                }
                pcc_edit_comment.setText("");
                PostComment(replyid,replyname,comment, "", 0, "text");
                dismiss();
            }
        });
    }


    /**
     * 发布评论,ctype:text,voice;type:dynmaic...
     */
    private void PostComment(Long replyid,String replyname,String comment, String voiceUrl, int voiceLong, String ctype) {
        Map param = new HashMap();
        param.put("comment", comment);
        param.put("voiceUrl", voiceUrl);
        param.put("voiceLong", voiceLong);
        param.put("entityId", entityId);
        param.put("avatar", AccountUtil.my_avatar);
        param.put("ctype", ctype);
        param.put("type", etype);

        com.alibaba.fastjson.JSONObject name = new com.alibaba.fastjson.JSONObject();
        name.put("posterid", AccountUtil.userid);
        name.put("postername", AccountUtil.nick_name);
        if (replyid == null) {
            name.put("single", true);
        } else {
            name.put("single", false);
            name.put("replyid",replyid);
            name.put("replyname",replyname);
        }
        param.put("username", name.toString());
        HttpUtil.post(Config.commonPostComment, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(callback != null) callback.onError("评论失败，请检查网络");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(callback != null) callback.onError("评论失败，请检查网络");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if(callback != null) callback.onError("评论失败，请检查网络");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AccountUtil.setToken(response.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(callback != null) callback.onSuccess("评论成功", entityId);
            }

        });
    }


    private ActivityCallback callback;
    
    public PostCommonComment() {
    }
}
