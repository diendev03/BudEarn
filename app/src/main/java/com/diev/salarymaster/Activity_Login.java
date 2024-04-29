package com.diev.salarymaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.diev.salarymaster.Fragment.Fragment_Manager;

public class Activity_Login extends AppCompatActivity {
Button btnffff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setControl();
        setEvent();
    }

    private void setEvent() {
        btnffff.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_Login.this, Fragment_Manager.class);
            startActivity(intent);
            finish();
        });
    }

    private void setControl() {
        btnffff=findViewById(R.id.btnffff);
    }
}