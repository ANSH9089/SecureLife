package com.example.securelife;

import static com.example.securelife.Prevalent.Prevalent.blockNo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securelife.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.subtyping.qual.Bottom;

import io.paperdb.Paper;


public class ResidentHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView buttonDrawerToggle;
    NavigationView navigationView;
    private String imageUri,name;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_home);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        navController= Navigation.findNavController(this,R.id.fragment_layout);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigationView);
        buttonDrawerToggle=findViewById(R.id.buttonDrawerToggle);

        buttonDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });
        View headView=navigationView.getHeaderView(0);
        ImageView useImage=headView.findViewById(R.id.profileimgg);
        TextView textUsername=headView.findViewById(R.id.textname1);
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();



        final DatabaseReference reference;

        reference=FirebaseDatabase.getInstance().getReference("Resident");
        reference.child(Prevalent.currentOnlineUser.getBlockNo()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot=task.getResult();
                        imageUri=String.valueOf(dataSnapshot.child("imageUrl").getValue());
                        name=String.valueOf(dataSnapshot.child("name").getValue());
                        Picasso.get().load(imageUri).into(useImage);
                        textUsername.setText(name);
                    }

                }
                else{
                    Toast.makeText(ResidentHomeActivity.this, "Failed To Show", Toast.LENGTH_SHORT).show();
                }
            }
        });







        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId= item.getItemId();
                if(itemId==R.id.navChat){
                    Intent intent = new Intent(ResidentHomeActivity.this,ResidentChatActivity.class);
                    startActivity(intent);
                }
                else if(itemId==R.id.navLogOut){
                    showLogoutDialog();
                }
                return false;
            }
        });

    }
    private void showLogoutDialog() {
        Dialog dialog = new Dialog(ResidentHomeActivity.this, R.style.dialoge);
        dialog.setContentView(R.layout.dilog_layout);
        Button no, yes;
        yes = dialog.findViewById(R.id.yesbnt);
        no = dialog.findViewById(R.id.nobnt);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(ResidentHomeActivity.this, MainActivity.class);
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
}