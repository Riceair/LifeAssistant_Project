package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.ReceiptQRPackage;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Invoice_activity extends AppCompatActivity {
    private boolean isInvoiceUpdate;
    private ReceiptQRPackage recepitContainerPre,recepitContainerRec;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   兌發票");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.receipt_checksum);
        updateInvoice(); //更新發票資訊
        invalidateOptionsMenu(); //依照資料獲取情況修改menu
        setCheckReward();
    }

    private void updateInvoice(){  //更新發票資訊
        ClientProgress client = new ClientProgress();
        client.setPackage(new ReceiptQRPackage());
        Thread cThread = new Thread(client);
        cThread.start();
        synchronized (client)
        {
            try {
                client.wait();
            }catch (InterruptedException e)
            {
                System.out.println(e);
            }

            ArrayList<DataPackage> rcvReceiptContainer = client.getRcvPackageList();
            if(rcvReceiptContainer.size()!=0) {
                recepitContainerRec = (ReceiptQRPackage) rcvReceiptContainer.get(0); //較近期的發票
                recepitContainerPre = (ReceiptQRPackage) rcvReceiptContainer.get(1); //上一期的發票
                isInvoiceUpdate=true;
            }
            else{
                Toast.makeText(this,"無網路連線",Toast.LENGTH_SHORT).show();
                isInvoiceUpdate=false;
            }
        }
    }

    public void setCheckReward(){
        final TextView receiptserial=findViewById(R.id.receiptserial);
        receiptserial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(receiptserial.getText().toString().length()==3){
                    if(isInvoiceUpdate) {
                        if (getSupportActionBar().getSubtitle().toString().equals("   " + getMonTitle(recepitContainerPre.getMonth()))) {
                            checkReward(receiptserial.getText().toString(), recepitContainerPre);
                        } else {
                            checkReward(receiptserial.getText().toString(), recepitContainerRec);
                        }
                    }else{
                        Toast.makeText(Invoice_activity.this,"未取得發票資訊",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    findViewById(R.id.invoice_rewards).setVisibility(View.INVISIBLE);
                    findViewById(R.id.invoice_rewords_number).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void checkReward(String myNumber, ReceiptQRPackage receiptQRPackage){
        TextView invoice_rewards=findViewById(R.id.invoice_rewards);
        LinearLayout invoice_rewards_number=findViewById(R.id.invoice_rewords_number);
        TextView invoice_first5=findViewById(R.id.invoice_first5);
        TextView invoice_last3=findViewById(R.id.invoice_last3);
        boolean isReward=false;

        if(receiptQRPackage.getHitNumber().get(0).substring(5).equals(myNumber)){
            invoice_rewards.setText("注意特別獎");
            invoice_rewards.setBackgroundResource(R.drawable.rewards_bonus);
            invoice_rewards.setVisibility(View.VISIBLE);

            //印出中獎碼全碼
            invoice_first5.setText(Html.fromHtml("<u>"+ receiptQRPackage.getHitNumber().get(0).substring(0,5)+"</u>"));
            invoice_last3.setText(receiptQRPackage.getHitNumber().get(0).substring(5));
            invoice_rewards_number.setVisibility(View.VISIBLE);

            isReward=true;
        }

        if(!isReward){
            if(receiptQRPackage.getHitNumber().get(1).substring(5).equals(myNumber)){
                invoice_rewards.setText("注意特獎");
                invoice_rewards.setBackgroundResource(R.drawable.rewards_bonus);
                invoice_rewards.setVisibility(View.VISIBLE);

                //印出中獎碼全碼
                invoice_first5.setText(Html.fromHtml("<u>"+ receiptQRPackage.getHitNumber().get(1).substring(0,5)+"</u>"));
                invoice_last3.setText(receiptQRPackage.getHitNumber().get(1).substring(5));
                invoice_rewards_number.setVisibility(View.VISIBLE);

                isReward=true;
            }
        }

        if(!isReward) {
            for (int i = 2; i < receiptQRPackage.getHitNumber().size() - 1; i++) {
                if (receiptQRPackage.getHitNumber().get(i).substring(5).equals(myNumber)) {
                    invoice_rewards.setText("中獎!!");
                    invoice_rewards.setBackgroundResource(R.drawable.rewards_general);
                    invoice_rewards.setVisibility(View.VISIBLE);

                    //印出中獎碼全碼
                    invoice_first5.setText(Html.fromHtml("<u>"+ receiptQRPackage.getHitNumber().get(i).substring(0,5)+"</u>"));
                    invoice_last3.setText(receiptQRPackage.getHitNumber().get(i).substring(5));
                    invoice_rewards_number.setVisibility(View.VISIBLE);

                    isReward = true;
                    break;
                }
            }
        }

        if(!isReward){ //增開六獎
            if(receiptQRPackage.getHitNumber().get(receiptQRPackage.getHitNumber().size()-1).substring(5).equals(myNumber)){
                invoice_rewards.setText("中獎!!");
                invoice_rewards.setBackgroundResource(R.drawable.rewards_general);
                invoice_rewards.setVisibility(View.VISIBLE);

                //印出中獎碼全碼
                invoice_first5.setText("六獎");
                invoice_last3.setText(receiptQRPackage.getHitNumber().get(receiptQRPackage.getHitNumber().size()-1).substring(5));
                invoice_rewards_number.setVisibility(View.VISIBLE);

                isReward = true;
            }
        }

        if(!isReward) {
            invoice_rewards.setText("未中獎");
            invoice_rewards.setBackgroundResource(R.drawable.rewards_exile);
            invoice_rewards.setVisibility(View.VISIBLE);
            invoice_rewards_number.setVisibility(View.INVISIBLE);
        }
    }

    public void clickToQRScan(View view)
    {
        Intent intent=new Intent(this,Invoice_qr_activity.class);
        intent.putExtra("isInvoiceUpdate",isInvoiceUpdate);
        intent.putExtra("recepitContainerPre", new Gson().toJson(recepitContainerPre));
        intent.putExtra("recepitContainerRec", new Gson().toJson(recepitContainerRec));
        startActivity(intent);
        overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
    }

    public void clickToAutoCheck(View view){
        Intent intent=new Intent(this,Invoice_auto_check_activity.class);
        intent.putExtra("isInvoiceUpdate",isInvoiceUpdate);
        intent.putExtra("recepitContainerPre", new Gson().toJson(recepitContainerPre));
        intent.putExtra("recepitContainerRec", new Gson().toJson(recepitContainerRec));
        startActivity(intent);
        overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.invoice_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isInvoiceUpdate) {
            menu.findItem(R.id.prevInv).setTitle("未取得發票資料");
            menu.findItem(R.id.recInv).setTitle("未取得發票資料");
        } else {
            menu.findItem(R.id.prevInv).setTitle(getMonTitle(recepitContainerPre.getMonth()));
            menu.findItem(R.id.recInv).setTitle(getMonTitle(recepitContainerRec.getMonth()));
            getSupportActionBar().setSubtitle("   "+getMonTitle(recepitContainerRec.getMonth()));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private String getMonTitle(String month){
        try {
            if (Integer.valueOf(month) == 9)
                return "09~10月";
            else if (Integer.valueOf(month) == 11)
                return "11~12月";
            else {
                return month + "~0" + String.valueOf(Integer.valueOf(month) + 1) + "月";
            }
        }catch (Exception e){
            return "網路取得錯誤";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(isInvoiceUpdate) {
            if (item.getItemId() == R.id.prevInv) {
                getSupportActionBar().setSubtitle("   " + getMonTitle(recepitContainerPre.getMonth()));
            } else if (item.getItemId() == R.id.recInv) {
                getSupportActionBar().setSubtitle("   " + getMonTitle(recepitContainerRec.getMonth()));
            }
            TextView receiptserial=findViewById(R.id.receiptserial);
            receiptserial.setText("");
        }
        if(item.getItemId()==android.R.id.home){
            Invoice_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}
