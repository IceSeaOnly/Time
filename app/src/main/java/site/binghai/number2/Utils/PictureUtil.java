package site.binghai.number2.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/9/18.
 */
public class PictureUtil {
    private static File compression(String path) {
        File outputFile = new File(path);
        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        while (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }
            fileSize = outputFile.length();
        }
        return outputFile;
    }

    public static Uri createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String saveAsPng(Context context, Uri uri, String bitName) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        String _path = "/sdcard/DCIM/Camera/" + bitName + ".png";
        File file = new File(_path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
                return _path;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void uploadPic(final String filePath, final Context context, final AfterUpload after) {
        String uriAPI = Config.PIC_SIGN_SERVER;
        HttpUtil.get(uriAPI, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String SIGN = new String(responseBody);
                Log.i("sign from server", SIGN);
                uploadPicInBackGround(filePath, context, SIGN, after);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                after.afterUpload(null);
                Toast.makeText(context, "网络原因,上传失败...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void uploadPicInBackGround(String filePath, final Context context, String SIGN, final AfterUpload after) {
        UploadManager photoUploadMgr = null;
        photoUploadMgr = new UploadManager(context, "10066161", Const.FileType.Photo, null);
        PhotoUploadTask task = new PhotoUploadTask(filePath, new IUploadTaskListener() {
            @Override
            public void onUploadSucceed(FileInfo fileInfo) {
                Toast.makeText(context, "图片上传成功", Toast.LENGTH_SHORT).show();
                after.afterUpload(fileInfo.url);
            }

            @Override
            public void onUploadFailed(int i, String s) {
                Toast.makeText(context, "上传失败:" + s, Toast.LENGTH_SHORT).show();
                after.afterUpload(null);
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("上传失败")
//                        .setContentText("上传结果:失败! ret:" + i + " msg:" + s)
//                        .setConfirmText("好的")
//                        .show();
            }

            @Override
            public void onUploadProgress(long total, long send) {
                after.uploadProgress(total, send);
            }

            @Override
            public void onUploadStateChange(ITask.TaskState taskState) {

            }
        });
        task.setBucket("time01");
        task.setAuth(SIGN);
        photoUploadMgr.upload(task);
    }

    public static void downloadImageFile(String url, final String fileName) throws Exception {

        // 指定文件类型
        String[] allowedContentTypes = new String[]{"image/png", "image/jpeg"};
        // 获取二进制数据如图片和其他文件
        HttpUtil.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] binaryData) {

                String tempPath = Environment.getExternalStorageDirectory()
                        .getPath() + "/Time/images/";
                FileUtil.isFolderExists(tempPath);

                Log.e("binaryData:", "共下载了：" + binaryData.length);
                //
                Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0,
                        binaryData.length);

                File file = new File(tempPath+fileName);
                // 压缩格式
                Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
                // 压缩比例
                int quality = 85;
                try {
                    // 若存在则删除
                    if (file.exists())
                        file.delete();
                    // 创建文件
                    file.createNewFile();
                    //
                    OutputStream stream = new FileOutputStream(file);
                    // 压缩输出
                    bmp.compress(format, quality, stream);
                    // 关闭
                    stream.close();
                    Log.i("新的欢迎图", "下载成功");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

            }
        });
    }


    public static void RecycleImageView(ImageView ad_iv){
        if(ad_iv == null) return;
        Drawable drawable = ad_iv.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (bmp != null && !bmp.isRecycled()) {
                ad_iv.setImageBitmap(null);
                bmp.recycle();
                bmp = null;
                Log.i("图片内存回收:","已经回收");
            }
        }
    }

    public static Uri compression(Context context,Uri data) {
        return Uri.fromFile(compression(getRealFilePath(context,data)));
    }

    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}