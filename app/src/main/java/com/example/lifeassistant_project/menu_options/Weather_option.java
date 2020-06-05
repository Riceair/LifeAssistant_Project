package com.example.lifeassistant_project.menu_options;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.menu_activity.Weather_activity;
import com.example.lifeassistant_project.menu_interface.Menu_options_interface;


public class Weather_option extends Menu_options_interface {

    public Weather_option(View view, LinearLayout parent_layout){
        super(view,parent_layout,"天氣");
        bmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.weather);
        bmp= Bitmap.createScaledBitmap(bmp,400,400,false);
        createView(parent_layout,"#00c3ff");
    }

    @Override
    public void createSubOption() {
        Intent intent = new Intent(view.getContext(), Weather_activity.class);
        view.getContext().startActivity(intent);
    }
}
