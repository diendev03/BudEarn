package com.diev.salarymaster.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.diev.salarymaster.Custom.ConfirmationAlert;
import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Custom.ImagePicker;
import com.diev.salarymaster.Custom.ImageUploader;
import com.diev.salarymaster.Model.Business;
import com.diev.salarymaster.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Activity_Add_Business extends AppCompatActivity {
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    String userId;
    private ImageUploader imageUploader;
    private Uri image;
    ImageButton ib_back, ib_save;
    ImageView iv_avatar;
    EditText edt_name, edt_salary;
    View viewBlocking;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        // Gán các control trong layout cho biến
        setControl();
        // Thiết lập các sự kiện
        setEvent();
        // Khởi tạo ImageUploader
        imageUploader = new ImageUploader("Business");
    }

    // Gán các control trong layout cho biến
    private void setControl() {
        ib_back = findViewById(R.id.ib_business_back);
        ib_save = findViewById(R.id.ib_business_new);
        iv_avatar = findViewById(R.id.iv_add_business_pic);
        edt_name = findViewById(R.id.edt_add_business_name);
        edt_salary = findViewById(R.id.edt_add_business_salary);
        viewBlocking=findViewById(R.id.viewBlocking_add_business);
        progressBar=findViewById(R.id.progressBar_add_business);
    }

    // Thiết lập các sự kiện
    private void setEvent() {
        // Sự kiện khi nhấn nút back
        ib_back.setOnClickListener(view -> finish());
        // Sự kiện khi nhấn chọn ảnh đại diện
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở hộp thoại để chọn hoặc chụp ảnh
                ImagePicker.showImagePicker(Activity_Add_Business.this, new ImagePicker.ImagePickerListener() {
                    @Override
                    public void onImageChosen(Uri chosenImageUri) {
                        // Xử lý khi đã chọn ảnh thành công
                        image = chosenImageUri;
                        iv_avatar.setImageURI(image);
                    }
                });
            }
        });
        // Sự kiện khi nhấn nút lưu
        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra dữ liệu trước khi lưu
                if (validate()) {
                    // Hiển thị viewBlocking và progressBar
                    viewBlocking.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    // Nếu đã chọn ảnh, tiến hành tải lên và tạo đối tượng Business
                    imageUploader.uploadImage(image,
                            new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Xử lý khi tải lên ảnh thành công
                                    String imageUrl = uri.toString(); // Lấy đường dẫn của ảnh
                                    // Tạo đối tượng Business với đường dẫn ảnh imageUrl
                                    Business business = CreateBusiness(imageUrl);
                                    // Tiếp tục xử lý với đối tượng Business
                                    CreateBusinessOnFirebase(business);
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ẩn viewBlocking và progressBar
                                    viewBlocking.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    // Xử lý khi tải lên ảnh thất bại
                                    InformationAlert dialogFragment = new InformationAlert("Lỗi! Vui lòng thử lại sau.");
                                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                                }
                            });
                }
            }
        });
    }

    // Hàm kiểm tra dữ liệu trước khi lưu
    private  boolean validate(){
        if (edt_name.getText().toString().trim().isEmpty() || edt_salary.getText().toString().trim().isEmpty()){
            InformationAlert dialogFragment = new InformationAlert("Ít nhất bạn phải cung cấp tên và lương theo giờ của nơi làm việc!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            return false;
        }
        if (image==null){
            InformationAlert dialogFragment = new InformationAlert("Vui lòng thêm 1 ảnh bất kì!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            return false;
        }
        return true;
    }

    // Hàm tạo đối tượng Business và lưu vào Firebase
    private void CreateBusinessOnFirebase(Business business){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference businessRef = database.getReference("Business").child(userId); // Đường dẫn đến nút "Business/userId/"
        DatabaseReference newBusinessRef = businessRef.push(); // Tạo một khóa mới cho công ty trong nút "Business/userId/"
        String businessId = newBusinessRef.getKey(); // Lấy khóa mới tạo
        if (businessId != null) {
            business.setId(businessId); // Đặt id của công ty là khóa mới tạo
            newBusinessRef.setValue(business)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Xử lý khi lưu thông tin công ty thành công
                            // Ẩn viewBlocking và progressBar
                            viewBlocking.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            // Refresh các trường dữ liệu
                            Refesh();
                            // Hiển thị thông báo và xác nhận quay lại màn hình trước
                            String message="Thêm thành công\nBạn có muốn trở lại màn hình trước đó không?";
                            ConfirmationAlert confirmationAlert = new ConfirmationAlert(message, "Không", "Có",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            confirmationAlert.show(getSupportFragmentManager(), "confirmation_alert_dialog");
                        }
                        private void Refesh() {
                            edt_name.setText("");
                            edt_salary.setText("");
                            iv_avatar.setImageResource(R.drawable.business);
                            image=null;
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Ẩn viewBlocking và progressBar
                        viewBlocking.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        // Xử lý khi có lỗi xảy ra
                        InformationAlert dialogFragment = new InformationAlert("Lỗi! Vui lòng thử lại sau.");
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                    });
        } else {
            // Ẩn viewBlocking và progressBar
            viewBlocking.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            InformationAlert dialogFragment = new InformationAlert("Vui lòng kiểm tra kết nối.");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
        }
    }

    // Xử lý kết quả khi chọn hoặc chụp ảnh từ ImagePicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.handleActivityResult(requestCode, resultCode, data, this, new ImagePicker.ImagePickerListener() {
            @Override
            public void onImageChosen(Uri chosenImageUri) {
                // Xử lý khi đã chọn hoặc chụp ảnh thành công
                image = chosenImageUri;
                iv_avatar.setImageURI(image);
                Log.e("pickerrrrrr", image.toString());
            }
        });
    }

    // Hàm tạo đối tượng Business từ dữ liệu nhập vào
    private Business CreateBusiness(String imageUrl) {
        String today = setCurrentDate();
        Business business = new Business();
        business.setName(edt_name.getText().toString());
        business.setSalary(edt_salary.getText().toString());
        business.setDateStart(today);
        business.setAvatar(imageUrl);
        business.setUuid(userId);
        return business;
    }

    // Hàm lấy ngày hiện tại và định dạng thành chuỗi
    @SuppressLint("DefaultLocale")
    private String setCurrentDate() {
        // Lấy ngày tháng năm hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0 nên cần cộng thêm 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%02d/%02d/%04d", day, month, year);
    }
}
