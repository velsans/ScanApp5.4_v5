package com.zebra.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zebra.main.fragments.FellingRegistrationFragments;
import com.zebra.main.fragments.InventoryCountFragment;
import com.zebra.main.fragments.InventoryReceivedFragments;
import com.zebra.main.fragments.InventoryTransferFragments;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FellingRegistrationFragments tab1 = new FellingRegistrationFragments();
                return tab1;
            case 1:
                InventoryCountFragment tab2 = new InventoryCountFragment();
                return tab2;
            case 2:
                InventoryTransferFragments tab3 = new InventoryTransferFragments();
                return tab3;
            case 3:
                InventoryReceivedFragments tab4 = new InventoryReceivedFragments();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}