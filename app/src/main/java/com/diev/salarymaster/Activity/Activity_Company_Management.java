package com.diev.salarymaster.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.diev.salarymaster.R;

public class Activity_Company_Management extends AppCompatActivity {
ImageButton btn_new;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company_management);
        setControl();
        setEvent();
    }

    private void setEvent() {
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Activity_Company_Management.this, Activity_Add_Company.class);
                startActivity(intent);
            }
        });
    }

    private void setControl() {
        btn_new=findViewById(R.id.btn_company_new);
    }
}