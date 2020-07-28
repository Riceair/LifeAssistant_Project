package com.example.lifeassistant_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.activity_update.*;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.finance.Invoice_activity;
import com.example.lifeassistant_project.menu_activity.login.Login_activity;
import com.example.lifeassistant_project.menu_activity.login.register_activity;
import com.example.lifeassistant_project.menu_activity.schedule.Planner_activity;
import com.example.lifeassistant_project.menu_activity.finance.report.Report_activity;
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

    private boolean isQuestion=false;
    private final String[] helpTitle={"如何使用","天氣指令","排成指令","記帳指令","報表指令","發票指令"};
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

        setHelp();

        //語音TTS
        chatbotBehavior = new ChatbotBehavior();
        userSay=findViewById(R.id.userSay);
        chatBotSay=(TextView) findViewById(R.id.chatBotSay);
        userSayButton=findViewById(R.id.userSayButton);
        userSayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DEBUG_FUNCTION();
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
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
            }
        });
        //註冊

        ImageView RegImg=headerView.findViewById(R.id.RegImg);
        RegImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,register_activity.class);
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

    //////////////////////////////說明提示////////////////////////////
    //help設置
    private void setHelp(){
        //重設
        LinearLayout help_list = findViewById(R.id.help_list);
        help_list.removeAllViews();

        //返回鍵設置
        TextView help_back=new TextView(this);
        help_back.setTextColor(Color.WHITE);
        help_back.setGravity(Gravity.RIGHT);
        help_back.setText("返回");
        help_back.setTextSize(30f);
        help_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        help_list.addView(help_back);

        for(int i=0;i<helpTitle.length;i++){
            LinearLayout main_help_element= (LinearLayout) getLayoutInflater().inflate(R.layout.main_help_element,null);
            TextView help_title = main_help_element.findViewById(R.id.help_title);
            final ImageView show_more = main_help_element.findViewById(R.id.show_more);

            //新增上底線
            ImageView seg=new ImageView(this);
            seg.setImageResource(R.drawable.segment);
            seg.setScaleType(ImageView.ScaleType.FIT_XY);
            help_list.addView(seg,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            //Help element加入
            help_list.addView(main_help_element);
            //新增下底線
            ImageView segment=new ImageView(this);
            segment.setImageResource(R.drawable.segment_gray);
            segment.setScaleType(ImageView.ScaleType.FIT_XY);
            help_list.addView(segment,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            help_title.setText(helpTitle[i]);
            main_help_element.setOnClickListener(new MainHelpItemOnClickListener(this,help_list,show_more,new String[]{"說出我要記帳\n聊天機器人可能會幫你記帳","123","你好"}));
        }
    }

    //說明按鍵觸發
    public void clickToGetHelp(View view){
        isQuestion=true;
        //基本物件與天氣設為INVISIBLE 使用說明顯示VISIBLE
        findViewById(R.id.popup_window).setVisibility(View.INVISIBLE);
        findViewById(R.id.basic_item).setVisibility(View.INVISIBLE);
        findViewById(R.id.question_item).setVisibility(View.VISIBLE);

        //顯示動畫
        Animation animation = new AlphaAnimation(1.0f,0.0f);
        animation.setDuration(500);
        findViewById(R.id.basic_item).setAnimation(animation);
        animation.startNow();

        animation = AnimationUtils.loadAnimation(this,R.anim.translate_up);
        findViewById(R.id.question_item).setAnimation(animation);
        animation.startNow();
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
        }else if(isQuestion){
            isQuestion=false;
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate_down);
            findViewById(R.id.question_item).setAnimation(animation);
            findViewById(R.id.question_item).setVisibility(View.INVISIBLE);
            animation.startNow();

            animation = new AlphaAnimation(0.0f,1.0f);
            animation.setDuration(1000);
            findViewById(R.id.basic_item).setAnimation(animation);
            findViewById(R.id.basic_item).setVisibility(View.VISIBLE);
            animation.startNow();

            setHelp();
        }else{
            super.onBackPressed();
        }
    }
}
