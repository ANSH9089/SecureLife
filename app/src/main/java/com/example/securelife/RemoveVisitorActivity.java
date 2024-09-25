package com.example.securelife;

import static android.text.TextUtils.replace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RemoveVisitorActivity extends AppCompatActivity {
    private ImageView pickEntryTimeButton;
    private TextView entryTimeTextView;
    private EditText visitorRemovalPhoneEditText;
    private Button mainLoginButton;

    private int currentHour, currentMinute;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_visitor);
        pickEntryTimeButton = findViewById(R.id.pickEntryTime);
        entryTimeTextView = findViewById(R.id.visitor_removal_entry_time);
        visitorRemovalPhoneEditText = findViewById(R.id.VisitorRemovalPhone);
        mainLoginButton = findViewById(R.id.main_login_btn);

        // Get current time
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        // Set click listener for picking entry time
        pickEntryTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(entryTimeTextView);
            }
        });

        mainLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = visitorRemovalPhoneEditText.getText().toString();
                String arrivaltime = entryTimeTextView.getText().toString();
                String visitorId = phone + "_" + arrivaltime.replace("/", "_").replace(":", "_");
                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference();
                RootRef.child("Visitor").child(visitorId).removeValue();
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
                                RemoveVisitorActivity.this,
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