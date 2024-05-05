package com.diev.salarymaster.Custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.diev.salarymaster.R;

public class ConfirmationAlert extends DialogFragment {

    private final String message;
    private final String positiveButtonText;
    private final String negativeButtonText;
    private final DialogInterface.OnClickListener negativeClickListener;

    public ConfirmationAlert(String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener negativeClickListener) {
        this.message = message;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.negativeClickListener = negativeClickListener;
    }

    @NonNull
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo");
        builder.setMessage(message);
        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.notification);
        builder.setIcon(icon);

        // Nút OK
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss dialog
                dialog.dismiss();
            }
        });

        // Nút Cancel
        builder.setNegativeButton(negativeButtonText, negativeClickListener);

        return builder.create();
    }
}
