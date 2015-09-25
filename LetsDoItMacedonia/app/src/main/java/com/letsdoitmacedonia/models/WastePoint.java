package com.letsdoitmacedonia.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class WastePoint implements ClusterItem {

  private final LatLng mPosition;
  private String id;
  private String lat;
  private String lng;
  private String des;
  private String vol;
  private String img;
  private String comp_name;
  private String color;
  private String name;


  public WastePoint(double lat, double lng) {

    mPosition = new LatLng(lat, lng);

  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public String getLat() {

    return lat;
  }

  public void setLat(String lat) {

    this.lat = lat;
  }

  public String getLng() {

    return lng;
  }

  public void setLng(String lng) {

    this.lng = lng;
  }

  public String getDes() {

    return des;
  }

  public void setDes(String des) {

    this.des = des;
  }

  public String getVol() {

    return vol;
  }

  public void setVol(String vol) {

    this.vol = vol;
  }

  public String getImg() {

    return img;
  }

  public void setImg(String img) {

    this.img = img;
  }

  public String getComp_name() {

    return comp_name;
  }

  public void setComp_name(String comp_name) {

    this.comp_name = comp_name;
  }

  public String getColor() {

    return color;
  }

  public void setColor(String color) {

    this.color = color;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  @Override
  public LatLng getPosition() {

    return mPosition;
  }
}
