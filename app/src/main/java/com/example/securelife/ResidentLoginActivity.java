package com.example.securelife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ResidentLoginActivity extends AppCompatActivity {
    private Button residentRegister,residentLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_login);
        residentLogin=(Button) findViewById(R.id.resident_login);
        residentRegister=(Button)findViewById(R.id.security_guard_login);
        residentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResidentLoginActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        residentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResidentLoginActivity.this, SecurityLoginActivity.class);
                startActivity(intent);
            }
        });

    }
}