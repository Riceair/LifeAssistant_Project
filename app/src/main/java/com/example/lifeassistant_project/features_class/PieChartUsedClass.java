package com.example.lifeassistant_project.features_class;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieChartUsedClass {
    private PieChart mChart;
    private List<Integer> color_default_list = new ArrayList<>();

    public PieChartUsedClass(PieChart mChart,List<String> type_list,List<Integer> amount_list){
        this.mChart=mChart;
        setDefulatColor(); //設置顏色
        setMChartDefault();
        setMChartLegend(20,15); //設置圖例
        setMChartData(type_list,amount_list);
    }

    private void setMChartDefault(){
        mChart.setUsePercentValues(true);  //使用百分比顯示
        mChart.getDescription().setEnabled(false);    //設置pipeChart圖表的描述
        //mChart.setBackgroundColor(Color.rgb(108,110,169)); //設置背景顏色
        mChart.setExtraOffsets(5, 5, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f); //設置pieChart圖表轉動阻力摩擦係數[0,1]
        mChart.setRotationAngle(0);                   //設置pieChart圖表起始角度
        mChart.setRotationEnabled(false);              //設置pieChart圖表是否可以手動旋轉
        mChart.setHighlightPerTapEnabled(false);       //設置piecahrt圖表點擊Item高亮是否可用
        //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);// 設置pieChart圖表展示動畫效果

// 設置 pieChart 圖表Item文字屬性
        mChart.setDrawEntryLabels(false);              //設置pieChart是否只顯示圓形圖上百分比不顯示文字（true：下面屬性才有效果）
        mChart.setEntryLabelColor(Color.BLACK);       //設置pieChart圖表文本字體顏色
        //mChart.setEntryLabelTypeface(mTfRegular);     //設置pieChart圖表文本字體樣式
        mChart.setEntryLabelTextSize(15f);            //設置pieChart圖表文本字體大小
        mChart.setDrawHoleEnabled(false);
    }

    public void setMChartData(List<String> type_list,List<Integer> amount_list){
        int length = type_list.size();
        ArrayList<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if(i<color_default_list.size())
                colors.add(color_default_list.get(i));
            else
                colors.add(randomColor());
        }

        // #FF8000 	#FF9224 #EA7500 #FFAF60 	#FF8040 #FF8F59 	#FF9D6F
        //圓形圖實體 PieEntry
        for (int i=0;i<length;i++){
            int value = amount_list.get(i);
            PieEntry pieEntry = new PieEntry(value,type_list.get(i));
            pieEntryList.add(pieEntry);
        }
        //餅狀圖資料集 PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setSliceSpace(3f);           //設置餅狀Item之間的間隙
        pieDataSet.setSelectionShift(10f);      //設置餅狀Item被選中時變化的距離
        pieDataSet.setColors(colors);           //為DataSet中的資料匹配上顏色集(圓形圖Item顏色)
        //最終資料 PieData
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);            //設置是否顯示資料實體(百分比，true:以下屬性才有意義)
        pieData.setValueTextColor(Color.BLACK);  //設置所有DataSet內資料實體（百分比）的文本顏色
        pieData.setValueTextSize(20f);          //設置所有DataSet內資料實體（百分比）的文本字體大小
        //pieData.setValueTypeface(mTfLight);     //設置所有DataSet內資料實體（百分比）的文本字體樣式
        pieData.setValueFormatter(new PercentFormatter());//設置所有DataSet內資料實體（百分比）的文本字體格式
        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();                    //將圖表重繪以顯示設定的屬性和資料
    }

    public void setMChartLegend(int formSize,int textSize){
        // 獲取pieChart圖例
        Legend l = mChart.getLegend();
        l.setEnabled(true);                    //是否啟用圖列（true：下面屬性才有意義）
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setForm(Legend.LegendForm.SQUARE); //設置圖例的形狀
        l.setFormSize(formSize);               //設置圖例的大小
        l.setFormToTextSpace(10f);         //設置每個圖例實體中標籤和形狀之間的間距
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);            //設置圖列換行(注意使用影響性能,僅適用legend位於圖表下面)
        l.setXEntrySpace(10f);            //設置圖例實體之間延X軸的間距（setOrientation = HORIZONTAL有效）
        l.setYEntrySpace(8f);             //設置圖例實體之間延Y軸的間距（setOrientation = VERTICAL 有效）
        l.setYOffset(0f);                //設置比例塊Y軸偏移量
        l.setTextSize(textSize);                  //設置圖例標籤文本的大小
        l.setTextColor(Color.BLACK);//設置圖例標籤文本的顏色
    }

    private void setDefulatColor() {
        color_default_list.add(Color.rgb(252,104,3));
        color_default_list.add(Color.rgb(139,101,8));
        color_default_list.add(Color.rgb(67,110,238));
        color_default_list.add(Color.rgb(34,139,34));
        color_default_list.add(Color.rgb(160,32,240));
        color_default_list.add(Color.rgb(65,105,225));
        color_default_list.add(Color.rgb(255,127,36));
    }

    //產生隨機顏色
    private static int randomColor(){
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.rgb(r,g,b);
    }

    public void setMChartSize(int left,int top,int right,int bottom){
        mChart.setExtraOffsets(left,top,right,bottom);
    }

    public void setLegendUsed(boolean isUse){
        Legend l = mChart.getLegend();
        l.setEnabled(isUse);
    }

    public void modifyDefaultColor(List<Integer> color_default_list){
        this.color_default_list=color_default_list;
    }
}
