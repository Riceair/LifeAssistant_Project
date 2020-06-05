package com.example.lifeassistant_project.menu_option_to_link;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.lifeassistant_project.menu_activity.Report_activity;
import com.example.lifeassistant_project.menu_interface.Options_choices_interface;

public class FinMan_Report extends Options_choices_interface {
    public FinMan_Report(LinearLayout linearLayout, View view, String choice_name, String backGround){
        super(linearLayout,view,choice_name,backGround);
    }

    @Override
    public void toLink() {
        Intent intent = new Intent(view.getContext(), Report_activity.class);
        view.getContext().startActivity(intent);
    }
}
