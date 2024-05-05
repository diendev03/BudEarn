package com.diev.salarymaster.Custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class ImagePicker {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1002;

    public interface ImagePickerListener {
        void onImageChosen(Uri chosenImageUri);
    }

    public static void showImagePicker(Activity activity, ImagePickerListener listener) {
        String[] options = {"Camera", "Thư viện ảnh"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chọn ảnh từ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Camera
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else if (which == 1) {
                    // Thư viện ảnh
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                }
            }
        });
        builder.show();
    }

    public static void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity, ImagePickerListener listener) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                // Xử lý chọn ảnh từ thư viện
                handleImageSelection(data, activity, listener);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Xử lý chụp ảnh từ camera
                handleCameraImage(data, activity, listener);
            }
        }
    }

    private static void handleImageSelection(Intent data, Activity activity, ImagePickerListener listener) {
        if (data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            listener.onImageChosen(imageUri);
        }
    }

    private static void handleCameraImage(Intent data, Activity activity, ImagePickerListener listener) {
        if (data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                Uri imageUri = getImageUri(activity, imageBitmap);
                listener.onImageChosen(imageUri);
            }
        }
    }

    private static Uri getImageUri(Activity activity, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}
