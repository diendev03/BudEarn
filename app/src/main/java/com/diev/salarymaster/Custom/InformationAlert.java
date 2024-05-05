package com.diev.salarymaster.Custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.diev.salarymaster.R;

public class InformationAlert extends DialogFragment {

    private String message;
    public InformationAlert(String message) {
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
