package com.example.securelife.ViewHolder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.securelife.Interface.ItemClickListner;
import com.example.securelife.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtPersontName, txtPersonDescription, txtPersonStatus,txtPersonContact,txtPersonAddress;
    public ItemClickListner listner;
    public ImageView txtimage;


    public ProductViewHolder(View itemView)
    {
        super(itemView);

        txtPersontName=itemView.findViewById(R.id.user_name);
        txtPersonContact=itemView.findViewById(R.id.user_contact);
        txtPersonAddress=itemView.findViewById(R.id.user_address);
        txtPersonStatus=itemView.findViewById(R.id.user_status);
        txtPersonDescription=itemView.findViewById(R.id.user_description);
        txtimage=itemView.findViewById(R.id.setimage);

    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
