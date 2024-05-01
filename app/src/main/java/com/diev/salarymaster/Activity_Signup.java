package com.diev.salarymaster;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Activity_Signup extends AppCompatActivity {
    TextView tv_birthday;
    ImageButton btn_back;
    EditText edt_email, edt_password, edt_fullname, edt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        setControl();
        setEvent();
    }

    private void setEvent() {
        String email = String.valueOf(edt_email.getText());
        String password = String.valueOf(edt_phone.getText());
        String phone = String.valueOf(edt_phone.getText());
        String fullname = String.valueOf(edt_fullname.getText());
        String birthday = String.valueOf(tv_birthday.getText());
        tv_birthday.setOnClickListener(view -> showDatePickerDialog());
        btn_back.setOnClickListener(view -> finish());
    }

    private void setControl() {
        btn_back = findViewById(R.id.btn_signup_back);
        tv_birthday = findViewById(R.id.tv_signup_birthday);
        edt_email = findViewById(R.id.edt_signup_email);
        edt_password = findViewById(R.id.edt_signup_password);
        edt_fullname = findViewById(R.id.edt_signup_fullname);
        edt_phone = findViewById(R.id.edt_signup_phone);
    }

    private boolean ValidateData(String email, String password, String fullname, String phone, String birthday) {
        // Kiểm tra không chuỗi nào được rỗng
        if (email.isEmpty() || password.isEmpty() || fullname.isEmpty() || phone.isEmpty() || birthday.isEmpty()) {
            return false;
        }

        // Kiểm tra định dạng email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        if (!email.matches(emailPattern)) {
            return false;
        }

        // Kiểm tra password có ít nhất 6 kí tự và ít nhất 1 số
        if (password.length() < 6 || !password.matches(".*\\d.*")) {
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        String phonePattern = "(\\+84|0)\\d{9,10}";
        if (!phone.matches(phonePattern)) {
            return false;
        }

        // Nếu các điều kiện trên đều đúng, trả về true
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