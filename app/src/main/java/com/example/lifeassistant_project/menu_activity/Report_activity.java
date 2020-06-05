package com.example.lifeassistant_project.menu_activity;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class Report_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";

    private SQLiteDatabase myDB;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏狀態列(綠色的那塊)
        setContentView(R.layout.activity_report_activity);
        Log.e("123","123");
        // 資料庫
        File FdbFile = new File(PATH, "databases");
        FdbFile.mkdir();
        File CdbFile = new File(PATH+"/databases",DBNAME);
        if(!CdbFile.exists() || !CdbFile.isFile())
            copyAssets(PATH,DBNAME); //初始資料庫複製到路徑

        ReadDBRecord();

        PieChart mChart=findViewById(R.id.pieChart);

        mChart.setUsePercentValues(true);  //使用百分比顯示
        mChart.getDescription().setEnabled(false);    //設置pipeChart圖表的描述
        mChart.setBackgroundColor(Color.rgb(108,110,169));
        mChart.setExtraOffsets(10, 10, 10, 10);
        mChart.setDragDecelerationFrictionCoef(0.95f); //設置pieChart圖表轉動阻力摩擦係數[0,1]
        mChart.setRotationAngle(0);                   //設置pieChart圖表起始角度
        mChart.setRotationEnabled(true);              //設置pieChart圖表是否可以手動旋轉
        mChart.setHighlightPerTapEnabled(true);       //設置piecahrt圖表點擊Item高亮是否可用
        //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);// 設置pieChart圖表展示動畫效果

// 設置 pieChart 圖表Item文字屬性
        mChart.setDrawEntryLabels(true);              //設置pieChart是否只顯示圓形圖上百分比不顯示文字（true：下面屬性才有效果）
        mChart.setEntryLabelColor(Color.WHITE);       //設置pieChart圖表文本字體顏色
        //mChart.setEntryLabelTypeface(mTfRegular);     //設置pieChart圖表文本字體樣式
        mChart.setEntryLabelTextSize(15f);            //設置pieChart圖表文本字體大小

        mChart.setDrawHoleEnabled(false);
/* 設置 pieChart 內部圓環屬性
        mChart.setDrawHoleEnabled(false);              //是否顯示PieChart內部圓環(true:下麵屬性才有意義)
        mChart.setHoleRadius(28f);                    //設置PieChart內部圓的半徑(這裡設置28.0f)
        mChart.setTransparentCircleRadius(31f);       //設置PieChart內部透明圓的半徑(這裡設置31.0f)
        mChart.setTransparentCircleColor(Color.BLACK);//設置PieChart內部透明圓與內部圓間距(31f-28f)填充顏色
        mChart.setTransparentCircleAlpha(50);         //設置PieChart內部透明圓與內部圓間距(31f-28f)透明度[0~255]數值越小越透明
        mChart.setHoleColor(Color.WHITE);             //設置PieChart內部圓的顏色
        mChart.setDrawCenterText(true);               //是否繪製PieChart內部中心文本（true：下麵屬性才有意義）
        //mChart.setCenterTextTypeface(mTfLight);       //設置PieChart內部圓文字的字體樣式
        //mChart.setCenterText("Test");                 //設置PieChart內部圓文字的內容
        mChart.setCenterTextSize(10f);                //設置PieChart內部圓文字的大小
        mChart.setCenterTextColor(Color.RED);         //設置PieChart內部圓文字的顏色
        */

// pieChart添加數據
        setData(mChart);

// 獲取pieCahrt圖例
        Legend l = mChart.getLegend();
        l.setEnabled(true);                    //是否啟用圖列（true：下麵屬性才有意義）
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setForm(Legend.LegendForm.SQUARE); //設置圖例的形狀
        l.setFormSize(20);               //設置圖例的大小
        l.setFormToTextSpace(10f);         //設置每個圖例實體中標籤和形狀之間的間距
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);            //設置圖列換行(注意使用影響性能,僅適用legend位於圖表下面)
        l.setXEntrySpace(10f);            //設置圖例實體之間延X軸的間距（setOrientation = HORIZONTAL有效）
        l.setYEntrySpace(8f);             //設置圖例實體之間延Y軸的間距（setOrientation = VERTICAL 有效）
        l.setYOffset(0f);                //設置比例塊Y軸偏移量
        l.setTextSize(14f);                  //設置圖例標籤文本的大小
        l.setTextColor(Color.parseColor("#ff9933"));//設置圖例標籤文本的顏色

//pieChart 選擇監聽
        // mChart.setOnChartValueSelectedListener(this);

//設置MARKERVIEW
        //CustomMarkerView mv = new CustomMarkerView(this, new PercentFormatter());
        //mv.setChartView(mChart);
        //mChart.setMarker(mv);
    }

    /**
     * 設置圓形圖的數據
     */
    private void setData(PieChart mChart) {
        ArrayList<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#f17548"));
        colors.add(Color.parseColor("#FF9933"));
        //圓形圖實體 PieEntry
        PieEntry CashBalance = new PieEntry(70, "現金餘額 1500");
        PieEntry ConsumptionBalance = new PieEntry(30, "消費餘額 768");
        pieEntryList.add(CashBalance);
        pieEntryList.add(ConsumptionBalance);
        //餅狀圖資料集 PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "資產總");
        pieDataSet.setSliceSpace(3f);           //設置餅狀Item之間的間隙
        pieDataSet.setSelectionShift(10f);      //設置餅狀Item被選中時變化的距離
        pieDataSet.setColors(colors);           //為DataSet中的資料匹配上顏色集(圓形圖Item顏色)
        //最終資料 PieData
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);            //設置是否顯示資料實體(百分比，true:以下屬性才有意義)
        pieData.setValueTextColor(Color.BLUE);  //設置所有DataSet內資料實體（百分比）的文本顏色
        pieData.setValueTextSize(12f);          //設置所有DataSet內資料實體（百分比）的文本字體大小
        //pieData.setValueTypeface(mTfLight);     //設置所有DataSet內資料實體（百分比）的文本字體樣式
        pieData.setValueFormatter(new PercentFormatter());//設置所有DataSet內資料實體（百分比）的文本字體格式
        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();                    //將圖表重繪以顯示設定的屬性和資料
    }

    ////////////////////////////////////////////讀資料庫///////////////////////////////////////////
    private void ReadDBRecord(){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.rawQuery("select record.分類, sum(record.金額)  from record,filter where filter.name = record.分類",null);
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();

                // 5. 關閉 DB
                myDB.close();
            }
            else {
                Toast.makeText(this,"Hint 1: 請將db準備好!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }


    //第一次開啟App才會啟用
    private void copyAssets(String path,String dbname) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open(dbname);
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
