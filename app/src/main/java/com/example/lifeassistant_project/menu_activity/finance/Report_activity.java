package com.example.lifeassistant_project.menu_activity.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.lifeassistant_project.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Report_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private PieChart mChart;
    private int currentYear,currentMonth;
    private Switch inoutSwitch;
    private int inOutAttribute=1; //收入或支出(資料庫Query)
    private int chYear,chMonth,chDay; //儲存選到的日期
    private String chMode="thisMonth"; //儲存menu選擇的mode lastYear thisYear lastMonth thisMonth all selfChoice
    private List<String> type_list = new ArrayList<>();
    private List<Integer> sum_list = new ArrayList<>();

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
        getSupportActionBar().setIcon(R.drawable.report);

        // 資料庫
        File dbDir = new File(PATH, "databases");
        dbDir.mkdir();
        File FdbFile = new File(PATH+"/databases",DBNAME);
        if(!FdbFile.exists() || !FdbFile.isFile())
            copyAssets(PATH); //初始資料庫複製到路徑

        //////////////////////////////報表API使用//////////////////////////////
        mChart=findViewById(R.id.pieChart);

        mChart.setUsePercentValues(true);  //使用百分比顯示
        mChart.getDescription().setEnabled(false);    //設置pipeChart圖表的描述
        //mChart.setBackgroundColor(Color.rgb(108,110,169)); //設置背景顏色
        mChart.setExtraOffsets(5, 5, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f); //設置pieChart圖表轉動阻力摩擦係數[0,1]
        mChart.setRotationAngle(0);                   //設置pieChart圖表起始角度
        mChart.setRotationEnabled(false);              //設置pieChart圖表是否可以手動旋轉
        mChart.setHighlightPerTapEnabled(false);       //設置piecahrt圖表點擊Item高亮是否可用
        //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);// 設置pieChart圖表展示動畫效果

// 設置 pieChart 圖表Item文字屬性
        mChart.setDrawEntryLabels(false);              //設置pieChart是否只顯示圓形圖上百分比不顯示文字（true：下面屬性才有效果）
        mChart.setEntryLabelColor(Color.BLACK);       //設置pieChart圖表文本字體顏色
        //mChart.setEntryLabelTypeface(mTfRegular);     //設置pieChart圖表文本字體樣式
        mChart.setEntryLabelTextSize(15f);            //設置pieChart圖表文本字體大小
        mChart.setDrawHoleEnabled(false);

// pieChart添加數據 title設置 底部資訊設置
        Calendar c = Calendar.getInstance();
        currentYear=c.get(Calendar.YEAR);
        currentMonth=c.get(Calendar.MONTH)+1;
        getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年"+String.valueOf(currentMonth)+"月");
        ReadDBRecord(currentYear,currentMonth,0);
        setData(mChart);
        setBotInf();

