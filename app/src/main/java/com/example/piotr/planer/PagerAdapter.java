package com.example.piotr.planer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumbOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumbOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                EventListFragment eventListFragment = new EventListFragment();
                return eventListFragment;
            case 1:
                CalendarFragment calendarFragment = new CalendarFragment();
                return calendarFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }
}
