package site.binghai.number2.Index;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import site.binghai.number2.R;
import site.binghai.number2.Utils.ScreenUtil;

/**
 * Created by Administrator on 2016/9/7.
 */
public class NiurenViewMaker {

    private ArrayList<String> urls;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<View> views = null;

    public NiurenViewMaker(ArrayList<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        views = new ArrayList<>();
        makeViews();
    }

    public ArrayList<View> getViews(){
        return views;
    }

    public View getView(int i){
        return views.get(i);
    }
    public int getSize(){
        return urls.size();
    }
    private void makeViews(){
        int W = (int) (ScreenUtil.getScreenWidth(context)*0.8);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) (W*0.72),W);
        for(int i = 0;i < urls.size();i++){
            View view = layoutInflater.inflate(R.layout.index_niuren,null);
            view.setLayoutParams(params);
            ImageView iv = (ImageView) view.findViewById(R.id.niuren_img);
            iv.setMinimumHeight(W);
            iv.setMinimumWidth((int)(W*0.72));

            try {
                Glide.with(context)
                        .load(urls.get(i))
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .crossFade()
                        .into(iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
            views.add(view);
        }
    }
}
