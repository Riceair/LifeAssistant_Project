package com.example.lifeassistant_project;

import android.accounts.Account;
import android.app.ActionBar;
import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.TypedValue;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.example.lifeassistant_project.activity_update.*;
import com.example.lifeassistant_project.activity_update.chatbot.ChatbotBehavior;
import com.example.lifeassistant_project.activity_update.packages.*;
import com.example.lifeassistant_project.activity_update.static_handler.DatabaseBehavior;
import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;
import com.example.lifeassistant_project.features_class.MainHelpItemOnClickListener;
import com.example.lifeassistant_project.features_class.PieChartUsedClass;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.finance.invoice.Invoice_activity;
import com.example.lifeassistant_project.menu_activity.login.Login_activity;
import com.example.lifeassistant_project.menu_activity.login.Register_activity;
import com.example.lifeassistant_project.menu_activity.schedule.Planner_activity;
import com.example.lifeassistant_project.menu_activity.finance.report.Report_activity;
import com.example.lifeassistant_project.menu_activity.schedule.ViewPlan_activity;
import com.example.lifeassistant_project.menu_activity.weather.Weather_activity;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.navigation.NavigationView;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.*;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final int REGISTER_CODE = 11;
    private static final int REGISTER_OK = 111;
    private static final int LOGIN_CODE = 12;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ChatbotBehavior chatbotBehavior;
    private ImageView userSayButton;
    private TextView userSay;
    private TextView chatBotSay;

    //機器人回應視窗
    private RelativeLayout popup_window,weather_response,account_cal_window, planner_window;
    private LinearLayout yes_no_response;
    private int popup_window_height,popup_window_width;
    private PieChart mChart;

    //註冊 登入
    private ImageView loginImg, regImg;
    private TextView regText,account_id;
    private RelativeLayout loginButton,login_bg;

    private LocationManager locationManager;
    private Geocoder geocoder;
    private String adminArea=""; //高雄市 台北市

    ArrayList<LinearLayout> all_help_linear=new ArrayList<>();
    ArrayList<MainHelpItemOnClickListener> helpItem_onclick_list=new ArrayList<>();

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

        setTTS();
        bind();

        popupGone();

        //Remember user's content
        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
        if(shared.contains("username") && shared.contains("password")){
            System.out.println("User content:");
            System.out.println(shared.getString("username", "null"));
            System.out.println(shared.getString("password", "null"));
            LoginPackage loginPackage = new LoginPackage(shared.getString("username", "null"), shared.getString("password", "null"));
            LoginHandler.setStoredUserName(shared.getString("username", "Null"));
            if(LoginHandler.Login(loginPackage))
            {
                setAfterLogin(shared.getString("username","null"));
            }
            else
            {
                if(LoginHandler.getResKey().equals("NO"))
                    Toast.makeText(this,"登入失敗！",Toast.LENGTH_SHORT).show();
                else if(LoginHandler.getResKey().equals("FA"))
                    Toast.makeText(this, LoginHandler.getErrorMessage(), Toast.LENGTH_SHORT).show();
                setBeforeLogin();
            }
        }
        else {
            System.out.println("There is no user's content!");
            setBeforeLogin();
        }

        //get weather data from server.
