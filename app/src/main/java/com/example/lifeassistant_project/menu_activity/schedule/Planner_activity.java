package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.lifeassistant_project.R;

public class Planner_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_planner_activity);
    }

    public void clickNewPlan(View view){
        Intent intent = new Intent(view.getContext(),NewPlan_activity.class);
        view.getContext().startActivity(intent);
    }

    public void clickTodayPlan(View view){
        Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
        view.getContext().startActivity(intent);
    }

    public void clickViewPlan(View view){
        Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
        view.getContext().startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}
