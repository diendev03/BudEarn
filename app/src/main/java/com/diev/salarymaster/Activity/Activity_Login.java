package com.diev.salarymaster.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Custom.ForgotPasswordDialogFragment;
import com.diev.salarymaster.Fragment.Fragment_Manager;
import com.diev.salarymaster.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Login extends AppCompatActivity {
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    EditText edtemail, edtpassword;
    Button btnlogin, btnsignup;
    TextView tvforgot;
    View viewBlocking;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setControl();
        setEvent();
        AutoLogin();
    }
    private void setControl() {
        edtemail = findViewById(R.id.edt_login_email);
        edtpassword = findViewById(R.id.edt_login_password);
        btnlogin = findViewById(R.id.btn_login_login);
        btnsignup = findViewById(R.id.btn_login_signup);
        tvforgot = findViewById(R.id.tv_login_forgetpassword);
        viewBlocking=findViewById(R.id.viewBlocking_login);
        progressBar=findViewById(R.id.progressBar_login);
    }

    private void setEvent() {
        btnsignup.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_Login.this, Activity_Signup.class);
            startActivity(intent);
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= String.valueOf(edtemail.getText());
                String password= String.valueOf(edtpassword.getText());
                if (!email.trim().equals("")&&!password.trim().equals("")){
                    login(email,password);
                }
            }
        });
        tvforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordDialogFragment dialogFragment = new ForgotPasswordDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "forgot_password_dialog");
                InformationAlert alert = new InformationAlert("Hãy kiểm tra email nhóe!");
                alert.show(getSupportFragmentManager(), "custom_dialog_fragment");
            }
        });
    }
    //Tự động đăng nhập
    private void AutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        String userId = sharedPreferences.getString(uuid, "");
        if (!userId.isEmpty()) {
            // Đã có thông tin đăng nhập, chuyển hướng đến màn hình chính
            Intent intent = new Intent(Activity_Login.this, Fragment_Manager.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        }
    }
    //Đăng nhập
    private void login(String email, String password) {
        viewBlocking.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Đăng nhập thành công
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Bạn có thể lấy thông tin của user từ đối tượng user ở đây
                saveLoginInfo(user.getUid());
                Intent intent = new Intent(Activity_Login.this, Fragment_Manager.class);
                assert user != null;
                intent.putExtra("userId", user.getUid());
                startActivity(intent);
                finish();
            } else {
                // Đăng nhập thất bại
                InformationAlert dialogFragment = new InformationAlert("Email hoặc mật khẩu không đúng!");
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            }
            viewBlocking.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        });
    } //Lưu thông tin đăng nhập
    private void saveLoginInfo(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(uuid, userId);
        editor.apply();
    }


    // Hiển thị thông báo thành công
    public void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage("Mã đã được gửi thành công đến địa chỉ email của bạn.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    // Hiển thị thông báo lỗi
    public void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi")
                .setMessage("Không thể gửi mã đến địa chỉ email của bạn. Vui lòng thử lại sau.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}