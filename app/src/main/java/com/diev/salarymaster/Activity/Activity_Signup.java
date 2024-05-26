package com.diev.salarymaster.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Model.User;
import com.diev.salarymaster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Activity_Signup extends AppCompatActivity {
    TextView tv_birthday;
    ImageButton btn_back;
    Button btn_signup;
    EditText edt_email, edt_password, edt_fullname, edt_phone;
    View viewBlocking;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setControl();
        setEvent();
    }

    private void setControl() {
        btn_back = findViewById(R.id.btn_signup_back);
        tv_birthday = findViewById(R.id.tv_signup_birthday);
        edt_email = findViewById(R.id.edt_signup_email);
        edt_password = findViewById(R.id.edt_signup_password);
        edt_fullname = findViewById(R.id.edt_signup_fullname);
        edt_phone = findViewById(R.id.edt_signup_phone);
        btn_signup = findViewById(R.id.btn_signup_completed);
        progressBar = findViewById(R.id.progressBar_signup);
        viewBlocking = findViewById(R.id.viewBlocking_signup);
    }

    private void setEvent() {
        tv_birthday.setOnClickListener(view -> showDatePickerDialog());
        btn_back.setOnClickListener(view -> finish());
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_email.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                if (ValidateData(email, password)) {
                    RegistrationOnFirebase(email, password);
                }
            }
        });
    }

    private void RegistrationOnFirebase(String email, String password) {
        viewBlocking.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                viewBlocking.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    String uuid = firebaseAuth.getCurrentUser().getUid();
                    if (createAccount(newUser(uuid, email))) {
                        InformationAlert dialogFragment = new InformationAlert("Tạo tài khoản thành công");
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                        Refesh();
                    }
                } else {
                    InformationAlert dialogFragment = new InformationAlert("Lỗi kết nối!");
                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
                }
            }
        });
    }

    private void Refesh() {
        edt_email.setText("");
        edt_fullname.setText("");
        edt_password.setText("");
        edt_phone.setText("");
        tv_birthday.setText("");
    }

    private User newUser(String uuid, String email) {
        String phone = edt_phone.getText().toString().trim();
        String fullname = edt_fullname.getText().toString().trim();
        String birthday = tv_birthday.getText().toString().trim();
        return new User(uuid, email, fullname, birthday, phone,"");
    }

    // Đăng kí tài khoản mới
    private boolean createAccount(User user) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
            databaseReference.child(user.getUuid()).setValue(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean ValidateData(String email, String password) {
        // Kiểm tra không chuỗi nào được rỗng
        if (email.isEmpty() || password.isEmpty()) {
            InformationAlert dialogFragment = new InformationAlert("Vui lòng điền đầy đủ thông tin!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            return false;
        }

        // Kiểm tra định dạng email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        if (!email.matches(emailPattern)) {
            InformationAlert dialogFragment = new InformationAlert("Vui lòng kiểm tra lại email chính xác!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            return false;
        }

        // Kiểm tra password có ít nhất 6 kí tự và ít nhất 1 số
        if (password.length() < 6 || !password.matches(".*\\d.*")) {
            InformationAlert dialogFragment = new InformationAlert("Mật khẩu phải có ít nhất 6 kí tự và chứa ít nhất 1 số!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        String phone = edt_phone.getText().toString().trim();
        String phonePattern = "(\\+84|0)\\d{9,10}";
        if (!phone.matches(phonePattern)) {
            InformationAlert dialogFragment = new InformationAlert("Vui lòng kiểm tra lại số điện thoại");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog_fragment");
            return false;
        }

        return true;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_Signup.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Tạo đối tượng SimpleDateFormat để định dạng ngày tháng năm
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                // Tạo đối tượng Calendar để đặt ngày tháng năm được chọn
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

                // Lấy chuỗi ngày tháng năm đã định dạng
                String formattedDate = sdf.format(selectedDate.getTime());

                // Gán ngày tháng năm đã định dạng vào TextView
                tv_birthday.setText(formattedDate);
            }
        }, year, month, day);

        // Hiển thị dialog
        datePickerDialog.show();
    }
}
