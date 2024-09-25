package com.example.securelife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class VisitorReason extends AppCompatActivity {
    private ImageView pickEntryTimeButton, pickDepartTimeButton;
    private TextView entryTimeTextView, departTimeTextView;
    private Calendar calendar;
    private int currentHour, currentMinute;
    private String amPm;
    public String ResidentPhone;


    private Button NewVisitor;
    private EditText InputName, InputReasonToMeet, InputBlockNo,InputPhoneNo;
    private TextView InputArrivalTime,InputDepartTime;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_reason);
        pickEntryTimeButton = findViewById(R.id.pickEntryTime);
        pickDepartTimeButton = findViewById(R.id.pickDepartTime);
        entryTimeTextView = findViewById(R.id.security_entry_time);
        departTimeTextView = findViewById(R.id.security_depart_time);
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
        loadingBar = new ProgressDialog(VisitorReason.this);


        InputName=findViewById(R.id.visitor_username_input);
        InputReasonToMeet=findViewById(R.id.visitor_reason);
        InputBlockNo=findViewById(R.id.visitor_block_no);
        InputPhoneNo=findViewById(R.id.visitor_phone_number_input);
        InputArrivalTime=findViewById(R.id.security_entry_time);
        InputDepartTime=findViewById(R.id.security_depart_time);
        NewVisitor=findViewById(R.id.visitor_register_btn);



        NewVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddVisitor();
            }
        });


        pickEntryTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(entryTimeTextView);
            }
        });

        pickDepartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(departTimeTextView);
            }
        });
    }

    private void AddVisitor() {
        String name = InputName.getText().toString();
        String reason = InputReasonToMeet.getText().toString();
        String blockNo = InputBlockNo.getText().toString();
        String phone=InputPhoneNo.getText().toString();
        String arrival=InputArrivalTime.getText().toString();
        String departure=InputDepartTime.getText().toString();



        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(reason))
        {
            Toast.makeText(this, "Please write your reason...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(blockNo))
        {
            Toast.makeText(this, "Please write the room no you want to meet", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone) )
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (phone.length() != 10) {
            Toast.makeText(this, "Phone number should be 10 digits.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(arrival))
        {
            Toast.makeText(this, "Please write your arrival time...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(departure))
        {
            Toast.makeText(this, "Please write your departure time...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidatephoneNumber(name, reason,blockNo,arrival, departure,phone);

        }
    }
    private void ValidatephoneNumber(String name, String reason, String blockNo, String arrival, String departure, String phone) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();



        final DatabaseReference reference;

        reference=FirebaseDatabase.getInstance().getReference("Resident");
        reference.child(blockNo).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot=task.getResult();
                         ResidentPhone=String.valueOf(dataSnapshot.child("phone").getValue());

                    }
                    else{
                        Toast.makeText(VisitorReason.this, "User Doesn't Exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(VisitorReason.this, "Failed To Show", Toast.LENGTH_SHORT).show();
                }
            }
        });



        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String visitorId = phone + "_" + arrival.replace("/", "_").replace(":", "_");
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Visitor").child(visitorId).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("reason", reason);
                    userdataMap.put("blockNo", blockNo);
                    userdataMap.put("arrival",arrival);
                    userdataMap.put("departure",departure);
                    userdataMap.put("phone",phone);
                    userdataMap.put("status","Not Approved");

                    RootRef.child("Visitor").child(visitorId).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        SmsManager smsManager=SmsManager.getDefault();
                                        smsManager.sendTextMessage(ResidentPhone,null,name+" has requested to visit you for the reason "+reason,null,null);
                                        Toast.makeText(VisitorReason.this, "Congratulations, your request has been sent.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(VisitorReason.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(VisitorReason.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(VisitorReason.this, "  already have entry.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intent = new Intent(VisitorReason.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }










    private void showDateTimePicker(final TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                VisitorReason.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                                        textView.setText(dayOfMonth + "/" + (month + 1) + "/" + year + " " + formattedTime);
                                    }
                                },
                                currentHour,
                                currentMinute,
                                false
                        );
                        timePickerDialog.show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}