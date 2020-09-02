package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.AccountPackage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.*;

public class Invoice_auto_check_activity extends AppCompatActivity {
    private ArrayList<ArrayList<AccountPackage>> allReward=new ArrayList<>();
    private ArrayList<AccountPackage> specialReward=new ArrayList<>();
    private ArrayList<AccountPackage> spReward=new ArrayList<>();
    private ArrayList<AccountPackage> hitReward=new ArrayList<>();
    private ArrayList<AccountPackage> sixReward=new ArrayList<>();
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

        getAutoCheckResult();
        setLayout();
    }

    private void getAutoCheckResult(){ //Modify Here
        AccountPackage accountPackage=new AccountPackage(1,1,1,1,1,"123","123",true,"123","123");
        specialReward.add(accountPackage);
        hitReward.add(accountPackage);
        hitReward.add(accountPackage);

        allReward.add(specialReward);
        allReward.add(spReward);
        allReward.add(hitReward);
        allReward.add(sixReward);
    }

    private void setLayout(){
        LinearLayout record_list=findViewById(R.id.record_list);
        ArrayList<int[]> color_lists=new ArrayList<>();
        color_lists.add(new int[]{Color.parseColor("#FA7411"), Color.parseColor("#F38A1A"), Color.parseColor("#F0B857")});
        color_lists.add(new int[]{Color.parseColor("#39F70D"), Color.parseColor("#4AE630"), Color.parseColor("#83F484")});
        color_lists.add(new int[]{Color.parseColor("#002BC0"), Color.parseColor("#254EDF"), Color.parseColor("#6A89F5")});
        color_lists.add(new int[]{Color.parseColor("#7B0AD2"), Color.parseColor("#A53FE7"), Color.parseColor("#CB8EF2")});

        for(int i=0;i<reward_names.length;i++){
            TextView reward=new TextView(this); //新增獎項文字
            reward.setText(reward_names[i]);
            reward.setTextColor(Color.WHITE);
            reward.setBackground(getGradientBackground(color_lists.get(i)));
            reward.setTextSize(getDP(getResources(),15));
            reward.setHeight((int) getDP(getResources(),75));
            reward.setGravity(Gravity.CENTER);
            record_list.addView(reward);

            addBaseline(this,record_list,R.drawable.segment);

            if(allReward.get(i).size() == 0){
                TextView no_hit_text=new TextView(this);
                no_hit_text.setText("無");
                no_hit_text.setTextColor(Color.WHITE);
                no_hit_text.setGravity(Gravity.CENTER);
                no_hit_text.setTextSize(getDP(getResources(),10));
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

//                    recordlist_element_detail.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent=new Intent();
//                            intent.putExtra("CALL","modBookkeeping");
//                            intent.putExtra("RECORDID",accountPackage.getID());
//                            Invoice_auto_check_activity.this.startActivityForResult(intent,0);
//                            overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
//                        }
//                    });
                    record_list.addView(recordlist_element_detail);
                }
            }
        }
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