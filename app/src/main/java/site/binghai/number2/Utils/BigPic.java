package site.binghai.number2.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.faradaj.blurbehind.BlurBehind;

import site.binghai.number2.R;

public class BigPic extends Activity {

    private ImageView big_pic_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pic);
        big_pic_img = (ImageView) findViewById(R.id.big_pic_img);
        String url = getIntent().getStringExtra("url");

        BlurBehind.getInstance()
                .withAlpha(60)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this);

        Glide.with(this)
                .load(url)
                .into(big_pic_img);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.finish();
        return super.dispatchTouchEvent(ev);
    }
}
