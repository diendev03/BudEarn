package com.diev.salarymaster;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.Nullable;

public class CustomAlertDialogFragment extends DialogFragment {

    private String message;
    public CustomAlertDialogFragment( String message) {
        this.message = message;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo");
        builder.setMessage(message);
        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.back);

        builder.setIcon(icon);

        // Nút OK
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Đóng dialog khi người dùng nhấn nút OK
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
