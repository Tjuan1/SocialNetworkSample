package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Return a NEW fragment instance in createFragment(int)
                return new ProfileTab();
            case 1:
                return new UsersTab();
            case 2:
                return new SharedPictureTab();
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
