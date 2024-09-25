package com.example.securelife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import io.paperdb.Paper;

public class SecurityHomeActivity extends AppCompatActivity implements View.OnClickListener {
    CardView sentNotification,removeVisitor;
    ImageView imglogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_home);
        removeVisitor=findViewById(R.id.remove_visitor);
        imglogout=findViewById(R.id.logoutimg);
        sentNotification = findViewById(R.id.sent_notification);
        sentNotification.setOnClickListener(this);
        removeVisitor.setOnClickListener(this);
        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SecurityHomeActivity.this, R.style.dialoge);
                dialog.setContentView(R.layout.dilog_layout);
                Button no, yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Paper.book().destroy();

                        Intent intent = new Intent(SecurityHomeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sent_notification) {
            Intent intent = new Intent(SecurityHomeActivity.this, SecuritySentNotificationActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.remove_visitor){
            Intent intent=new Intent(SecurityHomeActivity.this,RemoveVisitorActivity.class);
            startActivity(intent);
        }
    }
}
