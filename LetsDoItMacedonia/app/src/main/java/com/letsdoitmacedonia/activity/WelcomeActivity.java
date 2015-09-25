package com.letsdoitmacedonia.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.transformation.ParallaxPageTransformer;
import com.letsdoitmacedonia.transformation.ParallaxTransformInformation;
import com.letsdoitmacedonia.preferences.PrefsWelcome;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.adapters.WelcomePagerAdapter;
import com.viewpagerindicator.PageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WelcomeActivity extends AppCompatActivity {

  @InjectView(R.id.pagerWelcome)
  ViewPager pager;
  @InjectView(R.id.indicatorWelcome)
  PageIndicator indicator;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    //set portrait only for phone, normal for tablet
    if (getResources().getBoolean(R.bool.portrait_only)) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    if (PrefsWelcome.with(this).isWelcomeShown()) {
      // the welcome screen is not shown
      // introduce the user to this  app
      startActivity(new Intent(this, MainActivity.class));
      finish();
      // we must return here
      // finish is an async call and code execution will continue until done
      return;
    }

    setContentView(R.layout.activity_welcome);
    ButterKnife.inject(this);

    // set the adapter and preload all fragments
    pager.setAdapter(new WelcomePagerAdapter(getSupportFragmentManager()));
    pager.setOffscreenPageLimit(Constants.WELCOME_PAGES);
    indicator.setViewPager(pager);

    // setup the view pager
    // use 1f, 1f to achieve static pager transition
    ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
            .addViewToParallax(new ParallaxTransformInformation(R.id.image_background, -2f, -2f));
    pager.setPageTransformer(true, pageTransformer);
  }
}