package com.example.lifeassistant_project.menu_activity.login;

import android.content.SharedPreferences;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.LoginPackage;
import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;
import com.google.android.material.textfield.TextInputEditText;

public class Register_activity extends AppCompatActivity {
    private TextView account_input,pw_input,pw_check_input,account_hint,pw_hint,pw_check_hint;
    private boolean accIsLegal=false;
    private boolean pwIsLegal=false;
    private boolean pwChIsLegal=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  註冊");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.regsite_logo_small);

        bind();
        setChangeListener();

    }

    public void clickToRegister(View view){
        if(!(accIsLegal&&pwIsLegal&&pwChIsLegal)){
            Toast.makeText(this,"請填入正確的資料",Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isRegistered=false;
        //若註冊成功把 isRegistered改成true

        LoginPackage loginPackage = this.getClientLoginInfo();
        isRegistered = LoginHandler.Register(loginPackage);


        if(isRegistered) {
            saveInformation(loginPackage.getName(), loginPackage.getPassword());
            Intent intent=new Intent();
            intent.putExtra("ACCOUNT",account_input.getText().toString());
            intent.putExtra("PASSWORD",pw_input.getText().toString());
            setResult(RESULT_OK,intent);
            finish();
        }
        else{
            account_hint.setText("帳號已存在");
            account_hint.setTextColor(Color.RED);
            Toast.makeText(this,"帳號已存在",Toast.LENGTH_SHORT).show();
        }
    }

    private void bind(){
        account_input=findViewById(R.id.account_input);
        pw_input=findViewById(R.id.pw_input);
        pw_check_input=findViewById(R.id.pw_check_input);
        account_hint=findViewById(R.id.account_hint);
        pw_hint=findViewById(R.id.pw_hint);
        pw_check_hint=findViewById(R.id.pw_check_hint);
    }

    private void setChangeListener(){
        account_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(account_input.getText().toString().length()<6 || !isEngWithNumber(account_input.getText().toString())) {
                    account_hint.setText("請至少輸入6個英數字");
                    account_hint.setTextColor(Color.RED);
                    accIsLegal=false;
                }
                else {
                    account_hint.setText("");
                    accIsLegal=true;
                }
            }
        });

        pw_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(pw_input.getText().toString().length()<6 || !isEngWithNumber(pw_input.getText().toString())){
                    pw_hint.setText("請至少輸入6個英數字");
                    pw_hint.setTextColor(Color.RED);
                    pwIsLegal=false;
                }
                else{
                    pw_hint.setText("");
                    pwIsLegal=true;
                }

                if(pw_input.getText().toString().equals(pw_check_input.getText().toString())){
                    pw_check_hint.setVisibility(View.INVISIBLE);
                    pwChIsLegal=true;
                }
            }
        });

        pw_check_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!pw_input.getText().toString().equals(pw_check_input.getText().toString())){
                    pw_check_hint.setVisibility(View.VISIBLE);
                    pwChIsLegal=false;
                }
                else{
                    pw_check_hint.setVisibility(View.INVISIBLE);
                    pwChIsLegal=true;
                }
            }
        });
    }

    private boolean isEngWithNumber(String text){
        String regEng=".*[a-zA-Z].*";
        String regNum=".*[0-9].*";
        boolean hasEng=text.matches(regEng);
        boolean hasNum=text.matches(regNum);
        return hasEng&&hasNum;
    }

    private LoginPackage getClientLoginInfo()
    {
        LoginPackage loginPackage = new LoginPackage();
        EditText targetText = (EditText) findViewById(R.id.account_input);
        loginPackage.setName(targetText.getText().toString());
        targetText = (EditText) findViewById(R.id.pw_input);
        loginPackage.setPassword(targetText.getText().toString());
        return loginPackage;
    }

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
            Register_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}