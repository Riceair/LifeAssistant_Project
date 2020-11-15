package com.example.lifeassistant_project.features_class;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.lifeassistant_project.R;

import java.util.Calendar;
import java.util.Date;

public class AndroidCommonFunction {
    //取得dp的pixel
    //(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

    //取得螢幕長寬
    //    DisplayMetrics dm=new DisplayMetrics();
    //    getWindowManager().getDefaultDisplay().getMetrics(dm);
    //    int screenWidth=dm.widthPixels;
    //    int screenHeight=dm.heightPixels;

    public static void changeRelativeViewSize(ViewGroup viewGroup,float widthRate,float heightRate) {//傳入Activity頂層Layout,螢幕寬,螢幕高
        for(int i = 0; i<viewGroup.getChildCount(); i++ ){
            View v = viewGroup.getChildAt(i);
            if(v.getId() == R.id.toolbar) {
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) v.getLayoutParams();
                params.height= (int) (params.height*heightRate);
                v.setLayoutParams(params);
                changeToolbarLayoutSize((ViewGroup) v,widthRate,heightRate);
                return;
            }

            if(v instanceof ViewGroup){
                changeRelativeViewSize((ViewGroup)v, widthRate, heightRate);
            }else if(v instanceof RadioButton){ //
                ( (RadioButton)v ).setTextSize(TypedValue.COMPLEX_UNIT_PX,((RadioButton) v).getTextSize()*widthRate);
            }else if(v instanceof Button){
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) v.getLayoutParams();
                params.height= (int) (params.height*heightRate);
                params.width= (int) (params.width*widthRate);
                v.setLayoutParams(params);
                ( (Button)v ).setTextSize(TypedValue.COMPLEX_UNIT_PX,((Button) v).getTextSize()*widthRate);
            }else if(v instanceof TextView){
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) v.getLayoutParams();
                params.height= (int) (params.height*heightRate);
                params.width= (int) (params.width*widthRate);
                v.setLayoutParams(params);
                ( (TextView)v ).setTextSize(TypedValue.COMPLEX_UNIT_PX,((TextView) v).getTextSize()*widthRate);
            }
        }
    }

    public static void changeToolbarLayoutSize(ViewGroup viewGroup,float widthRate,float heightRate) {//傳入Activity頂層Layout,螢幕寬,螢幕高
        for(int i = 0; i<viewGroup.getChildCount(); i++ ){
            View v = viewGroup.getChildAt(i);

            if(v instanceof TextView){
                ( (TextView)v ).setTextSize(TypedValue.COMPLEX_UNIT_PX,((TextView) v).getTextSize()*widthRate);
            }
        }
    }

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
