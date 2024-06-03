package com.diev.salarymaster.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Custom.ForgotPasswordDialogFragment;
import com.diev.salarymaster.Fragment.Fragment_Manager;
import com.diev.salarymaster.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Activity_Login extends AppCompatActivity {
    // Shared Preferences keys
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";

    // UI elements
    EditText edtemail, edtpassword;
    Button btnlogin, btnsignup;
    TextView tvforgot;
    View viewBlocking;
    ProgressBar progressBar;

    // Biometric authentication components
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge UI
        setContentView(R.layout.activity_login);
        setControl(); // Initialize UI elements
        setEvent(); // Set event listeners
        AutoLogin(); // Attempt auto-login
    }

    // Initialize UI elements
    private void setControl() {
        edtemail = findViewById(R.id.edt_login_email);
        edtpassword = findViewById(R.id.edt_login_password);
        btnlogin = findViewById(R.id.btn_login_login);
        btnsignup = findViewById(R.id.btn_login_signup);
        tvforgot = findViewById(R.id.tv_login_forgetpassword);
        viewBlocking = findViewById(R.id.viewBlocking_login);
        progressBar = findViewById(R.id.progressBar_login);
    }

    // Set event listeners for buttons and text views
    private void setEvent() {
        btnsignup.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_Login.this, Activity_Signup.class);
            startActivity(intent); // Navigate to signup activity
        });
        btnlogin.setOnClickListener(view -> {
            String email = String.valueOf(edtemail.getText());
            String password = String.valueOf(edtpassword.getText());
            if (!email.trim().equals("") && !password.trim().equals("")) {
                login(email, password); // Perform login
            }
        });
        tvforgot.setOnClickListener(view -> {
            ForgotPasswordDialogFragment dialogFragment = new ForgotPasswordDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "forgot_password_dialog");
            InformationAlert alert = new InformationAlert("Hãy kiểm tra email sau khi nhập thông tin nhóe!");
            alert.show(getSupportFragmentManager(), "custom_dialog_fragment");
        });
    }

    // Initialize biometric prompt for fingerprint authentication
    private void initializeBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(Activity_Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                InformationAlert dialogFragment = new InformationAlert("Xác thực thất bại vui lòng thử lại!");
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                proceedToMainScreen(); // Authentication succeeded
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                InformationAlert dialogFragment = new InformationAlert("Xác thực thất bại vui lòng thử lại!");
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Xác thực sinh trắc học")
                .setSubtitle("Sử dụng vân tay của bạn để đăng nhập")
                .setNegativeButtonText("Hủy")
                .build();
    }

    // Attempt auto-login using saved credentials
    private void AutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        String userId = sharedPreferences.getString(uuid, "");
        if (!userId.trim().equals("")) {
            if (isBiometricSupported()){
                initializeBiometricPrompt();
                biometricPrompt.authenticate(promptInfo);// Prompt biometric authentication
            }else {
                proceedToMainScreen();
            }
        }
    }
    // Check if the device supports biometric authentication
    private boolean isBiometricSupported() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            BiometricManager biometricManager = (BiometricManager) getSystemService(Context.BIOMETRIC_SERVICE);
            return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
        } else {
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);
            return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
        }
    }
    // Perform login with email and password
    private void login(String email, String password) {
        viewBlocking.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                saveLoginInfo(user.getUid()); // Save login info
                proceedToMainScreen(); // Navigate to main screen
            } else {
                InformationAlert dialogFragment = new InformationAlert("Email hoặc mật khẩu không đúng!");
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            }
            viewBlocking.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        });
    }

    // Save login info to SharedPreferences
    private void saveLoginInfo(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(uuid, userId);
        editor.apply();
    }

    // Navigate to the main screen
    private void proceedToMainScreen() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        String userId = sharedPreferences.getString(uuid, "");
        Intent intent = new Intent(Activity_Login.this, Fragment_Manager.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    // Show a toast message
//    private void showToast(String message) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//    }

    // Show a success dialog
    public void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage("Mã đã được gửi thành công đến địa chỉ email của bạn.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Show an error dialog
    public void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi")
                .setMessage("Không thể gửi mã đến địa chỉ email của bạn. Vui lòng thử lại sau.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
