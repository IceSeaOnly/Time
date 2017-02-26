package site.binghai.number2.Dynamic;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import me.shaohui.bottomdialog.BaseBottomDialog;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Common.Comment;
import site.binghai.number2.Index.ActivityCallback;
import site.binghai.number2.Index.MainActivity;
import site.binghai.number2.R;
import site.binghai.number2.Utils.AfterUpload;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.RecordHelper;
import site.binghai.number2.Utils.SignUtil;

/**
 * Created by Administrator on 2016/10/15.
 */
public class PostNewDynamic extends BaseBottomDialog {

    private EditText edit_new_dynamic;
    private TextView post_new_dynamic_button;
    private ImageView post_new_dynamic_from_camera;
    private ImageView post_new_dynamic_from_photo;
    private TextView post_new_dynamic_picture_state;
    private ImageView postdynamic_record;

    private String voice_url;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
    public static final int RESULT_OK = -1;
    public static String mCurrentPhotoPath;
    public static File mTempDir;

    private String hint = "让我想想，要写点啥呢...";

    public void setHint(String h) {
        this.hint = h;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.post_new_dynamic;
    }

    @Override
    public void bindView(View v) {
        edit_new_dynamic = (EditText) v.findViewById(R.id.edit_new_dynamic);
        post_new_dynamic_button = (TextView) v.findViewById(R.id.post_new_dynamic_button);
        post_new_dynamic_from_photo = (ImageView) v.findViewById(R.id.post_new_dynamic_from_photo);
        post_new_dynamic_from_camera = (ImageView) v.findViewById(R.id.post_new_dynamic_from_camera);
        post_new_dynamic_picture_state = (TextView) v.findViewById(R.id.post_new_dynamic_picture_state);
        postdynamic_record = (ImageView) v.findViewById(R.id.postdynamic_record);
        edit_new_dynamic.setHint(hint);

        postdynamic_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voice_url = null;
                final RecordHelper rh = new RecordHelper();
                rh.setAfterupload(new AfterUpload() {
                    @Override
                    public void afterUpload(String url) {
                        voice_url = url;
                        if(voice_url == null){
                            Toast.makeText(getContext(),"录音已经准备好啦~",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void uploadProgress(long total, long send) {
                    }
                });
                rh.show(getFragmentManager());
            }
        });

        post_new_dynamic_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.post_new_dynamic_picture_state = null;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String fileName = "Temp_camera" + String.valueOf(System.currentTimeMillis());
                File cropFile = new File(mTempDir, fileName);
                Uri fileUri = Uri.fromFile(cropFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                mCurrentPhotoPath = fileUri.getPath();
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
            }
        });
        post_new_dynamic_from_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.post_new_dynamic_picture_state = null;
                Crop.pickImage(getActivity());
            }
        });
        post_new_dynamic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edit_new_dynamic.getText().toString();
                if (text.length() < 1 && voice_url == null) {
                    Toast.makeText(getContext(), "请输入些内容先~", Toast.LENGTH_SHORT).show();
                } else if (MainActivity.post_new_dynamic_picture_state == null) {
                    Toast.makeText(getContext(), "图片上传失败，请重试", Toast.LENGTH_SHORT).show();
                } else {
                    ToPostNewDynamic(MainActivity.post_new_dynamic_picture_state, text);
                    edit_new_dynamic.setText("");
                    MainActivity.post_new_dynamic_picture_state = null;
                    MainActivity.post_new_dynamic_pic_selected = false;
                    dismiss();
                }
            }
        });
    }

    SweetAlertDialog pDialog = null;

    /**
     * 发布新鲜
     */
    private void ToPostNewDynamic(String imgurl, String text) {
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在发布...");
        pDialog.setCancelable(true);
        pDialog.show();

        com.alibaba.fastjson.JSONObject dt = new com.alibaba.fastjson.JSONObject();
        dt.put("text",text);
        dt.put("voice",voice_url);
        Map param = new HashMap();
        param.put("text", dt);
        param.put("img", imgurl);
        param.put("avatar", AccountUtil.my_avatar);
        param.put("username", AccountUtil.nick_name);

        HttpUtil.post(Config.postNewDynamic, SignUtil.CommonUserTokenSIGN(param), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                pDialog.dismiss();
                callback.onSuccess("发布成功", 0L);
                try {
                    AccountUtil.setToken(response.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                callback.onError("发布失败，请检查网络");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                callback.onError("发布失败，请检查网络");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                callback.onError("发布失败，请检查网络");
            }
        });
    }

    @Override
    public float getDimAmount() {
        return 0.9f;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.post_new_dynamic_pic_selected)
            post_new_dynamic_picture_state.setText("已选择");
    }

    private ActivityCallback callback;

    public ActivityCallback getCallback() {
        return callback;
    }

    public void setCallback(ActivityCallback callback) {
        this.callback = callback;
    }
}
