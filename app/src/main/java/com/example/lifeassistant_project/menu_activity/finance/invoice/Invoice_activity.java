package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.ReceiptContainer;

import java.util.ArrayList;

public class Invoice_activity extends AppCompatActivity {
    private boolean isInvoiceUpdate;
    private ReceiptContainer recepitContainerPre,recepitContainerRec;

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
        client.setReceiptQR();
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

            if(client.getRcvReceiptContainer()!=null) {
                recepitContainerRec = client.getRcvReceiptContainer().get(0); //較新的
                recepitContainerPre = client.getRcvReceiptContainer().get(1);
                isInvoiceUpdate=true;
            }
            else{
                Toast.makeText(this,"無網路連線",Toast.LENGTH_SHORT).show();
                isInvoiceUpdate=false;
            }
        }
    }

    public void setCheckReward(){
        final TextView receiptsewrial=findViewById(R.id.receiptsewrial);
        receiptsewrial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(receiptsewrial.getText().toString().length()==3){
                    if(getSupportActionBar().getSubtitle().toString().equals("   "+getMonTitle(recepitContainerPre.getMonth()))){
                        checkReward(receiptsewrial.getText().toString(),recepitContainerPre);
                    }else{
                        checkReward(receiptsewrial.getText().toString(),recepitContainerRec);
                    }
                }
            }
        });
    }

    private void checkReward(String myNumber,ReceiptContainer receiptContainer){
        TextView invoice_reword=findViewById(R.id.invoice_reword);
        LinearLayout invoice_reword_number=findViewById(R.id.invoice_reword_number);
        TextView invoice_first5=findViewById(R.id.invoice_first5);
        TextView invoice_last3=findViewById(R.id.invoice_last3);
        boolean isReward=false;

        if(receiptContainer.getHitNumber().get(0).substring(5).equals(myNumber)){
            invoice_reword.setText("注意特別獎");
            invoice_reword.setBackgroundColor(Color.parseColor("#575D57"));
            invoice_reword.setVisibility(View.VISIBLE);

            //印出中獎碼全碼
            invoice_first5.setText(Html.fromHtml("<u>"+receiptContainer.getHitNumber().get(0).substring(0,5)+"</u>"));
            invoice_last3.setText(receiptContainer.getHitNumber().get(0).substring(5));
            invoice_reword_number.setVisibility(View.VISIBLE);

            isReward=true;
        }

        if(!isReward){
            if(receiptContainer.getHitNumber().get(1).substring(5).equals(myNumber)){
                invoice_reword.setText("注意特獎");
                invoice_reword.setBackgroundColor(Color.parseColor("#575D57"));
                invoice_reword.setVisibility(View.VISIBLE);

                //印出中獎碼全碼
                invoice_first5.setText(Html.fromHtml("<u>"+receiptContainer.getHitNumber().get(1).substring(0,5)+"</u>"));
                invoice_last3.setText(receiptContainer.getHitNumber().get(1).substring(5));
                invoice_reword_number.setVisibility(View.VISIBLE);

                isReward=true;
            }
        }

        if(!isReward) {
            for (int i = 2; i < receiptContainer.getHitNumber().size() - 1; i++) {
                if (receiptContainer.getHitNumber().get(i).substring(5).equals(myNumber)) {
                    invoice_reword.setText("中獎!!");
                    invoice_reword.setBackgroundColor(Color.parseColor("#1CE40E"));
                    invoice_reword.setVisibility(View.VISIBLE);

                    //印出中獎碼全碼
                    invoice_first5.setText(Html.fromHtml("<u>"+receiptContainer.getHitNumber().get(i).substring(0,5)+"</u>"));
                    invoice_last3.setText(receiptContainer.getHitNumber().get(i).substring(5));
                    invoice_reword_number.setVisibility(View.VISIBLE);

                    isReward = true;
                    break;
                }
            }
        }

        if(!isReward){ //增開六獎
            if(receiptContainer.getHitNumber().get(receiptContainer.getHitNumber().size()-1).substring(5).equals(myNumber)){
                invoice_reword.setText("中獎!!");
                invoice_reword.setBackgroundColor(Color.parseColor("#1CE40E"));
                invoice_reword.setVisibility(View.VISIBLE);

                //印出中獎碼全碼
                invoice_first5.setText("六獎");
                invoice_last3.setText(receiptContainer.getHitNumber().get(receiptContainer.getHitNumber().size()-1).substring(5));
                invoice_reword_number.setVisibility(View.VISIBLE);

                isReward = true;
            }
        }

        if(!isReward) {
            invoice_reword.setText("未中獎");
            invoice_reword.setBackgroundColor(Color.parseColor("#FF5722"));
            invoice_reword.setVisibility(View.VISIBLE);
            invoice_reword_number.setVisibility(View.INVISIBLE);
        }
    }

    public void clickToQRScan(View view)
    {
        Intent intent=new Intent(this,Invoice_qr_activity.class);
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
            TextView receiptsewrial=findViewById(R.id.receiptsewrial);
            receiptsewrial.setText("");
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
