package com.diev.salarymaster.Custom;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID;

public class ImageUploader {

    // Firebase Storage Reference
    private StorageReference storageReference;

    // Constructor
    public ImageUploader( String pathString) {
        // Khởi tạo Firebase Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Tham chiếu đến thư mục trong Firebase Storage bạn muốn lưu ảnh
        storageReference = storage.getReference().child(pathString);
    }

    // Hàm tải lên ảnh lên Storage
    public void uploadImage(Uri imageUri, final OnSuccessListener<Uri> onSuccessListener, final OnFailureListener onFailureListener) {
        // Tạo một tên ngẫu nhiên cho ảnh
        String imageName = UUID.randomUUID().toString();
        // Tạo tham chiếu đến file trong Firebase Storage
        final StorageReference imageRef = storageReference.child(imageName);

        // Upload ảnh lên Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                        // Nếu upload thành công, lấy đường dẫn của ảnh
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Trả về đường dẫn của ảnh
                                onSuccessListener.onSuccess(uri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Nếu có lỗi xảy ra, gửi thông báo lỗi
                        onFailureListener.onFailure(e);
                    }
                });
    }
}

