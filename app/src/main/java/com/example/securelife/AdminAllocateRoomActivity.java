package com.example.securelife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securelife.Interface.ItemClickListner;
import com.example.securelife.Model.Users;
import com.example.securelife.Prevalent.Prevalent;
import com.example.securelife.R;
import com.example.securelife.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminAllocateRoomActivity extends AppCompatActivity {
    private Button securityGuardButton,residentButton,adminButton,RemoveBtn;
    private ProgressDialog loadingBar;

    private DatabaseReference unverifiedProductRef,securityRef,residentRef,adminRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_allocate_room);

        securityGuardButton = findViewById(R.id.security_guard);
        residentButton = findViewById(R.id.resident);
        adminButton = findViewById(R.id.admin);
        RemoveBtn=findViewById(R.id.remove);
        unverifiedProductRef = FirebaseDatabase.getInstance().getReference().child("WaitingUsers");
        loadingBar = new ProgressDialog(this);
        Intent intent = getIntent();
        String productId = intent.getStringExtra("productId");
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserToAdmin(productId);
            }
        });
        securityGuardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserToSecurity(productId);
            }
        });
        residentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AdminAllocateRoomActivity.this, AdminAsignGiveBlockNoActivity.class);
                i.putExtra("productId", productId);
                startActivity(i);
            }
        });
        RemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delteUser(productId);

            }
        });

    }

    private void delteUser(String productId) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("WaitingUsers").child(productId).removeValue();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(productId, null, "Your Request is Declined" , null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(AdminAllocateRoomActivity.this, AdminAcceptNewResidentActivity.class);
        startActivity(intent);

    }

    // add guard end
    private void SaveUserToSecurity(final String phone) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("WaitingUsers").child(phone).exists()) {
                    Users usersData = dataSnapshot.child("WaitingUsers").child(phone).getValue(Users.class);
                    String address=usersData.getAddress();
                    String description=usersData.getDescription();
                    String name=usersData.getName();
                    String password=usersData.getPassword();
                    String phone=usersData.getPhone();
                    String status=usersData.getStatus();
                    String imageUrl=usersData.getImageUrl();
                    loadingBar.setTitle("Creating him Security Guard");
                    loadingBar.setMessage("Please wait, while we are checking the credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    addGuard(name, address,description,phone, password,status,imageUrl);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addGuard(String name, String address, String description, String phone, String password, String status,String imageUrl) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("Security").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Phone number already exists in Security
                    Toast.makeText(AdminAllocateRoomActivity.this, "This " + phone + " already exists as a Security Guard.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                } else {
                    // Phone number doesn't exist in Security, proceed with adding
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    userdataMap.put("status", "Security");
                    userdataMap.put("description", description);
                    userdataMap.put("address", address);
                    userdataMap.put("imageUrl", imageUrl);

                    RootRef.child("Security").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminAllocateRoomActivity.this, "Congratulations, you have added him as a new Security Guard", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(phone, null, "Congratulations! You have now Added as Security Guard " , null, null);
                                        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                                        // Remove the user from WaitingUsers after adding as Security Guard
                                        RootRef.child("WaitingUsers").child(phone).removeValue();

                                        Intent intent = new Intent(AdminAllocateRoomActivity.this, AdminAcceptNewResidentActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminAllocateRoomActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }

        });
    }

//here security end

    private void SaveUserToAdmin(final String phone) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("WaitingUsers").child(phone).exists()) {
                    Users usersData = dataSnapshot.child("WaitingUsers").child(phone).getValue(Users.class);
                    String address=usersData.getAddress();
                    String description=usersData.getDescription();
                    String name=usersData.getName();
                    String password=usersData.getPassword();
                    String phone=usersData.getPhone();
                    String status=usersData.getStatus();
                    String imageUrl=usersData.getImageUrl();
                    loadingBar.setTitle("Creating him Admin");
                    loadingBar.setMessage("Please wait, while we are checking the credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    addAdmin(name, address,description,phone, password,status,imageUrl);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addAdmin(String name, String address, String description, String phone, String password, String status,String imageUrl) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("Admins").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Phone number already exists in Admins
                    Toast.makeText(AdminAllocateRoomActivity.this, "This " + phone + " already exists as an Admin.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                } else {
                    // Phone number doesn't exist in Admins, proceed with adding
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    userdataMap.put("status", "Admin");
                    userdataMap.put("description", description);
                    userdataMap.put("address", address);
                    userdataMap.put("imageUrl", imageUrl);

                    RootRef.child("Admins").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminAllocateRoomActivity.this, "Congratulations, you have added him as a new Admin", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        // Remove the user from WaitingUsers after adding as Admin
                                        RootRef.child("WaitingUsers").child(phone).removeValue();
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(phone, null, "Congratulations! You have been now be an admin" , null, null);
                                        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(AdminAllocateRoomActivity.this, AdminAcceptNewResidentActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminAllocateRoomActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }



}


