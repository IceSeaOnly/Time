package site.binghai.number2.Dynamic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import site.binghai.number2.R;

/**
 * Created by Administrator on 2016/9/1.
 * 推荐用户实体类
 */
public class RecommendUserEntity implements DynamicEntity {

    private int id1;
    private int id2;
    private int id3;
    private String name1;
    private String name2;
    private String name3;
    private String avatarImgUrl1;
    private String avatarImgUrl2;
    private String avatarImgUrl3;


    public RecommendUserEntity(int id1, int id2, int id3, String name1, String name2, String name3, String avatarImgUrl1, String avatarImgUrl2, String avatarImgUrl3) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.avatarImgUrl1 = avatarImgUrl1;
        this.avatarImgUrl2 = avatarImgUrl2;
        this.avatarImgUrl3 = avatarImgUrl3;
    }

    public RecommendUserEntity() {
    }

    @Override
    public Object getData() {
        return null;
    }

    class RecommendUserEntityListItemView {//自定义控件集合
        public ImageView ivAvatar1;
        public ImageView ivAvatar2;
        public ImageView ivAvatar3;
        public TextView tvName1;
        public TextView tvName2;
        public TextView tvName3;
        public Button btnPayAttentionTo1;
        public Button btnPayAttentionTo2;
        public Button btnPayAttentionTo3;
    }

    @Override
    public View makeConvertView(final int position, View convertView, LayoutInflater mInflater, final Activity activity, DynamicActionCallBack callBack) {
        RecommendUserEntityListItemView rlistItemView = null;
        if (convertView == null || !convertView.getTag().getClass().isInstance(RecommendUserEntity.class)) {
            // 返回值为 0 的时候为推荐用户的对象
            // 返回值为 1 的时候为用户的动态信息
            // 返回值为 2 的时候为广告推广
            rlistItemView = new RecommendUserEntityListItemView();
            //获取list_item布局文件的视图
            convertView = mInflater.inflate(R.layout.dynamic_user_recommendation, null);
            //获取控件对象
            rlistItemView.ivAvatar1 = (ImageView) convertView.findViewById(R.id.iv_recommend_user_avatar1);
            rlistItemView.ivAvatar2 = (ImageView) convertView.findViewById(R.id.iv_recommend_user_avatar2);
            rlistItemView.ivAvatar3 = (ImageView) convertView.findViewById(R.id.iv_recommend_user_avatar3);
            rlistItemView.tvName1 = (TextView) convertView.findViewById(R.id.tv_recommend_user_name1);
            rlistItemView.tvName2 = (TextView) convertView.findViewById(R.id.tv_recommend_user_name2);
            rlistItemView.tvName3 = (TextView) convertView.findViewById(R.id.tv_recommend_user_name3);
            rlistItemView.btnPayAttentionTo1 = (Button) convertView.findViewById(R.id.btn_pay_attention_to_recommend_user1);
            rlistItemView.btnPayAttentionTo2 = (Button) convertView.findViewById(R.id.btn_pay_attention_to_recommend_user2);
            rlistItemView.btnPayAttentionTo3 = (Button) convertView.findViewById(R.id.btn_pay_attention_to_recommend_user3);
            //设置控件集到convertView
            convertView.setTag(rlistItemView);
        } else if(convertView.getTag().getClass().isInstance(RecommendUserEntity.class)){
            rlistItemView = (RecommendUserEntityListItemView) convertView.getTag();
        }
        // 设置Item内容
        rlistItemView.ivAvatar1.setImageResource(R.drawable.default_avatar);
        rlistItemView.ivAvatar2.setImageResource(R.drawable.default_avatar);
        rlistItemView.ivAvatar3.setImageResource(R.drawable.default_avatar);
        rlistItemView.tvName1.setText(name1);
        rlistItemView.tvName2.setText(name2);
        rlistItemView.tvName3.setText(name3);
        rlistItemView.btnPayAttentionTo1.setText("+关注");
        rlistItemView.btnPayAttentionTo2.setText("+关注");
        rlistItemView.btnPayAttentionTo3.setText("+关注");

        rlistItemView.btnPayAttentionTo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "你关注了" + id1 + "号用户", Toast.LENGTH_SHORT).show();
            }
        });
        rlistItemView.btnPayAttentionTo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "你关注了" + id2 + "号用户", Toast.LENGTH_SHORT).show();
            }
        });
        rlistItemView.btnPayAttentionTo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "你关注了" + id3 + "号用户", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }


    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public int getId3() {
        return id3;
    }

    public void setId3(int id3) {
        this.id3 = id3;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getAvatarImgUrl1() {
        return avatarImgUrl1;
    }

    public void setAvatarImgUrl1(String avatarImgUrl1) {
        this.avatarImgUrl1 = avatarImgUrl1;
    }

    public String getAvatarImgUrl2() {
        return avatarImgUrl2;
    }

    public void setAvatarImgUrl2(String avatarImgUrl2) {
        this.avatarImgUrl2 = avatarImgUrl2;
    }

    public String getAvatarImgUrl3() {
        return avatarImgUrl3;
    }

    public void setAvatarImgUrl3(String avatarImgUrl3) {
        this.avatarImgUrl3 = avatarImgUrl3;
    }

    @Override
    public int getEntityType() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Long getId() {
        return -99L;
    }


}
