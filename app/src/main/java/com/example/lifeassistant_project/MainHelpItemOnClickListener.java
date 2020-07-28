package com.example.lifeassistant_project;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainHelpItemOnClickListener implements View.OnClickListener {
    private Context MainActivity;
    private LinearLayout help_list;
    private ImageView shore_more;
    private LinearLayout LinearHelpDetail;
    private String help_detail[];
    private boolean isClick;

    public MainHelpItemOnClickListener(Context MainActivity, LinearLayout help_list, ImageView shore_more, String help_detail[]){
        this.MainActivity=MainActivity;
        this.help_list=help_list;  //detail要加的位置
        this.shore_more=shore_more;  //箭號
        this.help_detail=help_detail;  //要加的文字
        isClick=false;
        setDetail();
    }

    private void setDetail() {
        LinearHelpDetail = new LinearLayout(MainActivity);
        LinearHelpDetail.setOrientation(LinearLayout.VERTICAL);
        LinearHelpDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //文字加入
        for(int i=0;i<help_detail.length;i++){
            TextView detail=new TextView(MainActivity);
            detail.setText(help_detail[i]);
            detail.setGravity(Gravity.CENTER);
            detail.setTextColor(Color.WHITE);
            detail.setTextSize(20f);

            LinearHelpDetail.addView(detail);

            //新增下底線
            ImageView segment=new ImageView(MainActivity);
            segment.setImageResource(R.drawable.segment_gray);
            segment.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearHelpDetail.addView(segment,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        help_list.addView(LinearHelpDetail);
        LinearHelpDetail.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(!isClick){
            isClick=true;
            shore_more.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);

            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(200);
            LinearHelpDetail.startAnimation(mShowAction);
            LinearHelpDetail.setVisibility(View.VISIBLE);
        }else{
            isClick=false;
            shore_more.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
            LinearHelpDetail.setVisibility(View.GONE);
        }
    }
}
