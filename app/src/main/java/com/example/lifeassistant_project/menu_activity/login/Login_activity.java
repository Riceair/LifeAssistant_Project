package com.example.lifeassistant_project.menu_activity.login;

import android.content.SharedPreferences;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;
import com.example.lifeassistant_project.activity_update.packages.LoginPackage;
import com.google.android.material.textfield.TextInputEditText;

public class Login_activity extends AppCompatActivity {
    private static final int REGISTER_CODE = 11;
    private static final int LOGIN_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  登入");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.regsite_logo_small);
        final Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoginPackage loginPackage = getClientLoginInfo();
                if(loginPackage.getName().equals("") || loginPackage.getPassword().equals(""))
                    Toast.makeText(Login_activity.this,"請輸入正確的帳號密碼",Toast.LENGTH_SHORT).show();
                else
                {
                    if(LoginHandler.Login(loginPackage))
                    {
                        saveInformation(loginPackage.getName(), loginPackage.getPassword());
                        Intent intent=new Intent();
                        intent.putExtra("ACCOUNT",loginPackage.getName());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Login_activity.this,"帳號或密碼錯誤",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_activity.this,Register_activity.class);
                Login_activity.this.startActivityForResult(intent,REGISTER_CODE);
                overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REGISTER_CODE){
            if(resultCode==RESULT_OK){
                Bundle bundle = data.getExtras();
                String account=bundle.getString("ACCOUNT");
                String password=bundle.getString("PASSWORD");
                LoginPackage regloginpackage=new LoginPackage(account,password);
                if(LoginHandler.Login(regloginpackage))
                {
                    saveInformation(regloginpackage.getName(), regloginpackage.getPassword());
                    Login_activity.this.finish();
                }
            }
        }
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