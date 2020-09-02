package com.example.lifeassistant_project.menu_activity.finance.invoice;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;

import com.example.lifeassistant_project.activity_update.packages.ReceiptQRPackage;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;

public class Invoice_qr_activity extends AppCompatActivity {
    private SurfaceView qr_view;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private TextView invoice_reword;
    private LinearLayout invoice_reword_number;
    private TextView invoice_first5;
    private TextView invoice_last3;
    private TextView invoice_text;
    private TextView reword_amount;
    private boolean isInvoiceUpdate;
    private ReceiptQRPackage recepitContainerPre,recepitContainerRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_qr_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   兌發票");
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
        bind();
        qrCodeSet();
    }

    private void bind(){
        qr_view=findViewById(R.id.qr_view);
        barcodeDetector=new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource=new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(300,300).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();
        invoice_reword=findViewById(R.id.invoice_rewards);
        invoice_text=findViewById(R.id.invoice_text);
        invoice_reword_number=findViewById(R.id.invoice_rewords_number);
        invoice_first5=findViewById(R.id.invoice_first5);
        invoice_last3=findViewById(R.id.invoice_last3);
        reword_amount=findViewById(R.id.reword_amount);
    }

    private void qrCodeSet(){
        qr_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                //取得照相機權限
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Invoice_qr_activity.this,new String[]{Manifest.permission.CAMERA},1);
                    return;
                }
                try{
                    cameraSource.start(surfaceHolder);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>(){
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode>qrCodes=detections.getDetectedItems();
                if(qrCodes.size()!=0 && isInvoiceUpdate){
                    invoice_text.post(new Runnable() {
                        @Override
                        public void run() {
                            String invoice_info=qrCodes.valueAt(0).displayValue;
                            if(!isInvoice(invoice_info)){
                                Toast.makeText(Invoice_qr_activity.this,"請掃描左側的QR code",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String invoice_id=invoice_info.substring(0,2)+"-"+invoice_info.substring(2,10); //發票號碼
                                invoice_text.setText(invoice_id);
                                String invoice_number=invoice_info.substring(2,10); //發票碼
                                String invoice_year=invoice_info.substring(10,13); //發票年份
                                String invoice_month=invoice_info.substring(13,15); //發票月份
                                if(isInvoiceUpdate) {
                                    checkInvoice(invoice_number, Integer.valueOf(invoice_year) + 1911, Integer.valueOf(invoice_month));
                                }
                            }
                        }
                    });
                }
                else Toast.makeText(Invoice_qr_activity.this,"未取得發票資料",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInvoice(String info){
        String regFirst2="[A-Z]{2}";
        String regNum15="[0-9]{15}";
        if(info.length()>=78 && info.substring(0,2).matches(regFirst2) && info.substring(2,17).matches(regNum15)) { //確認前兩碼是否字母 發票號碼與日期是否確
            if (Integer.valueOf(info.substring(13, 15)) <= 12 && Integer.valueOf(info.substring(13, 15)) >= 1) //確認月份是否合法
                return true;
            else
                return false;
        }
        else
            return false;
    }

    private void checkInvoice(String number,int year,int month){
        if((year==Integer.valueOf(recepitContainerPre.getYear()) && month<Integer.valueOf(recepitContainerPre.getMonth())) ||
                year<Integer.valueOf(recepitContainerPre.getYear())){
            invoice_reword.setText("已過期");
            invoice_reword.setBackgroundResource(R.drawable.rewards_abandoned);
            invoice_reword.setVisibility(View.VISIBLE);
            invoice_reword_number.setVisibility(View.INVISIBLE);
            reword_amount.setVisibility(View.INVISIBLE);
            return;
        }
        if((year==Integer.valueOf(recepitContainerRec.getYear()) && month>Integer.valueOf(recepitContainerRec.getMonth())+1) ||
                year>Integer.valueOf(recepitContainerRec.getYear())){
            invoice_reword.setText("尚未開獎");
            invoice_reword.setBackgroundResource(R.drawable.rewards_abandoned);
            invoice_reword.setVisibility(View.VISIBLE);
            invoice_reword_number.setVisibility(View.INVISIBLE);
            reword_amount.setVisibility(View.INVISIBLE);
            return;
        }

        if(year==Integer.valueOf(recepitContainerPre.getYear()) &&
                (month==Integer.valueOf(recepitContainerPre.getMonth()) || month==Integer.valueOf(recepitContainerPre.getMonth())+1)){
            checkReword(number,recepitContainerPre);
        }else if(year==Integer.valueOf(recepitContainerRec.getYear()) &&
                (month==Integer.valueOf(recepitContainerRec.getMonth()) || month==Integer.valueOf(recepitContainerRec.getMonth())+1)){
            checkReword(number,recepitContainerRec);
        }
    }

    private void checkReword(String number, ReceiptQRPackage receiptQRPackage){
        if(receiptQRPackage.getHitNumber().get(0).equals(number)){
            invoice_reword.setText("特別獎!!");
            invoice_reword.setBackgroundResource(R.drawable.rewards_bonus);
            invoice_reword.setVisibility(View.VISIBLE);

            //印出中獎碼全碼
            invoice_first5.setText("");
            invoice_last3.setText(receiptQRPackage.getHitNumber().get(0));
            invoice_reword_number.setVisibility(View.VISIBLE);

            //印出中獎金額
            reword_amount.setText("一千萬元");
            reword_amount.setVisibility(View.VISIBLE);
            return;
        }

        if(receiptQRPackage.getHitNumber().get(1).equals(number)){
            invoice_reword.setText("特獎!!");
            invoice_reword.setBackgroundResource(R.drawable.rewards_bonus);
            invoice_reword.setVisibility(View.VISIBLE);

            //印出中獎碼全碼
            invoice_first5.setText("");
            invoice_last3.setText(receiptQRPackage.getHitNumber().get(1));
            invoice_reword_number.setVisibility(View.VISIBLE);

            //印出中獎金額
            reword_amount.setText("二百萬元");
            reword_amount.setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 2; i < receiptQRPackage.getHitNumber().size() - 1; i++) {
            if (receiptQRPackage.getHitNumber().get(i).substring(5).equals(number.substring(5))) {
                checkJackPot(number, receiptQRPackage.getHitNumber().get(i));
                return;
            }
        }

        if(receiptQRPackage.getHitNumber().get(receiptQRPackage.getHitNumber().size()-1).substring(5).equals(number.substring(5))){
            invoice_reword.setText("六獎!!");
            invoice_reword.setBackgroundResource(R.drawable.rewards_general);
            invoice_reword.setVisibility(View.VISIBLE);

            //印出中獎碼全碼
            invoice_first5.setText("");
            invoice_last3.setText(receiptQRPackage.getHitNumber().get(receiptQRPackage.getHitNumber().size()-1).substring(5));
            invoice_reword_number.setVisibility(View.VISIBLE);

            //印出中獎金額
            reword_amount.setText("200元");
            reword_amount.setVisibility(View.VISIBLE);
            return;
        }

        invoice_reword.setText("未中獎");
        invoice_reword.setVisibility(View.VISIBLE);
        invoice_reword.setBackgroundResource(R.drawable.rewards_abandoned);
        invoice_reword_number.setVisibility(View.INVISIBLE);
        reword_amount.setVisibility(View.INVISIBLE);
    }

    //檢查頭獎~五獎
    private void checkJackPot(String number,String hitNumber){
        String award=""; //獎項
        String money=""; //金額
        int hitAward=5;

        for(int i=0;i<number.length();i++){
            if(hitNumber.substring(i).equals(number.substring(i))){
                hitAward=i;
                break;
            }
        }

        switch (hitAward){
            case 0:
                award="頭獎";
                money="二十萬元";
                break;
            case 1:
                award="二獎";
                money="四萬元";
                break;
            case 2:
                award="三獎";
                money="一萬元";
                break;
            case 3:
                award="四獎";
                money="4000元";
                break;
            case 4:
                award="五獎";
                money="1000元";
                break;
            default:
                award="六獎";
                money="200元";
                break;
        }

        //印出獎項
        invoice_reword.setText(award);
        invoice_reword.setBackgroundResource(R.drawable.rewards_general);
        invoice_reword.setVisibility(View.VISIBLE);

        //印出中獎碼全碼
        invoice_first5.setText(Html.fromHtml("<u>"+hitNumber.substring(0,hitAward)+"</u>"));
        invoice_last3.setText(hitNumber.substring(hitAward));
        invoice_reword_number.setVisibility(View.VISIBLE);

        //印出中獎金額
        reword_amount.setText(money);
        reword_amount.setVisibility(View.VISIBLE);
        reword_amount.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            else{
                Intent intent=getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Invoice_qr_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}