package com.diev.salarymaster.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.diev.salarymaster.Adapter.SpinnerBusinessAdapter;
import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Model.Business;
import com.diev.salarymaster.Model.TimeWork;
import com.diev.salarymaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Activity_Detail_TimeWork extends AppCompatActivity {
    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    private String userId;
    Spinner sp_business, sp_month, sp_year;
    private SpinnerBusinessAdapter businessAdapter;
    ArrayList<Business> businesses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_time_work);
        setControl();
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        setEvent(userId);
    }

    private void setControl() {
        sp_business = findViewById(R.id.sp_detail_business);
        sp_month = findViewById(R.id.sp_detail_month);
        sp_year = findViewById(R.id.sp_detail_year);

        businessAdapter = new SpinnerBusinessAdapter(this, businesses);
        sp_business.setAdapter(businessAdapter);
    }

    private void setEvent(String userId) {
        setDataDate();
        getCompanies(userId);
    }

    private void setDataDate() {
        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng được đánh số từ 0

        // Danh sách các tháng và năm
        List<String> monthList = new ArrayList<>();
        List<String> yearList = new ArrayList<>();

        // Tạo danh sách tháng từ tháng 1 đến tháng 12
        for (int month = 1; month <= 12; month++) {
            String monthString = String.format(Locale.US, "%02d", month); // Format tháng
            monthList.add(monthString);
        }

        // Tạo danh sách năm từ năm 2023 đến năm hiện tại
        for (int year = 2023; year <= currentYear; year++) {
            yearList.add(String.valueOf(year));
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_month.setAdapter(monthAdapter);

        // Chọn tháng hiện tại
        sp_month.setSelection(currentMonth - 1);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_year.setAdapter(yearAdapter);

        // Chọn năm hiện tại
        sp_year.setSelection(yearList.indexOf(String.valueOf(currentYear)));
    }

    private void getCompanies(String userId) {
        sp_business.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Business").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                businesses.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Business business = dataSnapshot.getValue(Business.class);
                    if (business != null) {
                        businesses.add(business);
                    }
                }
                businessAdapter.notifyDataSetChanged(); // Thông báo cho adapter rằng dữ liệu đã thay đổi
                sp_business.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}