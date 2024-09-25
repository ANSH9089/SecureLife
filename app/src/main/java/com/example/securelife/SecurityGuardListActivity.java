package com.example.securelife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.securelife.Model.Resident;
import com.example.securelife.Model.Security;
import com.example.securelife.R;
import com.example.securelife.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SecurityGuardListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_guard_list);

        unverifiedProductRef= FirebaseDatabase.getInstance().getReference().child("Security");

        recyclerView=findViewById(R.id.admin_products_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Security> options=new FirebaseRecyclerOptions.Builder<Security>()
                .setQuery(unverifiedProductRef.orderByChild("status").equalTo("Security"), Security.class).build();
        FirebaseRecyclerAdapter<Security, ProductViewHolder> adapter= new FirebaseRecyclerAdapter<Security, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Security model) {
                holder.txtPersontName.setText("Name: "+model.getName());
                holder.txtPersonContact.setText("Contact No: "+model.getPhone());
                holder.txtPersonStatus.setText("Room No:"+model.getStatus());
                holder.txtPersonAddress.setText("description: "+model.getDescription());
                holder.txtPersonStatus.setText("Working Address: "+model.getAddress());
                Picasso.get().load(model.getImage()).into(holder.txtimage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String productID= model.getPhone();
                        CharSequence options[]=new CharSequence[]{
                                "Yes",
                                "NO"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(SecurityGuardListActivity.this);
                        builder.setTitle("Do You Want to Remove this Resident");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    ChangeProductState(productID);
                                }
                                if(i==1){

                                }
                            }
                        });
                        builder.show();
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
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Security").child(productID).removeValue();
    }
}