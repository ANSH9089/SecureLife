package com.example.securelife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.securelife.Model.Security;

import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {
    CardView newRequest,ResidentList,AdminList,SecurityList,addNotice;
    ImageView imglogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        newRequest=findViewById(R.id.newRequest);
        ResidentList=findViewById(R.id.ResidentList);
        AdminList=findViewById(R.id.AdminList);
        SecurityList=findViewById(R.id.SecurityList);
        imglogout = findViewById(R.id.logoutimg);
        addNotice=findViewById(R.id.addNotice);



        newRequest.setOnClickListener(this);
        ResidentList.setOnClickListener(this);
        AdminList.setOnClickListener(this);
        SecurityList.setOnClickListener(this);
        addNotice.setOnClickListener(this);
        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(AdminHomeActivity.this, R.style.dialoge);
                dialog.setContentView(R.layout.dilog_layout);
                Button no, yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Paper.book().destroy();

                        Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
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
        if (view.getId() == R.id.newRequest) {
            Intent intent = new Intent(AdminHomeActivity.this, AdminAcceptNewResidentActivity.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.ResidentList){
            Intent i=new Intent(AdminHomeActivity.this,ResidentListActivity.class);
            startActivity(i);
        }
        else if(view.getId()==R.id.AdminList){
            Intent i=new Intent(AdminHomeActivity.this, AdminListActivity.class);
            startActivity(i);
        }
        else if(view.getId()==R.id.SecurityList){
            Intent i=new Intent(AdminHomeActivity.this, SecurityGuardListActivity.class);
            startActivity(i);
        }
        else if(view.getId()==R.id.addNotice){
            Intent i=new Intent(AdminHomeActivity.this, addNotice.class);
            startActivity(i);
        }

    }
}