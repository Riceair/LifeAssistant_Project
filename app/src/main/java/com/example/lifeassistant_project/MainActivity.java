package com.example.lifeassistant_project;

import android.os.strictmode.CleartextNetworkViolation;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.SentenceHandler;
import com.example.lifeassistant_project.menu_activity.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.Invoice_activity;
import com.example.lifeassistant_project.menu_activity.Login_activity;
import com.example.lifeassistant_project.menu_activity.Planner_activity;
import com.example.lifeassistant_project.menu_activity.Report_activity;
import com.example.lifeassistant_project.menu_activity.Weather_activity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private ImageView userSayButton;
    private TextView userSay;
    private TextView chatBotSay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //天氣 預測 理財 生活
        setTitle("生活助理");
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

        //語音TTS
        userSay=findViewById(R.id.userSay);
        chatBotSay=(TextView) findViewById(R.id.chatBotSay);
        userSayButton=findViewById(R.id.userSayButton);
        userSayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說～");
                try{
                    startActivityForResult(intent,200);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DEBUG_FUNCTION();

        //登入
        View headerView=navigationView.getHeaderView(0);
        ImageView LoginImg=headerView.findViewById(R.id.LoginImg);
        LoginImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,Login_activity.class);
                MainActivity.this.startActivity(intent);

            }
        });
    }
    private void DEBUG_FUNCTION()
    {
        ClientProgress client = new ClientProgress();
        client.setChatBot("我要記帳");
        Thread cThread = new Thread(client);
        cThread.start();
    }
    private String dealSentenceAndRcvMessage(String message)
    {
        ClientProgress client = new ClientProgress();
        client.setChatBot(message);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client)
        {
            try {
                System.out.println("WAITTING");
                client.wait();
                System.out.println("GOGOGO");
            }
            catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        SentenceHandler handler = client.getRcvSentence();

        return handler.getFulfillment();
    }

    ////////////////////////////語音///////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                System.out.println(result.get(0));
                userSay.setText(result.get(0));
                chatBotSay.setText(dealSentenceAndRcvMessage(result.get(0)));
            }
        }
    }

    ///////////////////////////Navigation Drawer/////////////////////////////
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
