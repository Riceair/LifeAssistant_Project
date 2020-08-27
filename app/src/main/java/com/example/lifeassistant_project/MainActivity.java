package com.example.lifeassistant_project;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.activity_update.*;
import com.example.lifeassistant_project.activity_update.chatbot.ChatbotBehavior;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.LoginPackage;
import com.example.lifeassistant_project.activity_update.packages.ReceiptPackage;
import com.example.lifeassistant_project.activity_update.static_handler.DatabaseBehavior;
import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.finance.invoice.Invoice_activity;
import com.example.lifeassistant_project.menu_activity.login.Login_activity;
import com.example.lifeassistant_project.menu_activity.login.Register_activity;
import com.example.lifeassistant_project.menu_activity.schedule.Planner_activity;
import com.example.lifeassistant_project.menu_activity.finance.report.Report_activity;
import com.example.lifeassistant_project.menu_activity.weather.Weather_activity;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final int REGISTER_CODE = 11;
    private static final int LOGIN_CODE = 12;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ChatbotBehavior chatbotBehavior;
    private ImageView userSayButton;
    private TextView userSay;
    private TextView chatBotSay;

    //註冊 登入
    private ImageView loginImg, regImg;
    private TextView regText,account_id;
    private RelativeLayout loginButton;

    private LocationManager locationManager;
    private Geocoder geocoder;
    private String adminArea=""; //高雄市 台北市

    private boolean isQuestion=false;
    private final String[] helpTitle={"如何使用","天氣指令","排程指令","記帳指令","報表指令","發票指令"};
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

        setLocationPermission();
        setHelp();

        // 資料庫
        File dbDir = new File(PATH, "databases");
        dbDir.mkdir();
        File FdbFile = new File(PATH+"/databases",DBNAME);
        if(!FdbFile.exists() || !FdbFile.isFile())
            copyAssets(PATH); //初始資料庫複製到路徑

        //語音TTS
        chatbotBehavior = new ChatbotBehavior();
        userSay=findViewById(R.id.userSay);
        chatBotSay=(TextView) findViewById(R.id.chatBotSay);
        userSayButton=findViewById(R.id.userSayButton);
        userSayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // for DEBUG
                DEBUG_FUNCTION(-1);

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

        bind();

        //Remeber user's content
        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
        if(shared.contains("username") && shared.contains("password")){
            System.out.println("User content:");
            System.out.println(shared.getString("username", "null"));
            System.out.println(shared.getString("password", "null"));
            LoginPackage loginPackage = new LoginPackage(shared.getString("username", "null"), shared.getString("password", "null"));
            LoginHandler.Login(loginPackage);
            setAfterLogin(shared.getString("username","null"));
        } else {
            System.out.println("There is no user's content!");
            setBeforeLogin();
        }
    }

    private void bind(){
        loginImg = navigationView.getHeaderView(0).findViewById(R.id.LoginImg);
        account_id = navigationView.getHeaderView(0).findViewById(R.id.account_id);
        regImg = navigationView.getHeaderView(0).findViewById(R.id.RegImg);
        regText = navigationView.getHeaderView(0).findViewById(R.id.RegText);
        loginButton = navigationView.getHeaderView(0).findViewById(R.id.LoginButton);
    }

    /////////////// Before Login ///////////////////////////////////
    private void setBeforeLogin(){
        //登入
        loginImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,Login_activity.class);
                MainActivity.this.startActivityForResult(intent,LOGIN_CODE);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,Login_activity.class);
                MainActivity.this.startActivityForResult(intent,LOGIN_CODE);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
            }
        });

        //註冊
        regImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this, Register_activity.class);
                MainActivity.this.startActivityForResult(intent,REGISTER_CODE);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
            }
        });
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, Register_activity.class);
                MainActivity.this.startActivityForResult(intent,REGISTER_CODE);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
            }
        });
        regImg.setVisibility(View.VISIBLE);
        regText.setText("尚未加入嗎？按此註冊");
        account_id.setText("按此登入");
    }

    private void setAfterLogin(String account){
        loginImg.setOnClickListener(null);
        loginButton.setOnClickListener(null);
        account_id.setText(account);
        regImg.setVisibility(View.INVISIBLE);
        regImg.setOnClickListener(null);
        regText.setText("登出");
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("確定登出").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Login out here

                        //After login out
                        setBeforeLogin();
                    }
                }).setNegativeButton("取消",null).show();
            }
        });
    }

    /////////////// DEBUG ///////////////////////////////////
    private void DEBUG_FUNCTION(int debugCode)
    {
        switch (debugCode)
        {
            case -1:
                break;
            case 0:
                DatabaseBehavior.synchronizeServer2Client_Account();
                break;
            case 1:
//          FOR CHATBOT DEBUG
//                TextView tempText = findViewById(R.id.DEBUG_TEXT);
//                System.out.println(tempText.getText());
//
//                userSay.setText(tempText.getText());
//
//                System.out.println("Behavior");
//                System.out.println(chatbotBehavior.getBehaviorMode());
//                if(!chatbotBehavior.generateSendSentence(tempText.getText().toString()))
//                {
//                    System.out.println(chatbotBehavior.getErrorMessage());
//                }
//                else
//                {
//                    chatbotBehavior.sendSentence();
//                }
//
//                chatBotSay.setText(chatbotBehavior.getResponse());
                break;
            case 2:
                System.out.println(LoginHandler.getUserKey());

                LoginHandler.setUserName("key");
                System.out.println(LoginHandler.checkAuthorization());
                break;
            case 3:
                ClientProgress client = new ClientProgress();
                client.setPackage(new ReceiptPackage());
                Thread cThread = new Thread(client);
                cThread.start();
                synchronized (client)
                {
                    try {
                        client.wait(5000);
                    }catch (InterruptedException e)
                    {
                        System.out.println(e);
                    }
                }

                ArrayList<DataPackage> tempList = client.getRcvPackageList();

                for(DataPackage r : tempList)
                {
                    ReceiptPackage tempR = (ReceiptPackage) r;
                    System.out.println(tempR.getYear());
                    System.out.println(tempR.getMonth());
                    for(String str : tempR.getHitNumber())
                    {
                        System.out.println(str);
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                userSay.setText(result.get(0));

//                System.out.println("Behavior");
//                System.out.println(chatbotBehavior.getBehaviorMode());
                if(!chatbotBehavior.generateSendSentence(result.get(0)))
                {
                    System.out.println(chatbotBehavior.getErrorMessage());
                }
                else
                {
                    chatbotBehavior.sendSentence();
                }

                chatBotSay.setText(chatbotBehavior.getResponse());
                //if operation is searching.
                if(chatbotBehavior.getSentenceHandler().getOperation() == 4)
                {
                    findViewById(R.id.popup_window).setVisibility(View.VISIBLE);
                    findViewById(R.id.assure_button).setVisibility(View.INVISIBLE);
                    findViewById(R.id.cancelButton).setVisibility(View.INVISIBLE);
                    findViewById(R.id.weather_condition).setVisibility(View.INVISIBLE);
                }
            }
        }
        else if(requestCode==REGISTER_CODE){
            if(resultCode==RESULT_OK){
                Bundle bundle = data.getExtras();
                String account=bundle.getString("ACCOUNT");
                String password=bundle.getString("PASSWORD");
                LoginPackage regloginpackage=new LoginPackage(account,password);
                if(LoginHandler.Login(regloginpackage))
                {
                    saveInformation(regloginpackage.getName(), regloginpackage.getPassword());
                    setAfterLogin(account);
                }
            }
        }
        else if(requestCode==LOGIN_CODE){
            if(resultCode==RESULT_OK){
                Bundle bundle=data.getExtras();
                String account=bundle.getString("ACCOUNT");
                Toast.makeText(this,"登入成功",Toast.LENGTH_SHORT).show();
                setAfterLogin(account);
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
                intent.putExtra("AdminArea",adminArea);
                this.startActivity(intent);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                break;
            case R.id.menu_fin_bookkeeping:
                intent=new Intent(this, Bookkeeping_activity.class);
                intent.putExtra("CALL","bookkeeping");
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

    ////////////////////////////////////////取得定位////////////////////////////////////////
    private void setLocationPermission(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder=new Geocoder(this, Locale.TRADITIONAL_CHINESE);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= 23) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        adminArea=getAddressByLocation(location);
    }

    private String getAddressByLocation(Location location){
        String sAddress="";
        try{
            if(location!=null){
                Double geoLongitude = location.getLongitude();
                Double geoLatitude = location.getLatitude();

                List<Address> lstAddress = geocoder.getFromLocation(geoLatitude,geoLongitude,1);
                sAddress=lstAddress.get(0).getAdminArea();
                //楠梓區getLocality() 台灣getCountryName 高雄市getAdminArea
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sAddress;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
    ////////////////////////////////////////取得定位////////////////////////////////////////

    //used for store user's account content.
    public void saveInformation(String username,String password) {
        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    //返回鍵
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

    //第一次開啟App才會啟用
    private void copyAssets(String path) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open(DBNAME);
            out = new FileOutputStream(PATH + "/databases/" + DBNAME);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * 一既有的工具程式，可將來源 InputStream 物件所指向的資料串流
     * 拷貝到OutputStream 物件所指向的資料串流去
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[in.available()];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
