package com.letsdoitmacedonia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.interfaces.RetrofitInterface;
import com.letsdoitmacedonia.models.LogIn;
import com.letsdoitmacedonia.preferences.PrefsLogIn;
import com.letsdoitmacedonia.preferences.PrefsWelcome;
import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.utils.Utils;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class LoginActivity extends Activity {

  @InjectView(R.id.username)
  EditText username;
  @InjectView(R.id.password)
  EditText password;
  @InjectView(R.id.sign_in_button)
  Button signInButton;
  @InjectView(R.id.register_button)
  Button newUserButton;
  @InjectView(R.id.log_out)
  Button logOutButton;
  @InjectView(R.id.login_progress)
  ProgressBar progressBar;
  @InjectView(R.id.login_form)
  ScrollView scrollView;

  private String sUsername;
  private String sPassword;

  @OnClick(R.id.register_button)
  public void startRegisterActivity(Button button) {

    //start register activity
    Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
    startActivity(registerActivity);
  }

  @OnClick(R.id.sign_in_button)
  public void signIn(Button button) {

    sUsername = String.valueOf(username.getText());
    sPassword = String.valueOf(password.getText());

    if (Utils.getSingleton().isOnline(this)) {
      if (Utils.isPassValid(password, this)) {
        requestData(Constants.FULL_LOG_IN);
      }
    }
    else {
      Toast.makeText(LoginActivity.this,
              R.string.no_internet,
              Toast.LENGTH_LONG).show();
    }
  }

  @OnClick(R.id.log_out)
  public void logOut(Button button) {

    PrefsLogIn.with(this).removeLogInKey();

    Utils.ON_REG_LOG = false;

    Toast.makeText(LoginActivity.this,
            R.string.successful_log_out,
            Toast.LENGTH_LONG).show();

    Intent backToMain = new Intent(LoginActivity.this, MainActivity.class);
    backToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    backToMain.putExtra("EXIT", true);
    startActivity(backToMain);
    finish();

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.inject(this);

    progressBar.setVisibility(View.GONE);

    username.setTypeface(Utils.getSingleton().setCustomFont(this));
    password.setTypeface(Utils.getSingleton().setCustomFont(this));
    signInButton.setTypeface(Utils.getSingleton().setCustomFont(this));
    newUserButton.setTypeface(Utils.getSingleton().setCustomFont(this));

    if (PrefsLogIn.with(this).checkLogIn()) {
      logOutButton.setVisibility(View.VISIBLE);
      username.setVisibility(View.GONE);
      password.setVisibility(View.GONE);
      signInButton.setVisibility(View.GONE);
      newUserButton.setVisibility(View.GONE);
    }
    else {
      logOutButton.setVisibility(View.GONE);
      username.setVisibility(View.VISIBLE);
      password.setVisibility(View.VISIBLE);
      signInButton.setVisibility(View.VISIBLE);
      newUserButton.setVisibility(View.VISIBLE);
    }

  }

  private void requestData(String uri) {

    progressBar.setVisibility(View.VISIBLE);
    scrollView.setVisibility(View.GONE);

    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);

    RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(Constants.ENDOPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(okHttpClient))
            .build();

    RetrofitInterface api = adapter.create(RetrofitInterface.class);

    api.logIn(sUsername, sPassword, new Callback<LogIn>() {

      @Override
      public void success(LogIn logIn, Response response) {

        progressBar.setVisibility(View.GONE);
        //save api key in preferences
        PrefsLogIn.with(LoginActivity.this).setLogInKey(logIn.getKey());

        Utils.ON_REG_LOG = true;

        Toast.makeText(LoginActivity.this,
                R.string.successful_log_in,
                Toast.LENGTH_LONG).show();

        //start main activity
        Intent startMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startMainActivity.putExtra("EXIT", true);
        startActivity(startMainActivity);
        finish();

      }

      @Override
      public void failure(RetrofitError error) {

        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        Toast.makeText(LoginActivity.this,
                R.string.error_try_again,
                Toast.LENGTH_LONG).show();
        error.printStackTrace();
      }
    });
  }
}