package com.letsdoitmacedonia.models;

import android.text.Html;


public class Register {

  private String msg;
  private boolean status;
  private String error;

  public boolean isStatus() {

    return status;
  }

  public void setStatus(boolean status) {

    this.status = status;
  }

  public String getError() {

    return error;
  }

  public void setError(String error) {

    this.error = error;
  }

  public String getMsg() {

    return msg;
  }

  public void setMsg(String msg) {

    this.msg = msg;
  }
}
