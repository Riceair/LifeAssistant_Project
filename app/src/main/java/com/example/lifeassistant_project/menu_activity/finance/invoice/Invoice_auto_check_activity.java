package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.AccountPackage;
import com.example.lifeassistant_project.activity_update.packages.ReceiptQRPackage;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.*;

public class Invoice_auto_check_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private ArrayList<ArrayList<AccountPackage>> allReward=new ArrayList<>();
    private ArrayList<AccountPackage> specialReward=new ArrayList<>();
    private ArrayList<AccountPackage> spReward=new ArrayList<>();
    private ArrayList<AccountPackage> hitReward=new ArrayList<>();
    private ArrayList<AccountPackage> sixReward=new ArrayList<>();
    private boolean isInvoiceUpdate;
    private ReceiptQRPackage recepitContainerPre,recepitContainerRec;
    List<Integer> amount_list=new ArrayList<>();
    List<Integer> year_list=new ArrayList<>();
    List<Integer> month_list=new ArrayList<>();
    List<Integer> day_list=new ArrayList<>();
    List<String> detail_list=new ArrayList<>();
    List<String> invoice_list=new ArrayList<>();
    List<Integer> id_list=new ArrayList<>();
    int rec_index;
    private static final String[] reward_names={"注意特別獎","注意特獎","中獎","六獎"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type_detail_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   自動兌獎結果");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.receipt_checksum);

        Bundle bundle=getIntent().getExtras();
        isInvoiceUpdate=bundle.getBoolean("isInvoiceUpdate");
        recepitContainerPre=new Gson().fromJson(bundle.getString("recepitContainerPre"), ReceiptQRPackage.class);
        recepitContainerRec= new Gson().fromJson(bundle.getString("recepitContainerRec"), ReceiptQRPackage.class);

        if(!isInvoiceUpdate) Toast.makeText(this,"尚未取得兌獎資訊",Toast.LENGTH_SHORT).show();
        else readData();
        AutoCheck();
        setLayout();
        setLayoutSize();
    }

    private void readData(){
        id_list.clear();
        invoice_list.clear();
        detail_list.clear();
        amount_list.clear();
        year_list.clear();
        month_list.clear();
        day_list.clear();
        rec_index=0;
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try{
            //將上一期的發票讀入
            cursor=myDB.rawQuery("select record.金額, record.年, record.月, record.日, record.細項, record.發票, record.id "+
                            "from record where record.收支屬性=? and record.年=? and (record.月=? or record.月=?) and record.發票 is not null",
                            new String[]{String.valueOf(1),recepitContainerPre.getYear(),recepitContainerPre.getMonth(),
                            String.valueOf(Integer.valueOf(recepitContainerPre.getMonth())+1)});
            if(cursor!=null){
                int iRow=cursor.getCount();
                rec_index=iRow;
                cursor.moveToFirst();
                addListData(cursor,iRow);
            }

            //將近期的發票讀入
            cursor=myDB.rawQuery("select record.金額, record.年, record.月, record.日, record.細項, record.發票, record.id "+
                            "from record where record.收支屬性=? and record.年=? and (record.月=? or record.月=?) and record.發票 is not null",
                            new String[]{String.valueOf(1),recepitContainerRec.getYear(),recepitContainerRec.getMonth(),
                            String.valueOf(Integer.valueOf(recepitContainerRec.getMonth())+1)});
            if(cursor!=null){
                int iRow=cursor.getCount();
                cursor.moveToFirst();
                addListData(cursor,iRow);
            }
            myDB.close();
        }catch (Exception e) {
            Log.e("Query Error",e.toString());
        }
    }

    private void addListData(Cursor cursor,int iRow){
        for(int i=0;i<iRow;i++){
            int amount = cursor.getInt(0);
            int dbYear = cursor.getInt(1);
            int dbMonth = cursor.getInt(2);
            int dbDay = cursor.getInt(3);
            String dbDetail = cursor.getString(4);
            String dbInvoice = cursor.getString(5);
            int id = cursor.getInt(6);
            amount_list.add(amount);
            year_list.add(dbYear);
            month_list.add(dbMonth);
            day_list.add(dbDay);
            detail_list.add(dbDetail);
            invoice_list.add(dbInvoice);
            id_list.add(id);
            cursor.moveToNext();
        }
    }

    private void AutoCheck(){
        for(int i=0;i<id_list.size();i++){
            int result=4;
            if(i<rec_index)
                result=checkReward(invoice_list.get(i),recepitContainerPre);
            else
                result=checkReward(invoice_list.get(i),recepitContainerRec);
            if(result!=4){
                AccountPackage accountPackage=new AccountPackage(id_list.get(i),amount_list.get(i),year_list.get(i),month_list.get(i),day_list.get(i),
                        "",detail_list.get(i),true,invoice_list.get(i),"");
                classificationByReward(accountPackage,result);
            }
        }

        allReward.add(specialReward);
        allReward.add(spReward);
        allReward.add(hitReward);
        allReward.add(sixReward);
    }

    private void classificationByReward(AccountPackage accountPackage,int rewardIndex){
        switch (rewardIndex){
            case 0:
                specialReward.add(accountPackage);
                break;
            case 1:
                spReward.add(accountPackage);
                break;
            case 2:
                hitReward.add(accountPackage);
                break;
            case 3:
                sixReward.add(accountPackage);
                break;
            default:
                break;
        }
    }

    private int checkReward(String myNumber,ReceiptQRPackage receiptQRPackage){
        if(receiptQRPackage.getHitNumber().get(0).substring(5).equals(myNumber)) return 0;
        if(receiptQRPackage.getHitNumber().get(1).substring(5).equals(myNumber)) return 1;
        for (int i = 2; i < receiptQRPackage.getHitNumber().size() - 1; i++) {
            if (receiptQRPackage.getHitNumber().get(i).substring(5).equals(myNumber)) {
                return 2;
            }
        }
        if(receiptQRPackage.getHitNumber().get(receiptQRPackage.getHitNumber().size()-1).substring(5).equals(myNumber)) return 3;
        return 4;
    }

    private void setLayout(){
        LinearLayout record_list=findViewById(R.id.record_list);
        ArrayList<int[]> color_lists=new ArrayList<>();
        color_lists.add(new int[]{Color.parseColor("#FA7411"), Color.parseColor("#F38A1A"), Color.parseColor("#F0B857")});
        color_lists.add(new int[]{Color.parseColor("#DA8D08"), Color.parseColor("#F1BD1C"), Color.parseColor("#F7D22B")});
        color_lists.add(new int[]{Color.parseColor("#0966D8"), Color.parseColor("#254EDF"), Color.parseColor("#6A89F5")});
        color_lists.add(new int[]{Color.parseColor("#7B0AD2"), Color.parseColor("#A53FE7"), Color.parseColor("#CB8EF2")});

        for(int i=0;i<reward_names.length;i++){
            TextView reward=new TextView(this); //新增獎項文字
            reward.setText(reward_names[i]);
            reward.setTextColor(Color.WHITE);
            reward.setBackground(getGradientBackground(color_lists.get(i)));
            reward.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            reward.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
            reward.setGravity(Gravity.CENTER);
            record_list.addView(reward);

            addBaseline(this,record_list,R.drawable.segment);

            if(allReward.get(i).size() == 0){
                String nullText;
                if(isInvoiceUpdate) nullText="無";
                else nullText="尚未取得發票資訊";
                TextView no_hit_text=new TextView(this);
                no_hit_text.setText(nullText);
                no_hit_text.setTextColor(Color.WHITE);
                no_hit_text.setGravity(Gravity.CENTER);
                no_hit_text.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                record_list.addView(no_hit_text);
            }else{  //新增中獎項目
                for(int j=0;j<allReward.get(i).size();j++){
                    final AccountPackage accountPackage=allReward.get(i).get(j);
                    LinearLayout recordlist_element_detail = (LinearLayout) getLayoutInflater().inflate(R.layout.report_recordlist_element_detail,null);
                    final TextView detailName = (TextView) recordlist_element_detail.findViewById(R.id.detail);
                    TextView detailAmount = (TextView) recordlist_element_detail.findViewById(R.id.cost);
                    TextView detailDate = (TextView) recordlist_element_detail.findViewById(R.id.date);
                    detailName.setText(accountPackage.getDetail());
                    if(accountPackage.getType()){ //支出
                        detailAmount.setText("-$"+String.valueOf(accountPackage.getMoney()));
                        detailAmount.setTextColor(Color.RED);
                    }else{ //收入
                        detailAmount.setText("+$"+String.valueOf(accountPackage.getMoney()));
                        detailAmount.setTextColor(Color.rgb(4,117,2));
                    }
                    //取得星期幾
                    final String strDate=String.valueOf(accountPackage.getYear()+"/"+accountPackage.getMonth()+"/"+accountPackage.getDay());
                    SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
                    Date date=null;
                    try {
                        date = format.parse(strDate);
                    }catch (ParseException e){ }
                    final String week=" "+getWeekOfDate(date); //儲存星期幾
                    detailDate.setText(strDate+week);
                    addBaseline(this,record_list,R.drawable.segment);

                    recordlist_element_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(Invoice_auto_check_activity.this, Bookkeeping_activity.class);
                            intent.putExtra("CALL","modBookkeeping");
                            intent.putExtra("RECORDID",accountPackage.getID());
                            Invoice_auto_check_activity.this.startActivityForResult(intent,0);
                            overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                        }
                    });
                    record_list.addView(recordlist_element_detail);
                }
            }
        }
    }

    private void setLayoutSize(){
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float heightRate=dm.heightPixels/(float)1920;
        float widthRate=dm.widthPixels/(float)1080;

        RelativeLayout mainly=findViewById(R.id.mainly);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mainly.getLayoutParams();
        params.height= (int) (params.height*heightRate);
        params.width= (int) (params.width*widthRate);
        mainly.setLayoutParams(params);

        changeLinearViewSize((ViewGroup) findViewById(R.id.record_list),widthRate,heightRate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}