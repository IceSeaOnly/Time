package site.binghai.number2.ShowYourLove;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.checkbox.SmoothCheckBox;
import com.kevin.loopview.AdLoopView;
import com.kevin.loopview.internal.BaseLoopAdapter;
import com.kevin.loopview.internal.LoopData;
import com.kevin.loopview.utils.JsonTool;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.R;
import site.binghai.number2.Utils.AfterUpload;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.PictureUtil;
import site.binghai.number2.Utils.SignUtil;
import site.binghai.number2.Utils.ToastUtil;

public class PostYourLoveCard extends Activity {

    public static String post_new_picture_state = null;
    public static boolean post_new_pic_selected;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
    public static final int RESULT_OK = -1;
    public static String mCurrentPhotoPath;
    public static File mTempDir;

    private EditText username;
    private EditText shortDesc;
    private EditText mainText;
    private TextView btnPost;
    private TextView posthelp;
    private ImageView lovepostadd;
    private SmoothCheckBox loveniming;
    private TextView lovepostcancel;
    private TextView love_bg_chose_notice;
    private AdLoopView skin_loopview;
    private String bgurl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.post_new_love);
        mTempDir = new File(Environment.getExternalStorageDirectory(), "Temp");
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }

        username = (EditText) findViewById(R.id.username);
        shortDesc = (EditText) findViewById(R.id.shortDesc);
        mainText = (EditText) findViewById(R.id.mainText);
        btnPost = (TextView) findViewById(R.id.btnPost);
        love_bg_chose_notice = (TextView) findViewById(R.id.love_bg_chose_notice);
        posthelp = (TextView) findViewById(R.id.love_posthelp);
        lovepostadd = (ImageView) findViewById(R.id.love_postadd);
        lovepostcancel = (TextView) findViewById(R.id.love_postcancel);
        loveniming = (SmoothCheckBox) findViewById(R.id.love_postniming);
        skin_loopview = (AdLoopView) findViewById(R.id.love_skin_loopview);


        setLoopView();
        lovepostcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_new_picture_state = null;
                finish();
            }
        });
        posthelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastUtil(PostYourLoveCard.this).successNotice("小帮助","当你填写了ta的手机号，我们会帮你短信通知ta，但不会透露你的身份，除非你自己在表白里面写啦~");
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowYourLoveEntity it = new ShowYourLoveEntity(AccountUtil.my_avatar,
                        post_new_picture_state == null?"":post_new_picture_state,
                        mainText.getText().toString(),
                        AccountUtil.getAccountId(),
                        username.getText().toString(),
                        shortDesc.getText().toString(),
                        System.currentTimeMillis(),
                        false,
                        0,loveniming.isChecked(),bgurl==null?"":bgurl);
                if(it.getText().length()<5){
                    new ToastUtil(PostYourLoveCard.this).errorNotice("太短啦！","你的内容写的太短啦！多写一点吧！");
                }else{
                    ToPostNewEntity(it);
                    post_new_picture_state = null;
                }
            }
        });

        lovepostadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(PostYourLoveCard.this);
            }
        });
    }

    private void setLoopView() {
        HttpUtil.post(Config.LoveSkin, SignUtil.CommonUserTokenSIGN(null),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                final LoopData loopData = JsonTool.toBean(response.toString(), LoopData.class);
                skin_loopview.refreshData(loopData);
                skin_loopview.setOnClickListener(new BaseLoopAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PagerAdapter parent, View view,
                                            int position, int realPosition) {
                        love_bg_chose_notice.setText("已选择:"+loopData.items.get(position).descText);
                        bgurl = loopData.items.get(position).imgUrl;
                    }
                });
            }
        });

    }


    private void ToPostNewEntity(ShowYourLoveEntity entity) {
        onProcess("正在发布...");
        Map param = new HashMap();
        param.put("entity",entity);
        HttpUtil.post(Config.postNewShowYourLove, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                dismiss();
                try {
                    if (response.getBoolean("result")) {
                        AccountUtil.setToken(response.getString("token"));
                        finish();
                    }else{
                        Toast.makeText(PostYourLoveCard.this,"发布失败"+response.toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dismiss();
                Toast.makeText(PostYourLoveCard.this,"发布失败，请检查网络",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dismiss();
                Toast.makeText(PostYourLoveCard.this,"发布失败，请检查网络",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dismiss();
                Toast.makeText(PostYourLoveCard.this,"发布失败，请检查网络",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (curPhotoPath != null){
            Glide.with(this)
                    .load(curPhotoPath)
                    .placeholder(R.drawable.picture)
                    .centerCrop()
                    .into(lovepostadd);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Log.i("onActivityResult", requestCode + ":" + resultCode);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            post_new_pic_selected = true;
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else {
            if (mCurrentPhotoPath != null) {
                post_new_pic_selected = true;
                File __f = new File(mCurrentPhotoPath);
                beginCrop(Uri.fromFile(__f));
            } else
                Log.e("PhotoPathError", "mCurrentPhotoPath Is null");
        }
    }

    public static Uri curPhotoPath = null;

    private void beginCrop(Uri data) {
        String fileName = "Temp_" + String.valueOf(System.currentTimeMillis());
        File cropFile = new File(mTempDir, fileName);
        Uri outputUri = Uri.fromFile(cropFile);
//        new Crop(data).output(outputUri).setCropType(true).start(this);
        curPhotoPath = outputUri;
        new Crop(PictureUtil.compression(getApplicationContext(), data)).
                output(outputUri).withAspect(1, 1).start(this);
    }

    SweetAlertDialog pDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (pDialog != null) pDialog.dismiss();
        }
    };

    private void onProcess(String msg) {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(msg);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dismiss() {
        if (pDialog != null) pDialog.dismiss();
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri _uri = Crop.getOutput(result);
            String _tName = System.currentTimeMillis() + "_png_avatar";
            /**万象优图上传*/
            onProcess("图片正在上传...");
            try {
                final String pngPath = PictureUtil.saveAsPng(this, _uri, _tName);
                if (pngPath != null) {
                    PictureUtil.uploadPic(pngPath, this, new AfterUpload() {
                        @Override
                        public void afterUpload(String url) {
                            dismiss();
                            handler.handleMessage(new Message());
                            if (url != null) {
                                /** 更新本地，并保存链接到服务器*/
                                post_new_picture_state = url.toString();
                                curPhotoPath = null;
                            }
                        }

                        @Override
                        public void uploadProgress(long total, long send) {

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
        }
    }
}
