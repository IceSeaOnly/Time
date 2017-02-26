package site.binghai.number2.guideview;

import android.content.Context;

public class Utils {
   public static int  dp2px(Context context,float dp){
       final float scale = context.getResources().getDisplayMetrics().density;
       return (int) (dp * scale + 0.5f);
   }
}
