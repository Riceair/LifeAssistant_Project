package com.example.lifeassistant_project.menu_activity.login;

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
        toolbar.setTitle("   註冊");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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


        if(isRegistered) {
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