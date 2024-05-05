package com.diev.salarymaster.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diev.salarymaster.Adapter.Adapter_Company;
import com.diev.salarymaster.R;

public class Activity_Company_Management extends AppCompatActivity {
ImageButton btn_new,ib_back;
RecyclerView rcv_listCompany;
private Adapter_Company adapter_company;
View viewBlocking;
ProgressBar progressBar;
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company_management);
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        adapter_company=new Adapter_Company(this,userId);
        setControl();
        setEvent();
    }

    private void setEvent() {
        rcv_listCompany.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rcv_listCompany.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcv_listCompany.setAdapter(adapter_company);

        btn_new.setOnClickListener(view -> {
            Intent intent=new Intent(Activity_Company_Management.this, Activity_Add_Company.class);
            startActivity(intent);
        });
        ib_back.setOnClickListener(view -> finish());
    }

    private void setControl() {
        btn_new=findViewById(R.id.ib_company_new);
        ib_back=findViewById(R.id.ib_company_back);
rcv_listCompany=findViewById(R.id.rcv_listcompany);
    }
}