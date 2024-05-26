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

import com.diev.salarymaster.Adapter.Adapter_Business;
import com.diev.salarymaster.R;

public class Activity_Business_Management extends AppCompatActivity {
    ImageButton btn_new, ib_back;
    RecyclerView rcv_listBusiness;
    private Adapter_Business adapter_business;
    View viewBlocking;
    ProgressBar progressBar;
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_management);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        setControl();
        setEvent();
        adapter_business = new Adapter_Business(this, userId,progressBar);
        rcv_listBusiness.setAdapter(adapter_business); // Thêm adapter vào RecyclerView
    }

    private void setEvent() {
        rcv_listBusiness.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcv_listBusiness.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        btn_new.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_Business_Management.this, Activity_Add_Business.class);
            startActivity(intent);
        });
        ib_back.setOnClickListener(view -> finish());
    }

    private void setControl() {
        btn_new = findViewById(R.id.ib_business_new);
        ib_back = findViewById(R.id.ib_business_back);
        rcv_listBusiness = findViewById(R.id.rcv_list_business);
        progressBar = findViewById(R.id.progressBar_business_management);
    }
}