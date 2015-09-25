package com.letsdoitmacedonia.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.fragments.WelcomeFragment;


// we use fragment state pager adapter to make the view pager act like a horizontal list view
// fragments that are not visible may be destroyed to conserve memory
public class WelcomePagerAdapter extends FragmentStatePagerAdapter {

  public WelcomePagerAdapter(FragmentManager fm) {

    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    // all items are the same, the difference is the image and text they display
    return WelcomeFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    // the number of pages should represent the number of slides have
    return Constants.WELCOME_PAGES;
  }
}
