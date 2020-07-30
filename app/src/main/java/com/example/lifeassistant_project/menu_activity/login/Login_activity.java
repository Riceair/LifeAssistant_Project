package com.example.lifeassistant_project.menu_activity.login;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.LoginHandler;
import com.example.lifeassistant_project.activity_update.LoginPackage;
import com.google.android.material.textfield.TextInputEditText;

public class Login_activity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   登入");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final ImageView loginButton = (ImageView) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoginPackage loginPackage = getClientLoginInfo();
                if(loginPackage.getName().equals("") || loginPackage.getPassword().equals(""))
                    System.out.println("Error! Client need to give input message.");
                else
                {
                    if(LoginHandler.Login(loginPackage))
                    {
                        saveInformation(loginPackage.getName(), loginPackage.getPassword());
                        Login_activity.this.finish();
                    }
                }
            }
        });
    }

    private LoginPackage getClientLoginInfo()
    {
        LoginPackage loginPackage = new LoginPackage();
        TextInputEditText targetText = (TextInputEditText) findViewById(R.id.account_id);
        loginPackage.setName(targetText.getText().toString());
        targetText = (TextInputEditText) findViewById(R.id.account_password);
        loginPackage.setPassword(targetText.getText().toString());
        return loginPackage;
    }

    //used for store user's account content.
    public void saveInformation(String username,String password) {
        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Login_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}