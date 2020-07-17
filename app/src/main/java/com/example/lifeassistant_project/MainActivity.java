package com.example.lifeassistant_project;

import android.os.strictmode.CleartextNetworkViolation;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.menu_activity.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.Invoice_activity;
import com.example.lifeassistant_project.menu_activity.Planner_activity;
import com.example.lifeassistant_project.menu_activity.Report_activity;
import com.example.lifeassistant_project.menu_activity.Weather_activity;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //天氣 預測 理財 生活
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

//        this.DEBUG_FUNCTION();
    }
    private void DEBUG_FUNCTION()
    {
        ClientProgress client = new ClientProgress();
        client.setChatBot("我要記帳");
        Thread cThread = new Thread(client);
        cThread.start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_weather:
                intent=new Intent(this, Weather_activity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_predict_oil:
                Toast.makeText(this,"油價",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_fin_bookkeeping:
                intent=new Intent(this, Bookkeeping_activity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_fin_report:
                intent=new Intent(this, Report_activity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_fin_invoice:
                intent=new Intent(this, Invoice_activity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_schedule:
                intent=new Intent(this, Planner_activity.class);
                this.startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }


}
