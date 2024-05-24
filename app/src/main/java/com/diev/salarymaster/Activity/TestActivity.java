package com.diev.salarymaster.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.widget.Button;
import android.widget.Toast;

import com.diev.salarymaster.R;

import java.util.concurrent.Executor;

public class TestActivity extends AppCompatActivity {
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button btnAuthenticate = findViewById(R.id.btnAuthenticate);

        // Khởi tạo Biometric Manager
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            // Trường hợp thiết bị có thể sử dụng cảm biến vân tay
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "Bạn có thể sử dụng cảm biến vân tay để đăng nhập", Toast.LENGTH_SHORT).show();
                break;
            // Trường hợp thiết bị không có phần cứng cảm biến vân tay
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Thiết bị của bạn không có cảm biến vân tay", Toast.LENGTH_SHORT).show();
                break;
            // Trường hợp cảm biến vân tay hiện không khả dụng
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Cảm biến vân tay hiện không khả dụng", Toast.LENGTH_SHORT).show();
                break;
            // Trường hợp không có vân tay nào được đăng ký
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Thiết bị của bạn chưa đăng ký vân tay, vui lòng kiểm tra cài đặt bảo mật", Toast.LENGTH_SHORT).show();
                break;
        }

        // Tạo một thread pool cho BiometricPrompt
        Executor executor = ContextCompat.getMainExecutor(this);

        // Khởi tạo BiometricPrompt với callback
        biometricPrompt = new BiometricPrompt(TestActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Xử lý khi có lỗi xảy ra trong quá trình xác thực
                Toast.makeText(getApplicationContext(), "Lỗi xác thực: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Xử lý khi xác thực thành công
                Toast.makeText(getApplicationContext(), "Xác thực thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Xử lý khi xác thực thất bại
                Toast.makeText(getApplicationContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Tạo thông tin hiển thị cho BiometricPrompt
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập bằng sinh trắc học cho ứng dụng của tôi")
                .setSubtitle("Sử dụng thông tin sinh trắc học của bạn để đăng nhập")
                .setNegativeButtonText("Sử dụng mật khẩu tài khoản")
                .build();

        // Gắn sự kiện click cho nút bấm để bắt đầu xác thực
        btnAuthenticate.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }
}
