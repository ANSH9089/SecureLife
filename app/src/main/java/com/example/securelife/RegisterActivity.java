package com.example.securelife;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.securelife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
public class RegisterActivity extends AppCompatActivity
{
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword,InputAddress,InputDescription;
    private ProgressDialog loadingBar;
    CircleImageView rgProfileImg;
    Uri imageURI;
    FirebaseStorage storage;
    String imageuri;

    private Uri imageUri;

    private StorageReference storageReference;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InputAddress=(EditText)findViewById(R.id.register_username_address);
        InputDescription=findViewById(R.id.register_username_description);
        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        rgProfileImg = findViewById(R.id.profilerg0);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profile_images");

        rgProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });



        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateAccount();
            }
        });
    }



    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String address=InputAddress.getText().toString();
        String description=InputDescription.getText().toString();
        String status="Not Approved";
        String statusWhatsup = "Hey I'm Using This Application";


        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address))
        {
            Toast.makeText(this, "Please write your address...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write your description...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone) )
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (phone.length() != 10) {
            Toast.makeText(this, "Phone number should be 10 digits.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatephoneNumber(name, address,description,phone, password,status);
        }
    }



    private void ValidatephoneNumber(final String name,final String address,final String description, final String phone, final String password,final String status)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("WaitingUsers").child(phone);

        if (imageUri != null) {
            // If an image is selected, upload it to Firebase Storage
            final StorageReference imageRef = storageReference.child(phone + ".jpg");
            imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                saveUserData(name, address, description, phone, password, status, imageUrl);
                            }
                        });
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Please Select the Image", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveUserData(String name, String address, String description, String phone, String password,
                              String status, String imageUrl) {
        HashMap<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("phone", phone);
        userDataMap.put("password", password);
        userDataMap.put("name", name);
        userDataMap.put("status", status);
        userDataMap.put("description", description);
        userDataMap.put("address", address);
        userDataMap.put("imageUrl", imageUrl);
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("WaitingUsers").child(phone);


        rootRef.updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            rgProfileImg.setImageURI(imageUri);
        }
    }

}