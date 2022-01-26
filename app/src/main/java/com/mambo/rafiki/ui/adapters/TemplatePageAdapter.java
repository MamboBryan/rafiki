package com.mambo.rafiki.ui.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TemplatePageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String[] pageNames;

    public TemplatePageAdapter(@NonNull FragmentManager fm, List<Fragment> fragments,
                               String[] pageNames) {
        super(fm);

        this.fragments = fragments;
        this.pageNames = pageNames;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageNames[position];
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
