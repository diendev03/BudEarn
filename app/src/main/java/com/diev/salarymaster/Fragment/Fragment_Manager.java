package com.diev.salarymaster.Fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.diev.salarymaster.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Fragment_Manager extends AppCompatActivity {

    ViewPager2 vp_bottomNavigation;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setControl();
        Initialization();
    }

    @SuppressLint("ResourceAsColor")
    private void Initialization() {
        if (vp_bottomNavigation != null) {
            vp_bottomNavigation.setUserInputEnabled(false);
            Menu_Adapter menu_adapter = new Menu_Adapter(this);
            vp_bottomNavigation.setAdapter(menu_adapter);
            vp_bottomNavigation.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (bottomNavigation != null) {
                        bottomNavigation.getMenu();
                        bottomNavigation.getMenu().getItem(position).setChecked(true);
                    }
                }
            });
        }
        if (bottomNavigation != null) {
            // Thiết lập màu nền của bottom navigation bar là màu trắng
            bottomNavigation.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

            // Định nghĩa màu cho icon khi được chọn và khi không được chọn
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_checked }, // Trạng thái được chọn
                    new int[] { -android.R.attr.state_checked } // Trạng thái không được chọn
            };
            int[] colors = new int[] {
                    ContextCompat.getColor(this, R.color.primary), // Màu cho trạng thái được chọn
                    ContextCompat.getColor(this, android.R.color.darker_gray) // Màu xám cho trạng thái không được chọn
            };
            ColorStateList iconTintList = new ColorStateList(states, colors);

            // Thiết lập màu cho icon và văn bản của các mục bottom navigation
            bottomNavigation.setItemIconTintList(iconTintList); // Đặt màu cho icon

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (vp_bottomNavigation != null) {
                    if (itemId == R.id.nav_home) {
                        vp_bottomNavigation.setCurrentItem(0);
                        overridePendingTransition(0, 0);
                    } else if (itemId == R.id.nav_statictis) {
                        vp_bottomNavigation.setCurrentItem(1);
                        overridePendingTransition(0, 0);
                    } else if (itemId == R.id.nav_profile) {
                        vp_bottomNavigation.setCurrentItem(2);
                        overridePendingTransition(0, 0);
                    }
                }

                return true;
            });
        }
    }

    private void setControl() {
        vp_bottomNavigation = findViewById(R.id.vp_bottomNavigation);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

}