// 獲取pieCahrt圖例
        Legend l = mChart.getLegend();
        l.setEnabled(true);                    //是否啟用圖列（true：下面屬性才有意義）
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setForm(Legend.LegendForm.SQUARE); //設置圖例的形狀
        l.setFormSize(20);               //設置圖例的大小
        l.setFormToTextSpace(10f);         //設置每個圖例實體中標籤和形狀之間的間距
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);            //設置圖列換行(注意使用影響性能,僅適用legend位於圖表下面)
        l.setXEntrySpace(10f);            //設置圖例實體之間延X軸的間距（setOrientation = HORIZONTAL有效）
        l.setYEntrySpace(8f);             //設置圖例實體之間延Y軸的間距（setOrientation = VERTICAL 有效）
        l.setYOffset(0f);                //設置比例塊Y軸偏移量
        l.setTextSize(15f);                  //設置圖例標籤文本的大小
        l.setTextColor(Color.BLACK);//設置圖例標籤文本的顏色

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
                setData(mChart);
                setBotInf();
                break;
            case R.id.thisYear:
                chMode="thisYear";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年");
                ReadDBRecord(currentYear,0,0);
                setData(mChart);
                setBotInf();
                break;
            case R.id.lastMonth:
                chMode="lastMonth";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年"+String.valueOf(currentMonth-1)+"月");
                ReadDBRecord(currentYear,currentMonth-1,0);
                setData(mChart);
                setBotInf();
                break;
            case R.id.thisMonth:
                chMode="thisMonth";
                getSupportActionBar().setSubtitle("   "+String.valueOf(currentYear)+"年"+String.valueOf(currentMonth)+"月");
                ReadDBRecord(currentYear,currentMonth,0);
                setData(mChart);
                setBotInf();
                break;
            case R.id.all:
                chMode="all";
                getSupportActionBar().setSubtitle("   所有紀錄");
                ReadDBRecord(0,0,0);
                setData(mChart);
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
                        setData(mChart);
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
                setData(mChart);
                setBotInf();
                break;
            case "thisYear":
                ReadDBRecord(currentYear,0,0);
                setData(mChart);
                setBotInf();
                break;
            case "lastMonth":
                ReadDBRecord(currentYear,currentMonth-1,0);
                setData(mChart);
                setBotInf();
                break;
            case "thisMonth":
                ReadDBRecord(currentYear,currentMonth,0);
                setData(mChart);
                setBotInf();
                break;
            case "all":
                ReadDBRecord(0,0,0);
                setData(mChart);
                setBotInf();
                break;
            case "selfChoice":
                ReadDBRecord(chYear,chMonth,chDay);
                setData(mChart);
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

    //設置圓形圖的數據
    private void setData(PieChart mChart) {
        int length = type_list.size();
        ArrayList<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int i=0;i<length;i++){
            colors.add(randomColor());
        }
        // #FF8000 	#FF9224 #EA7500 #FFAF60 	#FF8040 #FF8F59 	#FF9D6F
        //圓形圖實體 PieEntry
        for (int i=0;i<length;i++){
            int value = sum_list.get(i);
            PieEntry pieEntry = new PieEntry(value,type_list.get(i));
            pieEntryList.add(pieEntry);
        }
        //餅狀圖資料集 PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setSliceSpace(3f);           //設置餅狀Item之間的間隙
        pieDataSet.setSelectionShift(10f);      //設置餅狀Item被選中時變化的距離
        pieDataSet.setColors(colors);           //為DataSet中的資料匹配上顏色集(圓形圖Item顏色)
        //最終資料 PieData
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);            //設置是否顯示資料實體(百分比，true:以下屬性才有意義)
        pieData.setValueTextColor(Color.WHITE);  //設置所有DataSet內資料實體（百分比）的文本顏色
        pieData.setValueTextSize(12f);          //設置所有DataSet內資料實體（百分比）的文本字體大小
        //pieData.setValueTypeface(mTfLight);     //設置所有DataSet內資料實體（百分比）的文本字體樣式
        pieData.setValueFormatter(new PercentFormatter());//設置所有DataSet內資料實體（百分比）的文本字體格式
        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();                    //將圖表重繪以顯示設定的屬性和資料
    }

    //設定底下顯示的資料
    private void setBotInf(){
        LinearLayout recordLinear = (LinearLayout) findViewById(R.id.record_list); //要顯示的layout
        recordLinear.removeAllViews();
        for(int i=0;i<type_list.size();i++){
            LinearLayout recordlist_element = (LinearLayout) getLayoutInflater().inflate(R.layout.report_recordlist_element,null);
            final TextView typeName = (TextView) recordlist_element.findViewById(R.id.record_type);
            TextView sum = (TextView) recordlist_element.findViewById(R.id.record_sum);
            typeName.setText(type_list.get(i));
            sum.setText(String.valueOf(sum_list.get(i)));
            recordlist_element.setOnClickListener(new View.OnClickListener() { //綁定依照類別查詢的Activity
                @Override
                public void onClick(View view) {
                    int transYear=0;
                    int transMonth=0;
                    int transDay=0;
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
                    intent.putExtra("YEAR",transYear);
                    intent.putExtra("MONTH",transMonth);
                    intent.putExtra("DAY",transDay);
                    Report_activity.this.startActivity(intent);
                    overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                }
            });
            recordLinear.addView(recordlist_element);
        }
    }


    //產生隨機顏色
    private static int randomColor(){
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.rgb(r,g,b);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
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
