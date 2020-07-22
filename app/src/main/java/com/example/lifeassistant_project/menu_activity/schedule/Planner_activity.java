package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.menu_activity.finance.Report_type_activity;

public class Planner_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   排程");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.newstand);
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
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Planner_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}
