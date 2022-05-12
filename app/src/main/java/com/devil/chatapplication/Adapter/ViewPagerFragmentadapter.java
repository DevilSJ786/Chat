package com.devil.chatapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.devil.chatapplication.Fragment.HomeFragment;
import com.devil.chatapplication.Fragment.StatusFragment;

public class ViewPagerFragmentadapter extends FragmentStateAdapter {

    private  String[] title = new String[]{"Home", "Status"};

    public ViewPagerFragmentadapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new StatusFragment();
        }
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return title.length;
    }
}
