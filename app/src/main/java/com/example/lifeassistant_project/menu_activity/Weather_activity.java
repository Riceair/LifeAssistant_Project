package com.example.lifeassistant_project.menu_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.lifeassistant_project.R;

import java.util.*;

public class Weather_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏狀態列(綠色的那塊)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);  //全螢幕
        setContentView(R.layout.activity_weather_activity);
        ArrayList<String> weather_condition = new ArrayList();
        ArrayList<Integer> weather_degree_higher = new ArrayList();
        ArrayList<Integer> weather_degree_lower = new ArrayList();
        //123
        ArrayList weather_thismorning = new ArrayList<String>();
        weather_condition.add("晴時多雲");
        weather_degree_higher.add(27);
        weather_degree_lower.add(14);
        System.out.println("Contents of al: " +weather_thismorning.get(1) );
        TextView currentcondition = (TextView)findViewById(R.id.conditions);
        currentcondition.setText(weather_condition.get(0));






    }



}
