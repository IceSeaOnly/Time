package site.binghai.number2.Dynamic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import site.binghai.number2.R;
import site.binghai.number2.Utils.ScreenUtil;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AdEntity implements DynamicEntity{
    private Long id;
    private String name;
    private String imgUrl;
    private String jumpUrl;

    public AdEntity(Long id, String name, String imgUrl, String jumpUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.jumpUrl = jumpUrl;
    }

    public AdEntity() {
    }

    @Override
    public int getEntityType() {
        return 2;
    }

    @Override
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Object getData() {
        return null;
    }

    class AdView{
        public ImageView ad_iv;
    }

    @Override
    public View makeConvertView(int position, View convertView, LayoutInflater mInflater, final Activity activity, DynamicActionCallBack callBack) {
        AdView adView = null;
        if (convertView == null || !convertView.getTag().getClass().isInstance(AdEntity.class)){
            adView = new AdView();
            convertView = mInflater.inflate(R.layout.dynamic_ad, null);
            adView.ad_iv = (ImageView) convertView.findViewById(R.id.dynamimc_ad_id);
            convertView.setTag(adView);
        }else{
            adView = (AdView) convertView.getTag();
        }

        adView.ad_iv.setImageResource(R.drawable.ad);

        int W = ScreenUtil.getScreenWidth(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(W, (int) (W*0.618));
        adView.ad_iv.setLayoutParams(params);
        adView.ad_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity,getName()+"图片链接："+getJumpUrl(),Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public void setName(String name) {
        this.name = name;
    }


}
