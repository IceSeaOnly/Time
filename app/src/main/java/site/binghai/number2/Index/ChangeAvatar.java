package site.binghai.number2.Index;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.soundcloud.android.crop.Crop;
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

public class ChangeAvatar extends AppCompatActivity {

    private ImageView iv;
    private Button from_camera;
    private Button from_photos;

    private File mTempDir;
    private String mCurrentPhotoPath;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        mTempDir = new File(Environment.getExternalStorageDirectory(), "Temp");
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }

        iv = (ImageView) findViewById(R.id.image_wait_to_change);
        from_camera = (Button) findViewById(R.id.from_camera);
        from_photos = (Button) findViewById(R.id.from_photos);


        Glide.with(ChangeAvatar.this)
                .load(AccountUtil.my_avatar)
                .placeholder(R.drawable.default_avatar)
                .centerCrop()
                .into(iv);

        /**
         * 从照相机选取 监听器
         * */
        from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String fileName = "Temp_camera" + String.valueOf(System.currentTimeMillis());
                File cropFile = new File(mTempDir, fileName);
                Uri fileUri = Uri.fromFile(cropFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
                mCurrentPhotoPath = fileUri.getPath();
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
            }
        });

        /**
         * 从相册选取 监听器
         * */
        from_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(ChangeAvatar.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            if (mCurrentPhotoPath != null) {
                File __f = new File(mCurrentPhotoPath);
//                File __f = PictureUtil.compression(mCurrentPhotoPath);

//                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("大小对比")
//                        .setContentText("原图:"+_f.length()+"\n压缩后:"+ __f.length())
//                        .setConfirmText("Yes,delete it!")
//                        .show();
                beginCrop(Uri.fromFile(__f));
            }
        }
    }

    private void beginCrop(Uri data) {
        String fileName = "Temp_" + String.valueOf(System.currentTimeMillis());
        File cropFile = new File(mTempDir, fileName);
        Uri outputUri = Uri.fromFile(cropFile);
        new Crop(PictureUtil.compression(getApplicationContext(), data))
                .output(outputUri).setCropType(true).start(this);
    }

    SweetAlertDialog pDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (pDialog != null) pDialog.dismiss();
        }
    };

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri _uri = Crop.getOutput(result);
            iv.setImageURI(_uri);
            String _tName = System.currentTimeMillis() + "_png_avatar";
            /**
             * 万象优图上传
             * */
            pDialog = new SweetAlertDialog(ChangeAvatar.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("正在上传...");
            pDialog.setCancelable(false);
            pDialog.show();
            try {
                final String pngPath = PictureUtil.saveAsPng(this, _uri, _tName);
                if (pngPath != null) {
                    PictureUtil.uploadPic(pngPath, this, new AfterUpload() {
                        @Override
                        public void afterUpload(String url) {
                            handler.handleMessage(new Message());
                            if (url != null) {
                                /**
                                 * 更新本地，并保存链接到服务器
                                 * */
                                AccountUtil.my_avatar = url;
                                uploadAvatar(url);
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
//            iv.setImageBitmap(getCircleBitmap(Crop.getOutput(result)));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 同步新的url到服务器
     */
    private void uploadAvatar(String url) {
        Map req = new HashMap();
        req.put("avatar_url", url);

        HttpUtil.post(Config.ChangeAvatar, SignUtil.CommonUserTokenSIGN(req), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("result")) {
                        Log.i("更换头像", "成功");
                        AccountUtil.setToken(response.getString("token"));
                    } else {
                        Log.i("更换头像", "失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("更换头像", "系统错误");
            }
        });
    }


}