//        getWeatherData();
    }

    private void bind(){
        loginImg = navigationView.getHeaderView(0).findViewById(R.id.LoginImg);
        account_id = navigationView.getHeaderView(0).findViewById(R.id.account_id);
        regImg = navigationView.getHeaderView(0).findViewById(R.id.RegImg);
        regText = navigationView.getHeaderView(0).findViewById(R.id.RegText);
        loginButton = navigationView.getHeaderView(0).findViewById(R.id.LoginButton);
        login_bg = navigationView.getHeaderView(0).findViewById(R.id.LoginBackground);

        popup_window=findViewById(R.id.popup_window);
        weather_response=findViewById(R.id.weather_response);
        planner_window = findViewById((R.id.Planner_popup));
        account_cal_window=findViewById(R.id.AccountCalWindow);
        yes_no_response=findViewById(R.id.yes_no_response);
        mChart=findViewById(R.id.pieChart);

        popup_window_height= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        popup_window_width=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
    }

    private void setTTS(){
        //語音TTS
        chatbotBehavior = new ChatbotBehavior();
        userSay=findViewById(R.id.userSay);
        chatBotSay=(TextView) findViewById(R.id.chatBotSay);
        userSayButton=findViewById(R.id.userSayButton);
        userSayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // for DEBUG
                DEBUG_FUNCTION(1);

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
    }

    //記帳查詢
    private void popupShow(List<String> type_list,List<Integer> amount_list, String calType){
        setPopupDefaultSize();
        popup_window.setVisibility(View.VISIBLE);
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.alpha_scale_anim);
        animation.setDuration(1000);
        popup_window.startAnimation(animation);

        switch (calType) {
            case "def":
                mChart.setVisibility(View.VISIBLE);
                PieChartUsedClass pieChartUsedClass = new PieChartUsedClass(mChart, type_list, amount_list);
                break;
            case "sum":
                account_cal_window.setVisibility(View.VISIBLE);
                int incomeSum = 0, paySum = 0;
                for(Integer value : amount_list)
                {
                    if (value > 0)
                        incomeSum += value;
                    else
                        paySum -= value;
                }
                TextView tempText;
                int textColorCode = 0;
                tempText = findViewById(R.id.payMoney);
                tempText.setText("$" + Integer.toString(paySum));
                textColorCode = tempText.getCurrentTextColor();
                tempText = findViewById(R.id.incomeMoney);
                tempText.setText("$" + Integer.toString(incomeSum));
                if(incomeSum - paySum >= 0)
                    textColorCode = tempText.getCurrentTextColor();

                tempText = findViewById(R.id.sumMoney);
                tempText.setTextColor(textColorCode);
                tempText.setText("$" + Integer.toString(incomeSum - paySum));
                break;
            case "avg":

                break;
            default:
                break;
        }
    }

    //天氣
    private void popupShow(ArrayList<DataPackage> weatherData){
        setPopupDefaultSize();
        popup_window.setVisibility(View.VISIBLE);
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.alpha_scale_anim);
        animation.setDuration(1000);
        popup_window.startAnimation(animation);

        weather_response.setVisibility(View.VISIBLE);
        yes_no_response.setVisibility(View.INVISIBLE);

        // set default time to today.
        int ptr = (Integer.parseInt(chatbotBehavior.getSentenceHandler().getFulfillment().substring(0, 1))) * 2;
        WeatherPackage selectedWeather = (WeatherPackage) weatherData.get(ptr);
        TextView tempText;

        tempText = findViewById(R.id.weather_city_text);
        tempText.setText(selectedWeather.getCity());
        tempText = findViewById(R.id.weather_condition_text);
        tempText.setText(selectedWeather.getSituation());
        tempText = findViewById(R.id.weather_highest_text);
        tempText.setText(Integer.toString(selectedWeather.getMax_temperature()));
        tempText = findViewById(R.id.weather_lowest_text);
        tempText.setText(Integer.toString(selectedWeather.getMin_temperature()));

        ImageView conditionImg = findViewById(R.id.weather_condition);
        WeatherPackage.assignCondition2Image(selectedWeather.getSituation(), conditionImg);
    }

    //排程
    private void popupShow(List<String> todoList, String calType)
    {
        setPopupDefaultSize();
        popup_window.setVisibility(View.VISIBLE);
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.alpha_scale_anim);
        animation.setDuration(1000);
        popup_window.startAnimation(animation);

        planner_window.setVisibility(View.VISIBLE);
        ListView todoListView = findViewById(R.id.list);

        String[] detTodoList = new String[todoList.size()];
        detTodoList = todoList.toArray(detTodoList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detTodoList);
        todoListView.setAdapter(adapter);
    }

    //猜測意圖
    private void popupShow()
    {
        final Button ASSURE_BUTTON = findViewById(R.id.assure_button), CANCEL_BUTTON = findViewById(R.id.cancelButton);
        yes_no_response.setVisibility(View.VISIBLE);
        popup_window.setVisibility(View.VISIBLE);

        mChart.setVisibility(View.INVISIBLE);
        weather_response.setVisibility(View.INVISIBLE);
        //設定popup_window位置與大小
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) popup_window.getLayoutParams();
        params.height=getViewHeight(yes_no_response)+50;
        popup_window.setLayoutParams(params);
        popup_window.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));

        ASSURE_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatbotBehavior.setReadyMode();
                chatBotSay.setText(chatbotBehavior.getResponse());
                popupGone();
            }
        });

        CANCEL_BUTTON.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chatbotBehavior.setDefaultMode();
                chatBotSay.setText("好的，請您重新再說出您的需求。");
                popupGone();
            }
        });
    }

    private void popupGone(){
        popup_window.setVisibility(View.INVISIBLE);
        weather_response.setVisibility(View.INVISIBLE);
        planner_window.setVisibility(View.INVISIBLE);
        account_cal_window.setVisibility(View.INVISIBLE);
        yes_no_response.setVisibility(View.INVISIBLE);
        mChart.setVisibility(View.INVISIBLE);
    }

    private void setPopupDefaultSize(){
        //將popup_window大小調回預設
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) popup_window.getLayoutParams();
        params.height=popup_window_height;
        params.width=popup_window_width;
        popup_window.setLayoutParams(params);
        popup_window.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
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
        login_bg.setBackgroundResource(R.drawable.toggle_login);
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
        login_bg.setBackgroundResource(R.drawable.login_already);
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("確定登出").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Login out here
                        LoginHandler.Logout();
                        //After login out
                        setBeforeLogin();
                    }
                }).setNegativeButton("取消",null).show();
            }
        });
    }

    /////////////// Weather Data ///////////////////////////////////
    private ArrayList<DataPackage> getWeatherData()
    {
        if(WeatherPackage.getCurrentCity() == null)
        {
            SQLiteDatabase myDB;
            Cursor cursor;
            String currentCity = adminArea;

            if(currentCity.equals("")){ //定位失敗
                Toast.makeText(this,"請開啟定位以取得定位資訊",Toast.LENGTH_SHORT).show();
                myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null); //取得資料庫過去取得的位置資料
                try{
                    cursor = myDB.rawQuery("select Location from history_weather",null);
                    if(cursor!=null){
                        cursor.moveToFirst();
                        currentCity =cursor.getString(0);
                    }
                    myDB.close();
                }catch (Exception e){}
            }else if(currentCity.startsWith("台")){
                currentCity ="臺"+ currentCity.substring(1);
            }
            //將現在的位置設為過去的位置資料
            myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
            ContentValues values=new ContentValues();
            values.put("Location", currentCity);
            myDB.update("history_weather",values,"id=1",null);
            myDB.close();

            WeatherPackage.setCurrentCity(currentCity);
        }

        return WeatherPackage.getRcvWeatherData();
    }

    /////////////// DEBUG ///////////////////////////////////
    private void DEBUG_FUNCTION(int debugCode)
    {
        switch (debugCode)
        {
            case -1:
                break;
            case 0:
                DatabaseBehavior.synchronizeDataFromClient();
                break;
            case 1:
//          FOR CHATBOT DEBUG
                TextView tempText = findViewById(R.id.DEBUG_TEXT);
                System.out.println(tempText.getText());

                userSay.setText(tempText.getText());

//                System.out.println("Behavior");
//                System.out.println(chatbotBehavior.getBehaviorMode());
                if(!chatbotBehavior.generateSendSentence(tempText.getText().toString()))
                {
                    System.out.println(chatbotBehavior.getErrorMessage());
                }
                else
                {
                    chatbotBehavior.sendSentence();
                }

                chatBotSay.setText(chatbotBehavior.getResponse());

                subwindowHandle();
                break;
            case 2:
                System.out.println(LoginHandler.getUserKey());

                LoginHandler.setUserName("key");
                System.out.println(LoginHandler.checkAuthorization());
                break;
            case 3:
                ClientProgress client = new ClientProgress();
                client.setPackage(new ReceiptQRPackage());
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
                    ReceiptQRPackage tempR = (ReceiptQRPackage) r;
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
            if(!(resultCode == RESULT_OK && data != null))
                return;

            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            userSay.setText(result.get(0));

//                System.out.println("Behavior");
//                System.out.println(chatbotBehavior.getBehaviorMode());
            if(!chatbotBehavior.generateSendSentence(result.get(0)))
                System.out.println(chatbotBehavior.getErrorMessage());
            else
                chatbotBehavior.sendSentence();

            chatBotSay.setText(chatbotBehavior.getResponse());

            this.subwindowHandle();
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
            }else if(resultCode==REGISTER_OK){
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
    }

    private void subwindowHandle()
    {
        popupGone();
        if(!chatbotBehavior.ifNeedSubWindow())
            return;

        findViewById(R.id.popup_window).setVisibility(View.VISIBLE);
        if (chatbotBehavior.isSearchFlag())
        {
            //記帳 or 行程查詢
            List<String> itemList = new ArrayList<>();
            List<Integer> moneyList = new ArrayList<>();
            List<String> todoList = new ArrayList<>();
            String selectType = chatbotBehavior.getSelectType();
            int packageType = 0; // 0 for default, 1 for AccountPackage, 2 for SchedulePackage

            for (DataPackage ele : chatbotBehavior.getSelectedPackage())
            {
                if(packageType == 0)
                {
                    try {
                        AccountPackage rcvEle = (AccountPackage) ele;
                        packageType = 1;
                    }catch (ClassCastException e) {
                        SchedulePackage rcvEle = (SchedulePackage) ele;
                        packageType = 2;
                    }
                }

                if(packageType == 1)
                {
                    AccountPackage rcvEle = (AccountPackage) ele;
                    if(!rcvEle.getType() && selectType.equals("def"))
                        continue;

                    if(!selectType.equals("def"))
                        System.out.println("?????");
                    int moneyBuf = rcvEle.getMoney();
                    if(!selectType.equals("def") && rcvEle.getType())
                        moneyBuf *= -1;

                    if(!itemList.contains(rcvEle.getItem()))
                    {
                        itemList.add(rcvEle.getItem());
                        moneyList.add(moneyBuf);
                    }
                    else
                    {
                        int ptr = itemList.indexOf(rcvEle.getItem()), moneyBuf_t = moneyList.get(ptr);
                        if(!selectType.equals("def") && rcvEle.getType())
                            moneyBuf_t *= -1;

                        moneyList.set(ptr, moneyBuf + moneyBuf_t);
                    }
                }
                else if(packageType == 2)
                {
                    SchedulePackage rcvEle = (SchedulePackage) ele;
                    if(selectType.equals("def"))
                    {
                        todoList.add(rcvEle.getTodo());
                    }
                }
            }

            if(packageType == 1)
                popupShow(itemList, moneyList, chatbotBehavior.getSelectType());
            else if(packageType == 2)
                popupShow(todoList, chatbotBehavior.getSelectType());
        }
        else if (chatbotBehavior.getCurrentIntent() == 2 && chatbotBehavior.getCurrentOperation() == 4)
        {
            //行程查詢 dropped.
            yes_no_response.setVisibility(View.INVISIBLE);
            findViewById(R.id.weather_condition).setVisibility(View.INVISIBLE);


        }
        else if (chatbotBehavior.getCurrentIntent() == 3)
        {
            //猜測意圖
            popupShow();
        }
        else if (chatbotBehavior.getCurrentIntent() == 4)
        {
            //天氣
            popupShow(getWeatherData());
        }
    }

    //////////////////////////////說明提示////////////////////////////
    //help設置
    private void setHelp(){
        //重設
        LinearLayout help_list = findViewById(R.id.help_list);
        help_list.removeAllViews();

        ClickToGetHelp clickToGetHelp=new ClickToGetHelp();
        findViewById(R.id.chatBotHelp).setOnClickListener(clickToGetHelp);
        findViewById(R.id.chatBotHelpImg).setOnClickListener(clickToGetHelp);

        //返回鍵設置
        TextView help_back=new TextView(this);
        help_back.setTextColor(Color.WHITE);
        help_back.setGravity(Gravity.RIGHT);
        help_back.setText("返回");
        help_back.setTextSize(30f);
        help_back.setOnClickListener(new ClickToBackFromHelp());
        help_list.addView(help_back);


        for(int i=0;i<helpTitle.length;i++){
            LinearLayout element= (LinearLayout) getLayoutInflater().inflate(R.layout.main_help_element,null); //說明標題物件
            TextView help_title = element.findViewById(R.id.help_title);
            ImageView show_more = element.findViewById(R.id.show_more);

            LinearLayout main_help_element = new LinearLayout(this); //實際加入物件
            main_help_element.setOrientation(LinearLayout.VERTICAL);

            //將上下底線、說明標題物件加入實際加入物件
            if(i==0) {
                //新增上底線
                addBaseline(this,main_help_element,R.drawable.segment);
            }
            //Help element設置
            main_help_element.addView(element);
            //新增下底線
            addBaseline(this,main_help_element,R.drawable.segment_gray);

            //將實際加入物件加入Help list中
            help_list.addView(main_help_element);

            help_title.setText(helpTitle[i]);
            MainHelpItemOnClickListener helpOnClick=new MainHelpItemOnClickListener(this,help_list,show_more,new String[]{"說出我要記帳\n聊天機器人可能會幫你記帳","123","你好"});
            main_help_element.setOnClickListener(helpOnClick);
            all_help_linear.add(main_help_element);
            helpItem_onclick_list.add(helpOnClick);
        }

        //設定自訂義OnClickListener
        for(int i=0;i<all_help_linear.size();i++){
            helpItem_onclick_list.get(i).addBelowLinear(new ArrayList<LinearLayout>(all_help_linear.subList(i,all_help_linear.size())));
        }
    }

    //說明按鍵觸發
    private class ClickToGetHelp implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(isQuestion) return;
            isQuestion=true;
            //基本物件與天氣設為INVISIBLE 使用說明顯示VISIBLE
            findViewById(R.id.popup_window).setVisibility(View.INVISIBLE);
            findViewById(R.id.basic_item).setVisibility(View.GONE);
            findViewById(R.id.question_item).setVisibility(View.VISIBLE);

            //顯示動畫
            Animation anim_alpha = new AlphaAnimation(1.0f,0.0f);
            anim_alpha.setDuration(500);
            findViewById(R.id.basic_item).startAnimation(anim_alpha);

            Animation anim_transUp = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate_up);
            anim_transUp.setDuration(500);
            findViewById(R.id.question_item).startAnimation(anim_transUp);
        }
    }

    private class ClickToBackFromHelp implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(isQuestion){
                isQuestion=false;
                findViewById(R.id.basic_item).setVisibility(View.VISIBLE);
                findViewById(R.id.question_item).setVisibility(View.INVISIBLE);

                Animation anim_alpha = new AlphaAnimation(0.0f,1.0f);
                anim_alpha.setDuration(500);
                findViewById(R.id.basic_item).startAnimation(anim_alpha);

                Animation anim_transDown = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate_down);
                anim_transDown.setDuration(500);
                findViewById(R.id.question_item).startAnimation(anim_transDown);

                for(int i=0;i<helpItem_onclick_list.size();i++)
                    helpItem_onclick_list.get(i).reset();
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
    public void clickViewPlan(View view){

        Intent intent = new Intent(view.getContext(),Planner_activity.class);
        view.getContext().startActivity(intent);
        startActivityForResult(intent,0);
    }

}
