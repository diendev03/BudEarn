package com.diev.salarymaster.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.diev.salarymaster.Activity.Activity_Business_Management;
import com.diev.salarymaster.Adapter.SpinnerBusinessAdapter;
import com.diev.salarymaster.Custom.InformationAlert;
import com.diev.salarymaster.Model.Business;
import com.diev.salarymaster.Model.TimeWork;
import com.diev.salarymaster.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Home newInstance(String param1, String param2) {
        Fragment_Home fragment = new Fragment_Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static String SHARED_PRE = "shared_pre";
    public static String uuid = "uuid";
    private String userId;
    private Business selectedBusiness;
    private Button btn_businessList, btn_addWorkTime;
    private TextView tvDate, tvTimeStart, tvTimeFinish;
    private Spinner sp_business;
    private ProgressBar progressBar_spinner;


    ArrayList<Business> companies = new ArrayList<>();
    private SpinnerBusinessAdapter businessAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setControl(view);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PRE, MODE_PRIVATE); // Thay đổi để sử dụng requireContext()
        userId = sharedPreferences.getString(uuid, "");

        setEvent();
        setCurrentDate();
        getCompanies(userId);
        return view;
    }

    private void setControl(View view) {
        tvDate = view.findViewById(R.id.tv_home_date);
        tvTimeStart = view.findViewById(R.id.tv_home_timestart);
        tvTimeFinish = view.findViewById(R.id.tv_home_timefinish);
        btn_businessList =view.findViewById(R.id.btn_home_workList);
        btn_addWorkTime = view.findViewById(R.id.btn_home_addWorkTime);
        sp_business = view.findViewById(R.id.sp_home_business);
        progressBar_spinner = view.findViewById(R.id.progressBar_home_spinner);
        // Khởi tạo adapter cho Spinner
        businessAdapter = new SpinnerBusinessAdapter(requireContext(), companies); // Sử dụng requireContext()
        sp_business.setAdapter(businessAdapter); // Thiết lập adapter cho Spinner
    }

    private void setEvent() {
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        tvTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvTimeStart);
            }
        });

        tvTimeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvTimeFinish);
            }
        });
        btn_businessList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireContext(), Activity_Business_Management.class);
                startActivity(intent);
            }
        });
        btn_addWorkTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimeWork(addTimeWork(userId));
            }
        });

        sp_business.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBusiness = (Business) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có gì được chọn
            }
        });

    }
    private void createTimeWork(TimeWork timeWork){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference tw=database.getReference("TimeWork").child(userId);
        DatabaseReference newTimeWork=tw.push();
        String timeWorkId=newTimeWork.getKey();
        if (timeWorkId!=null){
            timeWork.setId(timeWorkId);
            newTimeWork.setValue(timeWork).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    InformationAlert alert = new InformationAlert("Thêm giờ làm thành công!");
                    alert.show(getParentFragmentManager(), "custom_dialog_fragment"); // Sử dụng getParentFragmentManager()
                }
            });
        }
    }
    private TimeWork addTimeWork(String userId) {
        String date, start, finish;
        date = tvDate.getText().toString();
        start = tvTimeStart.getText().toString();
        finish = tvTimeFinish.getText().toString();
        Double wage=Double.parseDouble(selectedBusiness.getSalary());

        TimeWork timeWork = new TimeWork();
        timeWork.setUuid(userId);
        timeWork.setBusiness(selectedBusiness.getId());
        timeWork.setDate(date);
        timeWork.setStart(start);
        timeWork.setFinish(finish);
        timeWork.setWage(wage);

        // Chuyển đổi chuỗi thời gian thành giây
        long startTimeInSeconds = timeStringToSeconds(start);
        long finishTimeInSeconds = timeStringToSeconds(finish);

        // Tính toán tổng số giây
        long totalTimeInSeconds = finishTimeInSeconds - startTimeInSeconds;

        // Chuyển đổi tổng số giây thành giờ với một chữ số thập phân
        double totalTimeInHours = (double) totalTimeInSeconds / 3600.0;

        // Làm tròn tổng số giờ xuống một chữ số thập phân
        double roundedTotalTime = Math.floor(totalTimeInHours * 10) / 10;

        // Gán tổng số giờ đã làm tròn cho total
        timeWork.setTotal(roundedTotalTime);

        return timeWork;
    }

    // Phương thức chuyển đổi chuỗi thời gian sang giây
    private long timeStringToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 3600 + minutes * 60;
    }


    private void getCompanies(String userId) {
        sp_business.setVisibility(View.VISIBLE);
        sp_business.setVisibility(View.GONE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Business").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companies.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Business business = dataSnapshot.getValue(Business.class);
                    if (business != null) {
                        companies.add(business);
                    }
                }
                businessAdapter.notifyDataSetChanged(); // Thông báo cho adapter rằng dữ liệu đã thay đổi
                sp_business.setVisibility(View.VISIBLE);
                progressBar_spinner.setVisibility(View.GONE);
                if (companies.size() == 0) {
                    InformationAlert alert = new InformationAlert("Bạn cần thêm nơi làm việc vào danh sách!!!");
                    alert.show(getParentFragmentManager(), "custom_dialog_fragment"); // Sử dụng getParentFragmentManager()
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
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
                tvDate.setText(formattedDate);
            }
        }, year, month, day);

        // Hiển thị dialog
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Xử lý khi người dùng chọn thời gian
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                textView.setText(selectedTime);
            }
        }, hour, minute, true); // true: sử dụng đồng hồ 24 giờ

        // Hiển thị dialog
        timePickerDialog.show();
    }
    private void setCurrentDate() {
        // Lấy ngày tháng năm hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0 nên cần cộng thêm 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format ngày tháng năm thành chuỗi
        String currentDate = String.format("%02d/%02d/%04d", day, month, year);

        // Gán giá trị vào TextView date
        tvDate.setText(currentDate);
    }

}