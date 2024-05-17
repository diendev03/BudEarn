package com.diev.salarymaster.Custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.diev.salarymaster.Activity.Activity_Login;
import com.diev.salarymaster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialogFragment extends DialogFragment {

    private EditText edtEmail;
    private boolean isResetSuccessful = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgotpassword, null);
        edtEmail = view.findViewById(R.id.edt_forgot_email);
        builder.setView(view)
                .setTitle("Quên mật khẩu")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = edtEmail.getText().toString().trim();
                        sendCodeResetPassword(email);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    // Gửi mã xác nhận đổi mật khẩu
    private void sendCodeResetPassword(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (getActivity() != null) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Mã đã được gửi thành công đến địa chỉ email của bạn.", Toast.LENGTH_SHORT).show();
                        // Gửi thành công
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Không thể gửi mã đến địa chỉ email của bạn. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        // Gửi không thành công
                        dismiss();
                    }
                } else {
                    // Log lỗi hoặc xử lý khi getActivity() là null
                }
            }
        });
    }

    // Thông báo thành công đến Activity_Login
    private void notifySuccess() {
        if (getActivity() instanceof Activity_Login) {
            ((Activity_Login) getActivity()).showSuccessDialog();
        }
    }

    // Thông báo lỗi đến Activity_Login
    private void notifyError() {
        if (getActivity() instanceof Activity_Login) {
            ((Activity_Login) getActivity()).showErrorDialog();
        }
    }
}
