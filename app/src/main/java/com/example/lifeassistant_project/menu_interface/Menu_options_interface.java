package com.example.lifeassistant_project.menu_interface;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;

public abstract class Menu_options_interface {
    protected String title_name;
    protected LinearLayout body_option_layout;
    protected LayoutInflater layoutInflater;
    protected View view;
    protected String bakcgroundcolor;

    protected TextView tv;
    protected ImageView imgV;
    protected Bitmap bmp;

    public Menu_options_interface(View view,LinearLayout layout,String title_name){
        this.title_name=title_name;
        this.view=view;
        this.layoutInflater = LayoutInflater.from(view.getContext());
    }

    public void createView(LinearLayout parent_layout,String backgroundcolor) {
        body_option_layout = (LinearLayout) layoutInflater.inflate(R.layout.menu_text_and_image,null);
        body_option_layout.setBackgroundColor(Color.parseColor(backgroundcolor));
        body_option_layout.setGravity(Gravity.CENTER);
        tv = (TextView) body_option_layout.findViewById(R.id.menu_option_textV);
        ClickToShow clickToShow=new ClickToShow();
        body_option_layout.setOnClickListener(clickToShow);
        tv.setText(title_name);
        tv.setTextSize(80);
        tv.setPadding(50,50,50,50);
        imgV=(ImageView) body_option_layout.findViewById(R.id.menu_option_imgV);
        imgV.setImageBitmap(bmp);
        imgV.setPadding(10,10,10,10);
        parent_layout.addView(body_option_layout);
    }

    private class ClickToShow implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            createSubOption();
        }
    }
    public abstract void createSubOption();
}
