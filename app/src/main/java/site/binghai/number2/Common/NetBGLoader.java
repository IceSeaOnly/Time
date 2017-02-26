package site.binghai.number2.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by Administrator on 2016/12/8.
 */
public class NetBGLoader {
    private Activity context;
    private String url;
    private View convertView;

    private SimpleTarget target = new SimpleTarget<Bitmap>(){
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            convertView.setBackground(new BitmapDrawable(resource));
        }
    };

    public NetBGLoader(Activity activity, String bg_url, View convertView) {
        context = activity;
        url = bg_url;
        this.convertView = convertView;
    }

    public void load(){
        Glide.with(context)
                .load(url)
                .asBitmap() //必须写，否则会报类型转化异常
                .into(target); //此处为target
    }
}
