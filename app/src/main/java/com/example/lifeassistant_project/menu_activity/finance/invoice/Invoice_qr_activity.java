package com.example.lifeassistant_project.menu_activity.finance.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class Invoice_qr_activity extends AppCompatActivity {
    private SurfaceView qr_view;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private TextView invoice_reword;
    private TextView invoice_text;

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

        bind();
        qrCodeSet();
    }

    private void bind(){
        qr_view=findViewById(R.id.qr_view);
        barcodeDetector=new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource=new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(300,300).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();
        invoice_reword=findViewById(R.id.invoice_reword);
        invoice_text=findViewById(R.id.invoice_text);
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
                if(qrCodes.size()!=0){
                    invoice_text.post(new Runnable() {
                        @Override
                        public void run() {
                            String invoice_info=qrCodes.valueAt(0).displayValue;
                            if(!isInvoice(invoice_info)){
                                Toast.makeText(Invoice_qr_activity.this,"請掃描左側的QR code",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String invoice_id=invoice_info.substring(0,2)+"-"+invoice_info.substring(2,10); //發票號碼
                                String invoice_id_last3=invoice_info.substring(7,10); //發票末三碼
                                String invoice_year=invoice_info.substring(10,13); //發票年份
                                String invoice_month=invoice_info.substring(13,15); //發票月份
                                invoice_text.setText(invoice_id);
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isInvoice(String info){
        String regFirst2="[A-Z]{2}";
        if(info.length()>15 && info.substring(0,2).matches(regFirst2)
                && Integer.valueOf(info.substring(13,15))<=12 && Integer.valueOf(info.substring(13,15))>=1)
            return true;
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Invoice_qr_activity.this.finish();
        }
        return true;
    }
}