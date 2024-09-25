package com.example.securelife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.securelife.Interface.ItemClickListner;
import com.example.securelife.Model.Users;
import com.example.securelife.R;
import com.example.securelife.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminAcceptNewResidentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accept_new_resident);

        unverifiedProductRef= FirebaseDatabase.getInstance().getReference().child("WaitingUsers");

        recyclerView=findViewById(R.id.admin_products_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users>options=new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(unverifiedProductRef.orderByChild("status").equalTo("Not Approved"), Users.class).build();
        FirebaseRecyclerAdapter<Users, ProductViewHolder>adapter= new FirebaseRecyclerAdapter<Users, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Users model) {
                holder.txtPersontName.setText("Name: "+model.getName());
                holder.txtPersonDescription.setText("Description: "+model.getDescription());
                holder.txtPersonAddress.setText("Working Address: "+model.getAddress() );
                holder.txtPersonContact.setText("Phone Number: "+model.getPhone());
                holder.txtPersonStatus.setText("Status:  Unapproved");
                Picasso.get().load(model.getImageUrl()).into(holder.txtimage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String productId=model.getPhone();
                        Intent i=new Intent(AdminAcceptNewResidentActivity.this,AdminAllocateRoomActivity.class);
                        i.putExtra("productId", productId);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_check_resident, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void ChangeProductState(String productID) {
        unverifiedProductRef.child(productID).child("status").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminAcceptNewResidentActivity.this, "That Item Has Been Approved,And It Is Now Available For Sale BY Seller.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}