package com.diev.salarymaster.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diev.salarymaster.Model.User;
import com.diev.salarymaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        // Add event handling code here
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

    private void setData(User user) {
        if (isAdded()) { // Ensure fragment is attached to activity
            tv_name.setText(user.getFullname());
            tv_phone.setText(user.getPhone());
            tv_email.setText(user.getEmail());
            tv_birthday.setText(user.getBirthday());
            // Optionally set other user data like avatar and wage if needed
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
}
