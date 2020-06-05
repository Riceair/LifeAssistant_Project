package com.example.lifeassistant_project.menu_options;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.menu_interface.Menu_options_interface;


public class Predict_option extends Menu_options_interface {

    public Predict_option(View view, LinearLayout parent_layout){
        super(view,parent_layout,"預測");
        bmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.stocks);
        bmp= Bitmap.createScaledBitmap(bmp,400,400,false);
        createView(parent_layout,"#ff00ff");
    }

    @Override
    public void createSubOption() {
        Toast.makeText(view.getContext(),"Click is OK AAAA",Toast.LENGTH_SHORT).show();
    }
}
