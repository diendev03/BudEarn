package com.diev.salarymaster.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diev.salarymaster.Activity.Activity_Login;
import com.diev.salarymaster.Model.TimeWork;
import com.diev.salarymaster.Model.User;
import com.diev.salarymaster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Profile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_Profile newInstance(String param1, String param2) {
        Fragment_Profile fragment = new Fragment_Profile();
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

    public static final String SHARED_PRE = "shared_pre";
    public static final String uuid = "uuid";
    private String userId;
    ImageView iv_avatar;
    TextView tv_name, tv_email, tv_phone, tv_birthday, tv_wage;
    EditText edt_password, edt_newpassword;
    Button btn_change, btn_logout;
    View viewBlocking;
    ProgressBar progressBar, progressBar_avatar;
    ArrayList<TimeWork> dataTimeWork=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setControl(view);
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        userId = sharedPreferences.getString(uuid, "");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID không tồn tại!", Toast.LENGTH_SHORT).show();
        } else {
            getInformation(userId);
        }
        setEvent();
        return view;
    }
    private void setControl(View view) {
        iv_avatar = view.findViewById(R.id.iv_profile_avatar);
        tv_name = view.findViewById(R.id.tv_profile_name);
        tv_email = view.findViewById(R.id.tv_profile_email);
        tv_phone = view.findViewById(R.id.tv_profile_phone);
        tv_birthday = view.findViewById(R.id.tv_profile_birthday);
        tv_wage = view.findViewById(R.id.tv_profile_wage);
        edt_password = view.findViewById(R.id.edt_profile_password);
        edt_newpassword = view.findViewById(R.id.edt_profile_new_password);
        btn_change = view.findViewById(R.id.btn_profile_change_password);
        btn_logout = view.findViewById(R.id.btn_profile_logout);
        viewBlocking=view.findViewById(R.id.viewBlocking_profile);
        progressBar=view.findViewById(R.id.progressBar_profile);
        progressBar_avatar=view.findViewById(R.id.progressBar_profile_avatar);
    }

    private void setEvent() {
        getDataTimework();
        btn_change.setOnClickListener(v -> changePassword());
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                // Kết thúc hoạt động và quay về màn hình đăng nhập
                requireActivity().finish();
                startActivity(new Intent(requireContext(), Activity_Login.class));
            }
        });
    }

    private void getInformation(String userId) {
        viewBlocking.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    setData(user);
                }
                viewBlocking.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                viewBlocking.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void clearData(String userId) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PRE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(userId);
        editor.apply();
    }



    private void setData(User user) {
        if (isAdded()) {
            tv_name.setText(user.getFullname());
            tv_phone.setText(user.getPhone());
            tv_email.setText(user.getEmail());
            tv_birthday.setText(user.getBirthday());
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String imageUrl = user.getAvatar();
                progressBar_avatar.setVisibility(View.VISIBLE);
                Picasso.get().load(imageUrl).into(iv_avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar_avatar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar_avatar.setVisibility(View.GONE);
                        iv_avatar.setImageResource(R.drawable.account);
                    }
                });
            }
        }
    }

    private void getDataTimework() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TimeWork").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataTimeWork.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TimeWork timeWork = dataSnapshot.getValue(TimeWork.class);
                    if (timeWork != null) {
                        dataTimeWork.add(timeWork);
                    }
                }
                tv_wage.setText(calculateAverageMonthlyWage(dataTimeWork)+"  VNđ/tháng");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    private static int calculateAverageMonthlyWage(ArrayList<TimeWork> timeWorks) {
        Map<String, Double> totalWages = new HashMap<>();
        Map<String, Integer> months = new HashMap<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (TimeWork timeWork : timeWorks) {
            try {
                Date date = sdf.parse(timeWork.getDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                String monthKey = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1);

                double totalWageOfMonth = timeWork.getTotal() * timeWork.getWage();
                totalWages.put(monthKey, totalWages.getOrDefault(monthKey, 0.0) + totalWageOfMonth);
                months.put(monthKey, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        double totalWage = 0;
        for (double wage : totalWages.values()) {
            totalWage += wage;
        }

        int totalMonths = months.size();

        return (int) (totalWage / totalMonths);
    }

    private void changePassword() {
        // Lấy thông tin mật khẩu từ EditTexts
        String currentPassword = edt_password.getText().toString().trim();
        String newPassword = edt_newpassword.getText().toString().trim();
        String confirmPassword = edt_newpassword.getText().toString().trim();

        // Kiểm tra xem các trường đã được nhập đầy đủ chưa
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu mới và mật khẩu xác nhận có trùng khớp không
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Mật khẩu mới và mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy người dùng hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Xác thực mật khẩu hiện tại
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Xác thực thành công, tiếp tục thay đổi mật khẩu
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Thay đổi mật khẩu thành công
                                                Toast.makeText(requireContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                                            } else {
                                                // Thất bại khi thay đổi mật khẩu
                                                Toast.makeText(requireContext(), "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Xác thực thất bại
                            Toast.makeText(requireContext(), "Xác thực mật khẩu hiện tại thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}