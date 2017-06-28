package com.star.tinker;

import android.support.v4.app.Fragment;

import com.aspsine.fragmentnavigator.FragmentNavigatorAdapter;

/**
 * Created by aspsine on 16/3/31.
 */
public class FragmentAdapter implements FragmentNavigatorAdapter {

    private static final String TABS[] = {"Chats", "Contacts", "Circle", "Me"};

    @Override
    public Fragment onCreateFragment(int position) {
        return MainFragment.newInstance(TABS[position]);
    }

    @Override
    public String getTag(int position) {
        return MainFragment.TAG + TABS[position];
    }

    @Override
    public int getCount() {
        return TABS.length;
    }
}
