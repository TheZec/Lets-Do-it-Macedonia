package com.letsdoitmacedonia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.interfaces.RetrofitInterface;
import com.letsdoitmacedonia.models.Register;
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


public class RegisterActivity extends Activity {

  @InjectView(R.id.email_register)
  AutoCompleteTextView email;
  @InjectView(R.id.username_register)
  EditText username;
  @InjectView(R.id.name_register)
  EditText name;
  @InjectView(R.id.surname_register)
  EditText surname;
  @InjectView(R.id.password_register)
  EditText password;
  @InjectView(R.id.confirm_password_register)
  EditText confirmPassword;
  @InjectView(R.id.email_sign_in_button_register)
  Button sendButton;
  @InjectView(R.id.login_progress)
  ProgressBar progressBar;
  @InjectView(R.id.login_form)
  ScrollView scrollView;

  private String sEmail;
  private String sUsername;
  private String sName;
  private String sSurname;
  private String sPassword;
  private String sConfirmPassword;

  @OnClick(R.id.email_sign_in_button_register)
  public void register(Button button) {

    //set mail string and check if it is valid email address
    sEmail = String.valueOf(email.getText());
    Utils.isEmailValid(sEmail);
    if (!Utils.isEmailValid(sEmail)) {
      email.setError(getString(R.string.invalid_email));
    }

    //set strings for retrofit
    sUsername = String.valueOf(username.getText());
    sName = String.valueOf(name.getText());
    sSurname = String.valueOf(surname.getText());
    sPassword = String.valueOf(password.getText());
    sConfirmPassword = String.valueOf(confirmPassword.getText());

    //check if it is online
    if (Utils.getSingleton().isOnline(RegisterActivity.this)) {
      //check password and email if we are online
      if (Utils.isPassValid(password, this)
              && Utils.isEmailValid(sEmail)) {
        if (sPassword.equals(sConfirmPassword)) {
          //request data if all is ok
          requestData(Constants.FULL_REGISTER);
        }
        else {
          confirmPassword.setError(getString(R.string.password_not_match));
        }
      }
    }
    else {
      Toast.makeText(RegisterActivity.this,
              R.string.no_network_turn_on,
              Toast.LENGTH_LONG).show();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.inject(this);

    progressBar.setVisibility(View.GONE);

    email.setTypeface(Utils.getSingleton().setCustomFont(this));
    username.setTypeface(Utils.getSingleton().setCustomFont(this));
    name.setTypeface(Utils.getSingleton().setCustomFont(this));
    surname.setTypeface(Utils.getSingleton().setCustomFont(this));
    password.setTypeface(Utils.getSingleton().setCustomFont(this));
    confirmPassword.setTypeface(Utils.getSingleton().setCustomFont(this));
    sendButton.setTypeface(Utils.getSingleton().setCustomFont(this));

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


    api.register(sEmail, sUsername, sName, sSurname, sPassword, new Callback<Register>() {


      @Override
      public void success(Register register, Response response) {

        try {
          if (register.getError() == null && register.getMsg().equals("New user created.<br>Please login to continue")) {
            Toast.makeText(RegisterActivity.this,
                    R.string.successful_new_user,
                    Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

              @Override
              public void run() {
                progressBar.setVisibility(View.GONE);
                Intent logInActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(logInActivity);
                finish();
              }
            }, 1000);
          }
          else if (register.getError().equals("Username already exists")) {
            username.setError(getString(R.string.username_exist));
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
          }
          else if (register.getError().equals("Please enter valid e-mail address")) {
            email.setError(getString(R.string.invalid_email));
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
          }
          else if (register.getError().equals("E-mail address already in use")) {
            email.setError(getString(R.string.email_exist));
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void failure(RetrofitError error) {

        error.printStackTrace();
      }
    });
  }
}

