package com.letsdoitmacedonia.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.letsdoitmacedonia.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class Partners extends AppCompatActivity {

  @InjectView(R.id.toolbar_addItem)
  Toolbar toolbar;

  @OnClick(R.id.logo_british)
  public void british(ImageView imageView){

    try{
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gov.uk/government/world/organisations/british-embassy-skopje"));
      startActivity(browserIntent);

    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @OnClick(R.id.pakomak)
  public void pakomak(ImageView imageView){
    try{
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mpm.com.mk/pocetna.nspx"));
      startActivity(browserIntent);

    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_partners);
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
