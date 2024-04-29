package com.diev.salarymaster.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class Menu_Adapter extends FragmentStateAdapter {


    public Menu_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Fragment_Home();
            case 1:
                return new Fragment_Statictis();
            case 2:
                return new Fragment_Profile();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

