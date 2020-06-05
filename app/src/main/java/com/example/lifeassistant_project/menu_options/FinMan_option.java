package com.example.lifeassistant_project.menu_options;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.menu_interface.Menu_options_interface;
import com.example.lifeassistant_project.menu_option_to_link.FinMan_Bookkeeping;
import com.example.lifeassistant_project.menu_option_to_link.FinMan_Invoice;
import com.example.lifeassistant_project.menu_option_to_link.FinMan_Report;

//記帳 報表 對發票
public class FinMan_option extends Menu_options_interface {
    private TextView newText;
    private boolean isChecked = false;
    private FinMan_Bookkeeping bookkeeping;
    private FinMan_Report report;
    private FinMan_Invoice invoice;

    public FinMan_option(View view, LinearLayout parent_layout){
        super(view,parent_layout,"理財");
        bmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.trailers);
        bmp= Bitmap.createScaledBitmap(bmp,400,400,false);
        createView(parent_layout,"#e0af1f");

        LinearLayout vertlinear = (LinearLayout) body_option_layout.findViewById(R.id.vertical_linear);
        bookkeeping= new FinMan_Bookkeeping(vertlinear,view,"記帳","#ff4500");
        report= new FinMan_Report(vertlinear,view,"報表","#ff7f50");
        invoice= new FinMan_Invoice(vertlinear,view,"兌發票","#ffa07a");
    }
    @Override
    public void createSubOption() {
        if(!isChecked) {
            bookkeeping.textShow(!isChecked);
            report.textShow(!isChecked);
            invoice.textShow(!isChecked);
        }else{
            bookkeeping.textShow(!isChecked);
            report.textShow(!isChecked);
            invoice.textShow(!isChecked);
        }
        isChecked= !isChecked;
    }
}
