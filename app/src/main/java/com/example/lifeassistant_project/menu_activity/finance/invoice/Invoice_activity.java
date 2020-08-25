package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    }

    private void updateInvoice(){  //更新發票資訊
        ClientProgress client = new ClientProgress();
        client.setReceiptQR();
        Thread cThread = new Thread(client);
        cThread.start();
        synchronized (client)
        {
            try {
                client.wait(5000);
                isInvoiceUpdate=true;
            }catch (InterruptedException e)
            {
                isInvoiceUpdate=false;
            }
        }

        recepitContainerRec=client.getRcvReceiptContainer().get(0); //較新的
        recepitContainerPre=client.getRcvReceiptContainer().get(1);
    }

    public void clickToQRScan(View view)
    {
        Intent intent=new Intent(this,Invoice_qr_activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
    }

    public void clickToCheckReward(View view){
        TextView receiptsewrial=findViewById(R.id.receiptsewrial);

        if(receiptsewrial.getText().toString().length()!=3){
            Toast.makeText(this,"請輸入末三碼",Toast.LENGTH_SHORT).show();
            return;
        }

        if(getSupportActionBar().getSubtitle().toString().equals("   "+getMonTitle(recepitContainerPre.getMonth()))){
            checkReward(receiptsewrial.getText().toString(),recepitContainerPre);
        }else{
            checkReward(receiptsewrial.getText().toString(),recepitContainerRec);
        }
    }

    private void checkReward(String myNumber,ReceiptContainer receiptContainer){
        TextView invoice_reword=findViewById(R.id.invoice_reword);
        boolean isReward=false;

        for(int i=0;i<receiptContainer.getHitNumber().size();i++){
            if(receiptContainer.getHitNumber().get(i).equals(myNumber)){
                invoice_reword.setText("中獎!!");
                invoice_reword.setBackgroundColor(Color.GREEN);
                invoice_reword.setVisibility(View.VISIBLE);
                isReward=true;
                break;
            }
        }

        if(!isReward) {
            invoice_reword.setText("未中獎");
            invoice_reword.setBackgroundColor(Color.parseColor("#FF5722"));
            invoice_reword.setVisibility(View.VISIBLE);
        }
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
        if(item.getItemId()==R.id.prevInv){

        }
        else if(item.getItemId()==android.R.id.home){
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
