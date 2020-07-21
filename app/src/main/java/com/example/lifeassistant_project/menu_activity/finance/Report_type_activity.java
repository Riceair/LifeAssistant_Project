package com.example.lifeassistant_project.menu_activity.finance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.lifeassistant_project.R;

public class Report_type_activity extends AppCompatActivity {
    private String type;
    private int year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("報表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_report);

        Bundle bundle = getIntent().getExtras();
        type=bundle.getString("TYPE");
        year=bundle.getInt("YEAR");
        month=bundle.getInt("MONTH");
        day=bundle.getInt("DAY");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Report_type_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}