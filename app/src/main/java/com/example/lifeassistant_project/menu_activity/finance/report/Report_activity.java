package com.example.lifeassistant_project.menu_activity.finance.report;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.lifeassistant_project.features_class.PieChartUsedClass;
import com.example.lifeassistant_project.R;
import com.github.mikephil.charting.charts.PieChart;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.addBaseline;
import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.changeLinearViewSize;
import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.changeRelativeViewSize;

public class Report_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private PieChart mChart;
    private PieChartUsedClass pieChartUsedClass;
    private int currentYear,currentMonth;
    private Switch inoutSwitch;
    private int inOutAttribute=1; //收入或支出(資料庫Query)
    private int chYear,chMonth,chDay; //儲存選到的日期
    private String chMode="thisMonth"; //儲存menu選擇的mode lastYear thisYear lastMonth thisMonth all selfChoice
    private List<String> type_list = new ArrayList<>();
    private List<Integer> sum_list = new ArrayList<>();
    private List<Integer> color_default_list = new ArrayList<>();
    private int transYear,transMonth,transDay;
    private float heightRate,widthRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   報表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_report);

        //title設置 底部資訊設置
        Calendar c = Calendar.getInstance();
        currentYear=c.get(Calendar.YEAR);
        currentMonth=c.get(Calendar.MONTH)+1;
        getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年"+String.valueOf(currentMonth)+"月");
        ReadDBRecord(currentYear,currentMonth,0);

        //pieChart設定
        mChart=findViewById(R.id.pieChart);
        pieChartUsedClass=new PieChartUsedClass(mChart,type_list,sum_list);

        setBotInf();
        setSwitch();
        setLayoutSize();
    }

    private void setLayoutSize(){
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        heightRate=dm.heightPixels/(float)1920;
        widthRate=dm.widthPixels/(float)1080;

        //報表本體
        RelativeLayout mainly=findViewById(R.id.mainly);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mainly.getLayoutParams();
        params.height= (int) (params.height*heightRate);
        params.width= (int) (params.width*widthRate);
        mainly.setLayoutParams(params);

        //底部資訊
        ScrollView reportScroll=findViewById(R.id.reportScroll);
        RelativeLayout.LayoutParams sp= (RelativeLayout.LayoutParams) reportScroll.getLayoutParams();
        sp.height= (int) (sp.height*heightRate*(float)0.9);
        reportScroll.setLayoutParams(sp);

        //無資料文字顯示
        TextView no_data_text=findViewById(R.id.no_data_text);
        no_data_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,no_data_text.getTextSize()*widthRate);
        RelativeLayout.LayoutParams ntPar=(RelativeLayout.LayoutParams) no_data_text.getLayoutParams();
        ntPar.bottomMargin=(int) (sp.height*(float)0.3);
        no_data_text.setLayoutParams(ntPar);

        //switch按鈕
        Switch inoutSwitch=findViewById(R.id.inoutSwitch);
        RelativeLayout.LayoutParams switchParm=(RelativeLayout.LayoutParams) inoutSwitch.getLayoutParams();

        inoutSwitch.setScaleX(widthRate);
        inoutSwitch.setScaleY(widthRate);

    }

    private void setSwitch(){
        //////////////////////////////////////收入,支出切換//////////////////////////////////////
        final TextView outSwitchText=findViewById(R.id.outSwitchText);
        final TextView inSwitchText=findViewById(R.id.inSwitchText);
        inoutSwitch=findViewById(R.id.inoutSwitch);
        inoutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean IsCheck) {
                if(IsCheck) { //選擇收入
                    inSwitchText.setVisibility(View.VISIBLE);
                    outSwitchText.setVisibility(View.INVISIBLE);
                    inOutAttribute=0;
                }else { //選擇支出
                    inSwitchText.setVisibility(View.INVISIBLE);
                    outSwitchText.setVisibility(View.VISIBLE);
                    inOutAttribute=1;
                }
                InOutModeChange();
            }
        });
    }

    ////////////////////////////////////////////////////menu處理///////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.report_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.lastYear:
                chMode="lastYear";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear-1)+"年");
                ReadDBRecord(currentYear-1,0,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case R.id.thisYear:
                chMode="thisYear";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年");
                ReadDBRecord(currentYear,0,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case R.id.lastMonth:
                chMode="lastMonth";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年"+String.valueOf(currentMonth-1)+"月");
                ReadDBRecord(currentYear,currentMonth-1,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case R.id.thisMonth:
                chMode="thisMonth";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年"+String.valueOf(currentMonth)+"月");
                ReadDBRecord(currentYear,currentMonth,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case R.id.all:
                chMode="all";
                getSupportActionBar().setSubtitle("   所有紀錄");
                ReadDBRecord(0,0,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case R.id.selfChoice:
                chMode="selfChoice";
                int mYear,mMonth,mDay;
                final Calendar c=Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(Report_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        chYear=year;
                        chMonth=month+1;
                        chDay=day;
                        getSupportActionBar().setSubtitle("   "+String.valueOf(chYear)+"年"+String.valueOf(chMonth)+"月"+String.valueOf(chDay)+"日");
                        ReadDBRecord(year,month+1,day);
                        pieChartUsedClass.setMChartData(type_list,sum_list);
                        setBotInf();
                    }
                },mYear,mMonth,mDay).show();
                break;
            case android.R.id.home:
                Report_activity.this.finish();
                break;
        }
        return true;
    }
    ////////////////////////////////////////////////////menu處理///////////////////////////////////////////////////

    ////////////////////////////////////////////////////收入 支出切換///////////////////////////////////////////////////
    private void InOutModeChange(){
        switch (chMode){
            case "lastYear":
                ReadDBRecord(currentYear-1,0,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case "thisYear":
                ReadDBRecord(currentYear,0,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case "lastMonth":
                ReadDBRecord(currentYear,currentMonth-1,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case "thisMonth":
                ReadDBRecord(currentYear,currentMonth,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case "all":
                ReadDBRecord(0,0,0);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
            case "selfChoice":
                ReadDBRecord(chYear,chMonth,chDay);
                pieChartUsedClass.setMChartData(type_list,sum_list);
                setBotInf();
                break;
        }
    }
    ////////////////////////////////////////////////////收入 支出切換///////////////////////////////////////////////////

    ////////////////////////////////////////////////////數據處理///////////////////////////////////////////////////
    //讀資料庫
    private void ReadDBRecord(int year,int month,int day){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        type_list.clear();
        sum_list.clear();
        try {
            if(month==0){
                if(year==0) //顯示所有紀錄
                    cursor = myDB.rawQuery("select record.分類, sum(record.金額)  from record where record.收支屬性=? group by record.分類"
                            ,new String[]{String.valueOf(inOutAttribute)});
                else
                    cursor = myDB.rawQuery("select record.分類, sum(record.金額)  from record where record.年=? and record.收支屬性=? group by record.分類"
                            ,new String[]{String.valueOf(year),String.valueOf(inOutAttribute)});
            }else if(day!=0){ //自訂日期
                cursor = myDB.rawQuery("select record.分類, sum(record.金額)  from record where record.年=? and record.月=? and record.日=? and record.收支屬性=? group by record.分類"
                        ,new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day),String.valueOf(inOutAttribute)});
            }else {
                cursor = myDB.rawQuery("select record.分類, sum(record.金額)  from record where record.年=? and record.月=? and record.收支屬性=? group by record.分類"
                        , new String[]{String.valueOf(year), String.valueOf(month),String.valueOf(inOutAttribute)});
            }
            if(cursor!=null) { //取得資料
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for (int i=0;i<iRow;i++){
                    String type = cursor.getString(0);
                    int sum = cursor.getInt(1);
                    type_list.add(type);
                    sum_list.add(sum);
                    cursor.moveToNext();
                }
                // 5. 關閉 DB
                myDB.close();
            }
            else {
                Toast.makeText(this,"Hint 1: 請將db準備好!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Log.e("SQL wrong",e.toString());
        }
    }

    //設定底下顯示的資料
    private void setBotInf(){
        //無資料設定
        TextView noDataText=findViewById(R.id.no_data_text);
        if(type_list.isEmpty())
            noDataText.setVisibility(View.VISIBLE);
        else
            noDataText.setVisibility(View.INVISIBLE);

        //底部資料設定
        LinearLayout recordLinear = (LinearLayout) findViewById(R.id.record_list); //要顯示的layout
        recordLinear.removeAllViews();
        for(int i=0;i<type_list.size();i++){
            LinearLayout recordlist_element = (LinearLayout) getLayoutInflater().inflate(R.layout.report_recordlist_element,null);
            final TextView typeName = (TextView) recordlist_element.findViewById(R.id.record_type);
            TextView sum = (TextView) recordlist_element.findViewById(R.id.record_sum);
            typeName.setText(type_list.get(i));
            if(inOutAttribute==1){ //支出
                sum.setText("-$"+String.valueOf(sum_list.get(i)));
                sum.setTextColor(Color.RED);
            }else{ //收入
                sum.setText("+$"+String.valueOf(sum_list.get(i)));
                sum.setTextColor(Color.rgb(4,117,2));
            }
            recordlist_element.setOnClickListener(new View.OnClickListener() { //綁定依照類別查詢的Activity
                @Override
                public void onClick(View view) {
                    transYear=0;
                    transMonth=0;
                    transDay=0;
                    switch (chMode){
                        case "lastYear":
                            transYear=currentYear-1;
                            transMonth=0;
                            transDay=0;
                            break;
                        case "thisYear":
                            transYear=currentYear;
                            transMonth=0;
                            transDay=0;
                            break;
                        case "lastMonth":
                            transYear=currentYear;
                            transMonth=currentMonth-1;
                            transDay=0;
                            break;
                        case "thisMonth":
                            transYear=currentYear;
                            transMonth=currentMonth;
                            transDay=0;
                            break;
                        case "all":
                            transYear=0;
                            transMonth=0;
                            transDay=0;
                            break;
                        case "selfChoice":
                            transYear=chYear;
                            transMonth=chMonth;
                            transDay=chDay;
                            break;
                    }
                    Intent intent=new Intent(Report_activity.this,Report_type_activity.class);
                    intent.putExtra("TYPE",typeName.getText().toString());
                    intent.putExtra("inOutAttribute",inOutAttribute);
                    intent.putExtra("YEAR",transYear);
                    intent.putExtra("MONTH",transMonth);
                    intent.putExtra("DAY",transDay);
                    Report_activity.this.startActivityForResult(intent,0);
                    overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                }
            });
            recordLinear.addView(recordlist_element);
            //新增底線
            addBaseline(this,recordLinear,R.drawable.segment);
        }

        changeLinearViewSize(recordLinear,widthRate,heightRate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReadDBRecord(transYear,transMonth,transDay);
        pieChartUsedClass.setMChartData(type_list,sum_list);
        setBotInf();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}
