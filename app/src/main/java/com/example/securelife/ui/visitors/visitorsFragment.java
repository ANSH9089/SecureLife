package com.example.securelife.ui.visitors;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.securelife.Model.Visitors;
import com.example.securelife.Prevalent.Prevalent;
import com.example.securelife.R;
import com.example.securelife.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class visitorsFragment extends Fragment {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitors, container, false);

        unverifiedProductRef = FirebaseDatabase.getInstance().getReference().child("Visitor");

        recyclerView = view.findViewById(R.id.visitor_approval);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        displayVisitorList();

        return view;
    }
    private void displayVisitorList() {
        FirebaseRecyclerOptions<Visitors> options = new FirebaseRecyclerOptions.Builder<Visitors>()
                .setQuery(unverifiedProductRef.orderByChild("blockNo").equalTo(Prevalent.currentOnlineUser.getBlockNo()), Visitors.class).build();

        FirebaseRecyclerAdapter<Visitors, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Visitors, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Visitors model) {
                holder.txtPersontName.setText("Name: "+model.getName());
                holder.txtPersonContact.setText("Phone Number: "+model.getPhone());
                holder.txtPersonDescription.setText("Reason: "+ model.getReason());
                holder.txtPersonAddress.setText("Time: "+model.getArrival()+" to "+model.getDeparture());
                holder.txtPersonStatus.setText("status: "+model.getStatus());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String productID=  model.getPhone() + "_" + model.getArrival().replace("/", "_").replace(":", "_");
                        CharSequence options[]=new CharSequence[]{
                                "Yes",
                                "NO"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // use requireContext() to get the Context

                        builder.setTitle("Do You Want to Accept This Visitor");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(model.getPhone(), null, "Allow Him", null, null);

                                    ChangeProductState(productID);
                                }
                                if(i==1){
                                    final DatabaseReference RootRef;
                                    RootRef = FirebaseDatabase.getInstance().getReference();
                                    RootRef.child("Visitor").child(productID).removeValue();
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
        unverifiedProductRef.child(productID).child("status").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(requireContext(), "Visitor has been approved.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}