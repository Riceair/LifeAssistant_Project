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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.features_class.PieChartUsedClass;
import com.example.lifeassistant_project.R;
import com.github.mikephil.charting.charts.PieChart;
import java.util.ArrayList;
import java.util.List;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.addBaseline;

public class Report_type_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private PieChart mChart;
    private PieChartUsedClass pieChartUsedClass;
    private int inOutAttribute; //收入或支出(資料庫Query)
    private String type;
    private int year,month,day;
    private List<String> detail_list=new ArrayList<>(); //細項List
    private List<Integer> sum_list=new ArrayList<>(); //金額List


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  報表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_report);
        findViewById(R.id.inoutSwitch).setVisibility(View.GONE);

        //取得傳遞過來要Query的訊息
        Bundle bundle = getIntent().getExtras();
        type=bundle.getString("TYPE");
        inOutAttribute=bundle.getInt("inOutAttribute");
        year=bundle.getInt("YEAR");
        month=bundle.getInt("MONTH");
        day=bundle.getInt("DAY");


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

        mChart=findViewById(R.id.pieChart);
        pieChartUsedClass=new PieChartUsedClass(mChart,detail_list,sum_list);
        setBotInf();
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
            addBaseline(this,recordLinear,R.drawable.segment);
        }
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
        pieChartUsedClass.setMChartData(detail_list,sum_list);
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