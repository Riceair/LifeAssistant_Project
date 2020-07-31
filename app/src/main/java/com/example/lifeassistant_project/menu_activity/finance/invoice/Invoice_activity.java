package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.lifeassistant_project.R;

public class Invoice_activity extends AppCompatActivity {

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

    public void onbuttonclick(View view)
    {
        Intent intent=new Intent(this,Invoice_qr_activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
