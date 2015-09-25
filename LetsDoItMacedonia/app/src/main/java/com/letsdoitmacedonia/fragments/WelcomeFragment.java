package com.letsdoitmacedonia.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.preferences.PrefsWelcome;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.activity.MainActivity;
import com.letsdoitmacedonia.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WelcomeFragment extends Fragment {

  @InjectView(R.id.image_background)
  ImageView imageBackground;
  @InjectView(R.id.button_tutorial)
  TextView buttonTutorial;
  @InjectView(R.id.text_tutorial_first)
  TextView textTutorialFirst;
  @InjectView(R.id.text_tutorial_second)
  TextView textTutorialSecond;
  @InjectView(R.id.text_tutorial_third)
  TextView textTutorialThird;
  @InjectView(R.id.text_tutorial_fourth)
  TextView textTutorialFourth;
  @InjectView(R.id.text_tutorial_welcome_en)
  TextView textTutorialWelcomeEn;
  @InjectView(R.id.text_tutorial_welcome_mk)
  TextView textTutorialWelcomeMk;
  @InjectView(R.id.text_tutorial_welcome_sh)
  TextView textTutorialWelcomeSh;


  private int pageNumber;
  private Typeface typeFaceCustomFont;

  public static WelcomeFragment newInstance(int page) {

    WelcomeFragment pageFragment = new WelcomeFragment();
    Bundle arguments = new Bundle();
    arguments.putInt(Constants.EXTRA_PAGE_NUMBER, page);
    pageFragment.setArguments(arguments);
    return pageFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    pageNumber = getArguments().getInt(Constants.EXTRA_PAGE_NUMBER);

  }

  @Override
  public void onDestroy() {

    super.onDestroy();
    ButterKnife.reset(this);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_welcome, container, false);
    ButterKnife.inject(this, view);

    //set custom font
    Utils customFont = Utils.getSingleton();
    this.typeFaceCustomFont = customFont.setCustomFont(getActivity());

    textTutorialWelcomeEn.setTypeface(this.typeFaceCustomFont);
    textTutorialWelcomeMk.setTypeface(this.typeFaceCustomFont);
    textTutorialWelcomeSh.setTypeface(this.typeFaceCustomFont);
    textTutorialFirst.setTypeface(this.typeFaceCustomFont);
    textTutorialSecond.setTypeface(this.typeFaceCustomFont);
    textTutorialThird.setTypeface(this.typeFaceCustomFont);
    textTutorialFourth.setTypeface(this.typeFaceCustomFont);
    buttonTutorial.setTypeface(this.typeFaceCustomFont);

    textTutorialWelcomeEn.setVisibility(View.GONE);
    textTutorialWelcomeMk.setVisibility(View.GONE);
    textTutorialWelcomeSh.setVisibility(View.GONE);
    textTutorialFirst.setVisibility(View.GONE);
    textTutorialSecond.setVisibility(View.GONE);
    textTutorialThird.setVisibility(View.GONE);
    textTutorialFourth.setVisibility(View.GONE);
    buttonTutorial.setVisibility(View.GONE);

    buttonTutorial.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        PrefsWelcome.with(getActivity()).setWelcomeShown();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
      }
    });

    switch (pageNumber) {
      case 0:
        imageBackground.setImageResource(R.drawable.ic_british);
        textTutorialWelcomeEn.setVisibility(View.VISIBLE);
        textTutorialWelcomeMk.setVisibility(View.VISIBLE);
        textTutorialWelcomeSh.setVisibility(View.VISIBLE);
        break;
      case 1:
        imageBackground.setImageResource(R.drawable.ic_accessibility);
        textTutorialFirst.setVisibility(View.VISIBLE);
        break;
      case 2:
        imageBackground.setImageResource(R.drawable.ic_camera);
        textTutorialSecond.setVisibility(View.VISIBLE);
        break;
      case 3:
        imageBackground.setImageResource(R.drawable.ic_delete);
        textTutorialThird.setVisibility(View.VISIBLE);
        break;
      case 4:
        imageBackground.setImageResource(R.drawable.ic_nature);
        buttonTutorial.setVisibility(View.VISIBLE);
        textTutorialFourth.setVisibility(View.VISIBLE);
        break;
      default:
        break;
    }

    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {

    super.setUserVisibleHint(isVisibleToUser);

    if (!isVisibleToUser || pageNumber != Constants.WELCOME_PAGES - 1) {
      // the code block below should execute only after the last
      // welcome screen is visible  to the user
      return;
    }

  }
}
