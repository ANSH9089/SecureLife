package com.example.securelife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securelife.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminAsignGiveBlockNoActivity extends AppCompatActivity {
    private TextView Name,Address,Description,Phone;
    private EditText Block;
    private Button assignBlock,decline;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_asign_give_block_no);
        Name=findViewById(R.id.register1_username_input);
        Address=findViewById(R.id.register1_username_address);
        Description=findViewById(R.id.register1_username_description);
        Phone=findViewById(R.id.register1_phone_number_input);
        Block=findViewById(R.id.register_allocate_room);
        assignBlock=findViewById(R.id.register1_btn);
        decline=findViewById(R.id.room_full);
        loadingBar = new ProgressDialog(this);

        Intent intent = getIntent();
        String phone= intent.getStringExtra("productId");

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
                    String phone=usersData.getPhone();
                    String status=usersData.getStatus();
                    Name.setText(name);
                    Address.setText(address);
                    Description.setText(description);
                    Phone.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        assignBlock.setOnClickListener(new View.OnClickListener() {
            Intent intent = getIntent();
            String productId = intent.getStringExtra("productId");
            @Override
            public void onClick(View view) {
                assignRoom(productId);
            }
        });
        
        
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(phone);
            }
        });
        
    }

    private void deleteUser(String productId) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("WaitingUsers").child(productId).removeValue();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(productId, null, "Your Request is Declined" , null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(AdminAsignGiveBlockNoActivity.this, AdminAcceptNewResidentActivity.class);
        startActivity(intent);
    }

    private void assignRoom(String phone) {
        String BlockNo = Block.getText().toString();
        if (TextUtils.isEmpty(BlockNo))
        {
            Toast.makeText(this, "Please assign the block", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Assigning Room");
            loadingBar.setMessage("Please wait, while we are checking for availability.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
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
                        String phone=usersData.getPhone();
                        String status=usersData.getStatus();
                        String password=usersData.getPassword();
                        String  imageUrl=usersData.getImageUrl();
                        Name.setText("Name :"+name);
                        Address.setText("Address :"+address);
                        Description.setText("Description : "+description);
                        Phone.setText("Phone :"+ phone);
                        loadingBar.setTitle("Assigning Him Room Available");
                        loadingBar.setMessage("Please wait, while we are checking the credentials.");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        AssignRoom(name, address,description,phone,status,BlockNo,password,imageUrl);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        }
    private void AssignRoom(String name, String address, String description, String phone, String status, String BlockNo,String password, String  imageUrl) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference residentRef = RootRef.child("Resident").child(BlockNo);

        residentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Room is already occupied
                    loadingBar.dismiss();
                    Toast.makeText(AdminAsignGiveBlockNoActivity.this, "This room is already occupied.", Toast.LENGTH_SHORT).show();
                } else {
                    // Room is available, proceed with assigning the room
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("name", name);
                    userdataMap.put("status", "Resident");
                    userdataMap.put("description", description);
                    userdataMap.put("address", address);
                    userdataMap.put("BlockNo", BlockNo);
                    userdataMap.put("password",password);
                    userdataMap.put("imageUrl", imageUrl);

                    RootRef.child("Resident").child(BlockNo).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminAsignGiveBlockNoActivity.this, "Congratulations, you have added him as a new Resident", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        // Remove the user from WaitingUsers after adding as Admin
                                        RootRef.child("WaitingUsers").child(phone).removeValue();

                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(phone, null, "Congratulations! You have been assigned to Room " + BlockNo, null, null);
                                        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();


                                        Intent intent = new Intent(AdminAsignGiveBlockNoActivity.this, AdminAcceptNewResidentActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminAsignGiveBlockNoActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
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

