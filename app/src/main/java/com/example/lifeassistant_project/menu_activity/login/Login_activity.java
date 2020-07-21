package com.example.lifeassistant_project.menu_activity.login;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.LoginPackage;
import com.google.android.material.textfield.TextInputEditText;
import org.w3c.dom.Text;

public class Login_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏狀態列(綠色的那塊)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);  //全螢幕
        setContentView(R.layout.activity_login_activity);

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
                        LoginPackage.setIsLogin(true);
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
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}