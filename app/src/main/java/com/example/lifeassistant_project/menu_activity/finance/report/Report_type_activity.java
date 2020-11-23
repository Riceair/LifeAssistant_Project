package com.example.lifeassistant_project.menu_activity.finance.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.features_class.PieChartUsedClass;
import com.example.lifeassistant_project.R;
import com.github.mikephil.charting.charts.PieChart;
import java.util.ArrayList;
import java.util.List;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.addBaseline;
import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.changeLinearViewSize;

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
    private float heightRate,widthRate;


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
        findViewById(R.id.inoutText).setVisibility(View.INVISIBLE);
        setLayoutSize();
        setBotInf();
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


        //收支文字參數
        RelativeLayout inoutText=findViewById(R.id.inoutText);
        RelativeLayout.LayoutParams ioParm= (RelativeLayout.LayoutParams) inoutText.getLayoutParams();
        float sSParm = (float) 0.9;
        float tSParm = (float) 0.8;
        if(widthRate<=1.001 && widthRate>=0.999){
            sSParm = 1;
            tSParm = 1;
        }

        //switch按鈕
        Switch inoutSwitch=findViewById(R.id.inoutSwitch);
        RelativeLayout.LayoutParams switchParm= (RelativeLayout.LayoutParams) inoutSwitch.getLayoutParams();
        switchParm.rightMargin= (int) (ioParm.width*(widthRate-1)*0.5*sSParm);
        switchParm.bottomMargin=(int) (ioParm.height*(widthRate-1)*0.5*sSParm);
        inoutSwitch.setLayoutParams(switchParm);
        inoutSwitch.setScaleX(widthRate);
        inoutSwitch.setScaleY(widthRate);

        //收入支出文字
        ioParm.height= (int) (ioParm.height*widthRate*tSParm);
        ioParm.width= (int) (ioParm.width*widthRate*tSParm);
        inoutText.setLayoutParams(ioParm);

        TextView outText=findViewById(R.id.outSwitchText);
        ( (TextView)outText ).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (((TextView) outText).getTextSize()*widthRate*tSParm));
        TextView inText=findViewById(R.id.inSwitchText);
        ( (TextView)inText ).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (((TextView) inText).getTextSize()*widthRate*tSParm));

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

        changeLinearViewSize(recordLinear,widthRate,heightRate);
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