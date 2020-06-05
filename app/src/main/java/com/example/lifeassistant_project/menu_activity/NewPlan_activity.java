package com.example.lifeassistant_project.menu_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lifeassistant_project.R;

public class NewPlan_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_plan_activity);
    }
}
