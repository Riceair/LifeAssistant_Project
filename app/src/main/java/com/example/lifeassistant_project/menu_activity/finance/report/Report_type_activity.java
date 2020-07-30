package com.example.lifeassistant_project.menu_activity.finance.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Random;

public class Report_type_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private PieChart mChart;
    private int inOutAttribute; //收入或支出(資料庫Query)
    private String type;
    private int year,month,day;
    private List<String> detail_list=new ArrayList<>(); //細項List
    private List<Integer> sum_list=new ArrayList<>(); //金額List
    private List<Integer> color_default_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  報表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_report);

        setDefulatColor();
        //取得傳遞過來要Query的訊息
        Bundle bundle = getIntent().getExtras();
        type=bundle.getString("TYPE");
        inOutAttribute=bundle.getInt("inOutAttribute");
        year=bundle.getInt("YEAR");
        month=bundle.getInt("MONTH");
        day=bundle.getInt("DAY");

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

// pieChart添加數據 Subtitle設置 底部資訊設置
        if(year==0){
            getSupportActionBar().setSubtitle("  所有紀錄: "+type);
        }else if(month==0){
            getSupportActionBar().setSubtitle("  "+String.valueOf(year)+"年: "+type);
        }else if(day==0){
            getSupportActionBar().setSubtitle("  "+String.valueOf(year)+"年"+String.valueOf(month)+"月: "+type);
        }else{
            getSupportActionBar().setSubtitle("  "+String.valueOf(year)+"年"+String.valueOf(month)+"月"+String.valueOf(day)+"日: "+type);
        }
        ReadDBRecord(year,month,day);
        setData(mChart);
        setBotInf();

// 獲取pieChart圖例
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
    }

    ////////////////////////////////////////////////////數據處理///////////////////////////////////////////////////
    //讀資料庫
    private void ReadDBRecord(int year,int month,int day){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        detail_list.clear();
        sum_list.clear();
        try {
            if(month==0){
                if(year==0) //顯示所有紀錄
                    cursor = myDB.rawQuery("select record.細項, sum(record.金額) from record where record.收支屬性=? and record.分類=? group by record.細項"
                            ,new String[]{String.valueOf(inOutAttribute),type});
                else
                    cursor = myDB.rawQuery("select record.細項, sum(record.金額)  from record where record.年=? and record.收支屬性=? and record.分類=? group by record.細項"
                            ,new String[]{String.valueOf(year),String.valueOf(inOutAttribute),type});
            }else if(day!=0){ //自訂日期
                cursor = myDB.rawQuery("select record.細項, sum(record.金額)  from record where record.年=? and record.月=? and record.日=? and record.收支屬性=? and record.分類=? group by record.細項"
                        ,new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day),String.valueOf(inOutAttribute),type});
            }else {
                cursor = myDB.rawQuery("select record.細項, sum(record.金額)  from record where record.年=? and record.月=? and record.收支屬性=? and record.分類=? group by record.細項"
                        , new String[]{String.valueOf(year), String.valueOf(month),String.valueOf(inOutAttribute),type});
            }
            if(cursor!=null) { //取得資料
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for (int i=0;i<iRow;i++){
                    String type = cursor.getString(0);
                    int sum = cursor.getInt(1);
                    detail_list.add(type);
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
            Toast.makeText(this,"Query wrong",Toast.LENGTH_SHORT).show();
        }
    }

    //設置圓形圖的數據
    private void setData(PieChart mChart) {
        int length = detail_list.size();
        ArrayList<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if(i<color_default_list.size())
                colors.add(color_default_list.get(i));
            else
                colors.add(randomColor());
        }

        // #FF8000 	#FF9224 #EA7500 #FFAF60 	#FF8040 #FF8F59 	#FF9D6F
        //圓形圖實體 PieEntry
        for (int i=0;i<length;i++){
            int value = sum_list.get(i);
            PieEntry pieEntry = new PieEntry(value,detail_list.get(i));
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
        pieData.setValueTextColor(Color.BLACK);  //設置所有DataSet內資料實體（百分比）的文本顏色
        pieData.setValueTextSize(20f);          //設置所有DataSet內資料實體（百分比）的文本字體大小
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
        for(int i=0;i<detail_list.size();i++){
            LinearLayout recordlist_element = (LinearLayout) getLayoutInflater().inflate(R.layout.report_recordlist_element,null);
            final TextView detailName = (TextView) recordlist_element.findViewById(R.id.record_type);
            TextView sum = (TextView) recordlist_element.findViewById(R.id.record_sum);
            detailName.setText(detail_list.get(i));
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
                    Intent intent=new Intent(Report_type_activity.this,Report_type_detail_activity.class);
                    intent.putExtra("TYPE",type);
                    intent.putExtra("DETAIL",detailName.getText().toString());
                    intent.putExtra("inOutAttribute",inOutAttribute);
                    intent.putExtra("YEAR",year);
                    intent.putExtra("MONTH",month);
                    intent.putExtra("DAY",day);
                    Report_type_activity.this.startActivityForResult(intent,0);
                    overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                }
            });
            recordLinear.addView(recordlist_element);
            //新增底線
            ImageView segment=new ImageView(this);
            segment.setImageResource(R.drawable.segment);
            segment.setScaleType(ImageView.ScaleType.FIT_XY);
            recordLinear.addView(segment,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
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

    private void setDefulatColor() {
        color_default_list.add(Color.rgb(252,104,3));
        color_default_list.add(Color.rgb(139,101,8));
        color_default_list.add(Color.rgb(67,110,238));
        color_default_list.add(Color.rgb(34,139,34));
        color_default_list.add(Color.rgb(160,32,240));
        color_default_list.add(Color.rgb(65,105,225));
        color_default_list.add(Color.rgb(255,127,36));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Report_type_activity.this.finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReadDBRecord(year,month,day);
        setData(mChart);
        setBotInf();
        if(detail_list.size()==0){
            TextView no_data_text=findViewById(R.id.no_data_text);
            no_data_text.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}