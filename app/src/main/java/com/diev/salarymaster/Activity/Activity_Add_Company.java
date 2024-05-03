package com.diev.salarymaster.Activity;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.diev.salarymaster.ConfirmationAlert;
import com.diev.salarymaster.InformationAlert;
import com.diev.salarymaster.ImagePicker;
import com.diev.salarymaster.ImageUploader;
import com.diev.salarymaster.Model.Company;
import com.diev.salarymaster.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Activity_Add_Company extends AppCompatActivity {
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
        setContentView(R.layout.activity_add_company);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        setControl();
        setEvent();
        // Khởi tạo ImageUploader
        imageUploader = new ImageUploader("Company");
    }

    private void setControl() {
        ib_back = findViewById(R.id.ib_company_back);
        ib_save = findViewById(R.id.ib_company_new);
        iv_avatar = findViewById(R.id.iv_add_company_pic);
        edt_name = findViewById(R.id.edt_add_company_name);
        edt_salary = findViewById(R.id.edt_add_company_salary);
        viewBlocking=findViewById(R.id.viewBlocking_add_company);
        progressBar=findViewById(R.id.progressBar_add_company);
    }

    private void setEvent() {
        ib_back.setOnClickListener(view -> finish());
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở hộp thoại để chọn hoặc chụp ảnh
                ImagePicker.showImagePicker(Activity_Add_Company.this, new ImagePicker.ImagePickerListener() {
                    @Override
                    public void onImageChosen(Uri chosenImageUri) {
                        // Xử lý khi đã chọn ảnh thành công
                        image = chosenImageUri;
                        iv_avatar.setImageURI(image);
                    }
                });
            }
        });
        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image != null) {
                    viewBlocking.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    // Nếu đã chọn ảnh, tiến hành tải lên và tạo đối tượng Company
                    imageUploader.uploadImage(image,
                            new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Xử lý khi tải lên ảnh thành công
                                    String imageUrl = uri.toString(); // Lấy đường dẫn của ảnh
                                    // Tạo đối tượng Company với đường dẫn ảnh imageUrl
                                    Company company = CreateCompany(imageUrl);
                                    // Tiếp tục xử lý với đối tượng Company
                                    CreateCompanyOnFirebase(company);
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    viewBlocking.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    // Xử lý khi tải lên ảnh thất bại
                                    InformationAlert dialogFragment = new InformationAlert("Lỗi! Vui lòng thử lại sau.");
                                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                                }
                            });
                } else {
                    // Nếu chưa chọn ảnh, thông báo cho người dùng
                    InformationAlert dialogFragment = new InformationAlert("Vui lòng chọn ảnh");
                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                }
            }
        });
    }

    private void CreateCompanyOnFirebase(Company company){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference companyRef = database.getReference("Company").child(userId); // Đường dẫn đến nút "Company/userId/"
        DatabaseReference newCompanyRef = companyRef.push(); // Tạo một khóa mới cho công ty trong nút "Company/userId/"
        String companyId = newCompanyRef.getKey(); // Lấy khóa mới tạo
        if (companyId != null) {
            company.setId(companyId); // Đặt id của công ty là khóa mới tạo
            newCompanyRef.setValue(company)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Xử lý khi lưu thông tin công ty thành công

                            viewBlocking.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            Refesh();
                            String message="Thêm thành công\nBạn có muốn trở lại màn hình trước đó không?";
                            // Tạo một phiên bản mới của ConfirmationAlert và truyền vào thông tin cần thiết
                            ConfirmationAlert confirmationAlert = new ConfirmationAlert(message, "Không", "Có",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });

                            // Hiển thị Dialog bằng cách sử dụng FragmentManager
                            confirmationAlert.show(getSupportFragmentManager(), "confirmation_alert_dialog");
                        }
                        private void Refesh() {
                            edt_name.setText("");
                            edt_salary.setText("");
                            iv_avatar.setImageResource(R.drawable.default_pic);
                            image=null;
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi có lỗi xảy ra
                        viewBlocking.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        InformationAlert dialogFragment = new InformationAlert("Lỗi! Vui lòng thử lại sau.");
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                    });
        } else {
            viewBlocking.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            InformationAlert dialogFragment = new InformationAlert("Vui lòng kiểm tra kết nối.");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
        }
    }

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


    private Company CreateCompany(String imageUrl) {
        String today = setCurrentDate();
        Company company = new Company();
        company.setName(edt_name.getText().toString());
        company.setSalary(edt_salary.getText().toString());
        company.setDateStart(today);
        company.setAvatar(imageUrl);
        company.setUuid(userId);
        return company;
    }

    private String setCurrentDate() {
        // Lấy ngày tháng năm hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0 nên cần cộng thêm 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%02d/%02d/%04d", day, month, year);
    }
}
