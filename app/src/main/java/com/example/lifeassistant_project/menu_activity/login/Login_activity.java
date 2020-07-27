package com.example.lifeassistant_project.menu_activity.login;

import android.media.Image;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.DatabaseBehavior;
import com.example.lifeassistant_project.activity_update.LoginPackage;
import com.example.lifeassistant_project.menu_activity.finance.Report_type_activity;
import com.google.android.material.textfield.TextInputEditText;
import org.w3c.dom.Text;

public class Login_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    String userKey = LoginCertification(loginPackage);
                    LoginPackage.setUserKey(userKey);
                    if(userKey.equals("NO"))
                    {
                        System.out.println("Certification Fail."); //need to be conduct functionality.
                    }
                    else if(userKey.equals("FA"))
                    {
                        System.out.println("Connection Fail.");
                    }
                    else
                    {
//                        System.out.println(LoginPackage.getUserKey());
                        LoginPackage.setUserName(loginPackage.getName());
                        LoginPackage.setIsLogin(true);
                        DatabaseBehavior.synchronizeServer2Client();
                        Login_activity.this.finish();
                    }
                }
            }
        });
    }

    private String LoginCertification(LoginPackage loginPackage)
    {
        ClientProgress client = new ClientProgress();
        client.setLogin(loginPackage);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client)
        {
            try {
                System.out.println("WAITTING");
                client.wait(3000);
                System.out.println("GOGOGO");
            }
            catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        return client.getRcvUserKey();
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