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

import com.diev.salarymaster.Model.Company;
import com.diev.salarymaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Company extends RecyclerView.Adapter<Adapter_Company.CompanyViewHolder> {
    private ArrayList<Company> companies = new ArrayList<>();
    private Context context;
    private String userId;
    View viewBlocking;
    ProgressBar progressBar;


    public Adapter_Company(Context context, String userId, View viewBlocking, ProgressBar progressBar) {
        this.context = context;
        this.userId = userId;
        this.viewBlocking=viewBlocking;
        this.progressBar=progressBar;
        khoitao();
    }

    private void khoitao() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Company").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companies.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Company company = dataSnapshot.getValue(Company.class);
                    if (company != null) {
                        companies.add(company);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @NonNull
    @Override
    public Adapter_Company.CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemCompany = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new Adapter_Company.CompanyViewHolder(itemCompany);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Company.CompanyViewHolder holder, int position) {
        Company company = companies.get(position);
        holder.tv_name.setText(company.getName());
        if (company.getAvatar() != null && !company.getAvatar().isEmpty()) {
            String imageUrl = company.getAvatar();

            // Hiển thị ProgressBar
            holder.progressBar.setVisibility(View.VISIBLE);

            // Sử dụng Picasso để tải ảnh
            Picasso.get().load(imageUrl).into(holder.iv_avatar, new Callback() {
                @Override
                public void onSuccess() {
                    // Ẩn ProgressBar khi tải thành công
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    // Ẩn ProgressBar và xử lý lỗi
                    holder.progressBar.setVisibility(View.GONE);
                    // Bạn có thể đặt một ảnh mặc định nếu tải ảnh thất bại
                    holder.iv_avatar.setImageResource(R.drawable.default_pic);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public class CompanyViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tv_name;
        ImageView iv_avatar;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_item_company_name);
            iv_avatar = itemView.findViewById(R.id.iv_item_company_avatar);
            progressBar = itemView.findViewById(R.id.progressBar_item_company_avatar);
        }
    }
}
