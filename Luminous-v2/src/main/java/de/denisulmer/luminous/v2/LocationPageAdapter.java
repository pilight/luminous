package de.denisulmer.luminous.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

public class LocationPageAdapter extends FragmentPagerAdapter
{
    private List<Fragment> mFragments;

    public LocationPageAdapter(FragmentManager fm, List<Fragment> fragments)
    {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position)
    {
        return this.mFragments.get(position % mFragments.size());
    }

    @Override
    public int getCount()
    {
        return this.mFragments.size();
    }

    @Override
    public CharSequence getPageTitle (int position)
    {
        return ((LocationFragment) this.mFragments.get(position % mFragments.size())).getTitle();
    }
}