package com.example.lifeassistant_project.features_class;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;

public class AndroidCommonFunction {
    //取得dp的pixel
    //(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());


    public static int getViewWidth(View view){
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(width,height);
        return view.getMeasuredWidth();
    }

    public static int getViewHeight(View view){
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(width,height);
        return view.getMeasuredHeight();
    }

    public static GradientDrawable getGradientBackground(int[] colors){
        GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setColors(colors);
        return gradientDrawable;
    }

    public static void addBaseline(Context context, LinearLayout linearLayout, int drawableID){
        ImageView segment=new ImageView(context);
        segment.setImageResource(drawableID);
        segment.setScaleType(ImageView.ScaleType.FIT_XY);
        linearLayout.addView(segment,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    //取得星期幾
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
