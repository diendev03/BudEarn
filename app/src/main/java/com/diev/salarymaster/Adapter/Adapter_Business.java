package com.diev.salarymaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diev.salarymaster.Model.Business;
import com.diev.salarymaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Adapter_Business.java
public class Adapter_Business extends RecyclerView.Adapter<Adapter_Business.BusinessViewHolder> {
    private ArrayList<Business> businessList = new ArrayList<>();
    private Context context;
    private String userId;
    View viewBlocking;
    ProgressBar progressBar;

    public Adapter_Business(Context context, String userId, ProgressBar progressBar) {
        this.context = context;
        this.userId = userId;
        this.progressBar = progressBar;
        khoitao();
    }

    private void khoitao() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Business").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                businessList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Business business = dataSnapshot.getValue(Business.class);
                    if (business != null) {
                        businessList.add(business);
                    }
                }
                notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemBusiness = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(itemBusiness);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        Business business = businessList.get(position);
        holder.tv_name.setText(business.getName());
        if (business.getAvatar() != null && !business.getAvatar().isEmpty()) {
            String imageUrl = business.getAvatar();
            holder.progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).into(holder.iv_avatar, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.iv_avatar.setImageResource(R.drawable.business);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public class BusinessViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tv_name;
        ImageView iv_avatar;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_item_business_name);
            iv_avatar = itemView.findViewById(R.id.iv_item_business_avatar);
            progressBar = itemView.findViewById(R.id.progressBar_item_business_avatar);
        }
    }
}
