package com.example.lifeassistant_project;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.activity_update.*;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.finance.Invoice_activity;
import com.example.lifeassistant_project.menu_activity.login.Login_activity;
import com.example.lifeassistant_project.menu_activity.schedule.Planner_activity;
import com.example.lifeassistant_project.menu_activity.finance.Report_activity;
import com.example.lifeassistant_project.menu_activity.weather.Weather_activity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ChatbotBehavior chatbotBehavior;

    private ImageView userSayButton;
    private TextView userSay;
    private TextView chatBotSay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //天氣 預測 理財 生活
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("生活助理");
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        //語音TTS
        chatbotBehavior = new ChatbotBehavior();
        userSay=findViewById(R.id.userSay);
        chatBotSay=(TextView) findViewById(R.id.chatBotSay);
        userSayButton=findViewById(R.id.userSayButton);
        userSayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                DEBUG_FUNCTION();
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

        //popup window hidden
        findViewById(R.id.popup_window).setVisibility(View.INVISIBLE);

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
//        ClientProgress client = new ClientProgress();
//        client.setChatBot("我要記帳");
//        Thread cThread = new Thread(client);
//        cThread.start();

//        System.out.println(LoginPackage.getUserKey());

        DatabaseBehavior.synchronizeServer2Client();
    }

    ////////////////////////////語音///////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                userSay.setText(result.get(0));

                System.out.println("Behavior");
                System.out.println(chatbotBehavior.getBehaviorMode());
                if(!chatbotBehavior.generateSendSentence(result.get(0)))
                {
                    System.out.println(chatbotBehavior.getErrorMessage());
                }
                else
                {
                    chatbotBehavior.sendSentence();
                }

                chatBotSay.setText(chatbotBehavior.getSentenceHandler().getFulfillment());
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
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                break;
            case R.id.menu_predict_oil:
                Toast.makeText(this,"油價",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_fin_bookkeeping:
                intent=new Intent(this, Bookkeeping_activity.class);
                this.startActivity(intent);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                break;
            case R.id.menu_fin_report:
                intent=new Intent(this, Report_activity.class);
                this.startActivity(intent);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                break;
            case R.id.menu_fin_invoice:
                intent=new Intent(this, Invoice_activity.class);
                this.startActivity(intent);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                break;
            case R.id.menu_schedule:
                intent=new Intent(this, Planner_activity.class);
                this.startActivity(intent);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
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
