package com.diev.salarymaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_Login extends AppCompatActivity {
    EditText edtemail, edtpassword;
    Button btnlogin, btnsignup;
    TextView tvforgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setControl();
        setEvent();
    }

    private void setEvent() {
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Login.this, Activity_Signup.class);
                startActivity(intent);
            }
        });
    }

    private void setControl() {
        edtemail = findViewById(R.id.edt_login_email);
        edtpassword = findViewById(R.id.edt_login_password);
        btnlogin = findViewById(R.id.btn_login_login);
        btnsignup = findViewById(R.id.btn_login_signup);
        tvforgot = findViewById(R.id.tv_login_forgetpassword);
    }
}