package site.binghai.number2.Question;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;

import java.util.ArrayList;

import site.binghai.number2.R;
import site.binghai.number2.Utils.BigPic;

/**
 * Created by Administrator on 2016/8/29.
 */
public class QuestionAdapter extends BaseAdapter {

    private ArrayList<QuestionEntity> questions;
    private Context context;
    private Activity activity;
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

    public QuestionAdapter(ArrayList<QuestionEntity> questions, Activity activity) {
        this.activity = activity;
        this.context = activity;
        this.questions = questions;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public QuestionEntity getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return questions.get(position).getId();
    }

public final class ListItemView{                //自定义控件集合
    public ImageView imgBtn_avatar;
    public TextView mainContent;
    public ImageView item_leftImg;
    public ImageView item_CenterImg;
    public ImageView item_RightImg;
    public ImageView item_btn_think_low;
    public ImageView item_btn_think_good;
    public ImageView item_btn_to_comment;
    public TextView item_show_comment_good_low;

}
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //自定义视图
        ListItemView  listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = mInflater.inflate(R.layout.question_list_item, null);
            //获取控件对象
            listItemView.imgBtn_avatar = (ImageView) convertView.findViewById(R.id.imgBtn_avatar);
            listItemView.mainContent = (TextView) convertView.findViewById(R.id.mainContent);
            listItemView.item_leftImg = (ImageView) convertView.findViewById(R.id.item_leftImg);
            listItemView.item_CenterImg = (ImageView) convertView.findViewById(R.id.item_CenterImg);
            listItemView.item_RightImg = (ImageView) convertView.findViewById(R.id.item_RightImg);
            listItemView.item_btn_think_low = (ImageView) convertView.findViewById(R.id.item_btn_think_low);
            listItemView.item_btn_think_good = (ImageView) convertView.findViewById(R.id.item_btn_think_good);
            listItemView.item_btn_to_comment = (ImageView) convertView.findViewById(R.id.item_btn_to_comment);
            listItemView.item_show_comment_good_low = (TextView) convertView.findViewById(R.id.item_show_comment_good_low);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else
            listItemView = (ListItemView) convertView.getTag();

        // 设置Item内容
        listItemView.imgBtn_avatar.setImageResource(R.drawable.default_avatar);
        listItemView.mainContent.setText(questions.get(position).getMainContent());


        listItemView.item_leftImg.setImageResource(R.drawable.default_img);
        listItemView.item_leftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlurBehind.getInstance().execute(activity, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(context, BigPic.class);
                        intent.putExtra("url","http://118.192.140.147/data/f_92996215.png");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        context.startActivity(intent);
                    }
                });
            }
        });

        listItemView.item_CenterImg.setImageResource(R.drawable.default_img);
        listItemView.item_RightImg.setImageResource(R.drawable.default_img);



        listItemView.item_btn_think_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"你认为id为"+getItemId(position)+"的问题说呸！",Toast.LENGTH_SHORT).show();
            }
        });
        listItemView.item_btn_think_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"你认为id为"+getItemId(position)+"的问题点赞",Toast.LENGTH_SHORT).show();
            }
        });
        listItemView.item_btn_to_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"你将给id为"+getItemId(position)+"的问题评论",Toast.LENGTH_SHORT).show();
            }
        });

        listItemView.item_show_comment_good_low.setText(getItem(position).getItem_show_comment_good_low());
        ArrayList<String>names = questions.get(position).getComments_names();
        ArrayList<String>conmments = questions.get(position).getComments();


        return convertView;
    }
}
