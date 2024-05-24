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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Custom.ForgotPasswordDialogFragment;
import com.diev.salarymaster.Fragment.Fragment_Manager;
import com.diev.salarymaster.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Activity_Login extends AppCompatActivity {
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    EditText edtemail, edtpassword;
    Button btnlogin, btnsignup;
    TextView tvforgot;
    View viewBlocking;
    ProgressBar progressBar;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setControl();
        setEvent();
        initializeBiometricPrompt();
        AutoLogin();
    }

    private void setControl() {
        edtemail = findViewById(R.id.edt_login_email);
        edtpassword = findViewById(R.id.edt_login_password);
        btnlogin = findViewById(R.id.btn_login_login);
        btnsignup = findViewById(R.id.btn_login_signup);
        tvforgot = findViewById(R.id.tv_login_forgetpassword);
        viewBlocking = findViewById(R.id.viewBlocking_login);
        progressBar = findViewById(R.id.progressBar_login);
    }

    private void setEvent() {
        btnsignup.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_Login.this, Activity_Signup.class);
            startActivity(intent);
        });
        btnlogin.setOnClickListener(view -> {
            String email = String.valueOf(edtemail.getText());
            String password = String.valueOf(edtpassword.getText());
            if (!email.trim().equals("") && !password.trim().equals("")) {
                login(email, password);
            }
        });
        tvforgot.setOnClickListener(view -> {
            ForgotPasswordDialogFragment dialogFragment = new ForgotPasswordDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "forgot_password_dialog");
            InformationAlert alert = new InformationAlert("Hãy kiểm tra email sau khi nhập thông tin nhóe!");
            alert.show(getSupportFragmentManager(), "custom_dialog_fragment");
        });
    }

    private void initializeBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(Activity_Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showToast("Lỗi xác thực: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                proceedToMainScreen();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showToast("Xác thực thất bại");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Xác thực sinh trắc học")
                .setSubtitle("Sử dụng vân tay của bạn để đăng nhập")
                .setNegativeButtonText("Hủy")
                .build();
    }

    private void AutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        String userId = sharedPreferences.getString(uuid, "");
        if (!userId.isEmpty()) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void login(String email, String password) {
        viewBlocking.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                saveLoginInfo(user.getUid());
                proceedToMainScreen();
            } else {
                InformationAlert dialogFragment = new InformationAlert("Email hoặc mật khẩu không đúng!");
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            }
            viewBlocking.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        });
    }

    private void saveLoginInfo(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(uuid, userId);
        editor.apply();
    }

    private void proceedToMainScreen() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        String userId = sharedPreferences.getString(uuid, "");
        Intent intent = new Intent(Activity_Login.this, Fragment_Manager.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage("Mã đã được gửi thành công đến địa chỉ email của bạn.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi")
                .setMessage("Không thể gửi mã đến địa chỉ email của bạn. Vui lòng thử lại sau.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
