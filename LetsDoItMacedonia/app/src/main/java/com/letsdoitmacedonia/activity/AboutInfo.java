package com.letsdoitmacedonia.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AboutInfo extends AppCompatActivity {

  @InjectView(R.id.about_info)
  TextView aboutInfo;
  @InjectView(R.id.toolbar_addItem)
  Toolbar toolbar;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about_info);
    ButterKnife.inject(this);

    // use toolbar as action bar
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        onBackPressed();
      }
    });
  }
}
