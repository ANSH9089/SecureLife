package com.example.securelife.ui.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securelife.Model.NoticeData;
import com.example.securelife.R;
import com.example.securelife.ViewHolder.NoticeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class noticeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference().child("Notice");
        unverifiedProductRef = noticeRef;

        recyclerView = view.findViewById(R.id.admin_products_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        loadNotices();

        return view;
    }

    private void loadNotices() {
        FirebaseRecyclerOptions<NoticeData> options = new FirebaseRecyclerOptions.Builder<NoticeData>()
                .setQuery(unverifiedProductRef.orderByChild("date").equalTo("15-02-24"), NoticeData.class).build();

        FirebaseRecyclerAdapter<NoticeData, NoticeViewHolder> adapter = new FirebaseRecyclerAdapter<NoticeData, NoticeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(NoticeViewHolder holder, int position, NoticeData model) {
                holder.txtProductName.setText("Name: " + model.getTitle());
                // Assuming you have an 'imageView' in your NoticeViewHolder
                Picasso.get().load(model.getImage()).into(holder.imageView);
            }

            @Override
            public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice, parent, false);
                return new NoticeViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
