package com.zebra.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.zebra.main.fragments.FellingRegistrationFragments;
import com.zebra.main.fragments.InventoryCountFragment;
import com.zebra.main.fragments.InventoryReceivedFragments;
import com.zebra.main.fragments.InventoryTransferFragments;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new FellingRegistrationFragments();
            case 1:
                return new InventoryCountFragment();
            case 2:
                return new InventoryTransferFragments();
            case 3:
                return new InventoryReceivedFragments();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
