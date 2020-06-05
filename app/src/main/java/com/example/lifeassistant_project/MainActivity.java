package com.example.lifeassistant_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.lifeassistant_project.menu_options.FinMan_option;
import com.example.lifeassistant_project.menu_options.Life_option;
import com.example.lifeassistant_project.menu_options.Predict_option;
import com.example.lifeassistant_project.menu_options.Weather_option;


public class MainActivity extends AppCompatActivity {
    // 行事曆
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏狀態列(綠色的那塊)
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);  //全螢幕
        //天氣 預測 理財 生活
        View view = getLayoutInflater().inflate(R.layout.activity_main,null);
        LinearLayout parent_layout=(LinearLayout) view.findViewById(R.id.menu_linear);

        Weather_option wo=new Weather_option(view,parent_layout);
        Predict_option po=new Predict_option(view,parent_layout);
        FinMan_option fo=new FinMan_option(view,parent_layout);
        Life_option lo=new Life_option(view,parent_layout);
        setContentView(view);
    }
}
