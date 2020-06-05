package com.example.lifeassistant_project.menu_interface;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class Options_choices_interface {
    protected String choice_name;
    protected LinearLayout linearLayout;
    protected View view;
    protected TextView the_text;

    public Options_choices_interface(LinearLayout linearLayout, View view, String choice_name, String backGround){
        this.linearLayout=linearLayout;
        this.choice_name=choice_name;
        this.view=view;
        the_text= new TextView(view.getContext());
        the_text.setBackgroundColor(Color.parseColor(backGround));
        the_text.setGravity(Gravity.CENTER);
        the_text.setText(choice_name);
        the_text.setTextSize(40);
        the_text.setVisibility(View.INVISIBLE);
        the_text.setOnClickListener(new ClickToShow());
    }

    public void textShow(boolean toShow){
        if(toShow) {
            the_text.setVisibility(View.VISIBLE);
            linearLayout.addView(the_text);
        }
        else{
            the_text.setVisibility(View.INVISIBLE);
            linearLayout.removeView(the_text);
        }
    }

    private class ClickToShow implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            toLink();
        }
    }

    public abstract void toLink();
}
