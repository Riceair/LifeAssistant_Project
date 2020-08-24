package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lifeassistant_project.R;

public class Invoice_activity extends AppCompatActivity {
    private static boolean isInvoiceUpdate=false;
    private MenuItem prevInv,recInv;

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
    }

    private void bind(){
        prevInv=findViewById(R.id.prevInv);
        recInv=findViewById(R.id.recInv);
    }

    public void clickToQRScan(View view)
    {
        Intent intent=new Intent(this,Invoice_qr_activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
    }

    private void setInvoiceMonth(){
        if(!isInvoiceUpdate){
            Toast.makeText(this,"請開啟網路取得兌獎資訊",Toast.LENGTH_SHORT).show();
            prevInv.setTitle("未取得資訊");
            recInv.setTitle("未取得資訊");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.invoice_menu,menu);
        return true;
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
