package com.bogdan801.schedule.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bogdan801.schedule.fragments.LessonsScheduleFragment;
import com.bogdan801.schedule.fragments.OpenFileFragment;
import com.bogdan801.schedule.fragments.TimeScheduleFragment;

public class MainViewPageAdapter extends FragmentStateAdapter {
    private LessonsScheduleFragment lsFragment = new LessonsScheduleFragment();
    private TimeScheduleFragment tsFragment = new TimeScheduleFragment();


    public MainViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return lsFragment;
            case 1:
                return tsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public Fragment getFragment(int position){
        switch (position){
            case 0:
                return lsFragment;
            case 1:
                return tsFragment;
            default:
                return null;
        }
    }
}
