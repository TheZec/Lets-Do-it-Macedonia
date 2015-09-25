package com.letsdoitmacedonia.interfaces;

import com.letsdoitmacedonia.models.AddWastePoint;
import com.letsdoitmacedonia.models.LogIn;
import com.letsdoitmacedonia.models.NewsList;
import com.letsdoitmacedonia.models.Register;
import com.letsdoitmacedonia.models.WastePoint;
import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.utils.Utils;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;


public interface RetrofitInterface {

  //register
  @FormUrlEncoded
  @POST(Constants.REGISTER)
  void register(
          @Field("email") String email, @Field("username") String username,
          @Field("name") String name, @Field("surname") String surname,
          @Field("password") String password,
          Callback<Register> response);

  //login
  @FormUrlEncoded
  @POST(Constants.LOG_IN)
  void logIn(
          @Field("username") String username, @Field("password") String password,
          Callback<LogIn> response);

  //get all waste points en
  @FormUrlEncoded
  @POST(Constants.WASTEPOINT_EN)
  void wastePointsEn(
          @Field("laf") String laf, @Field("lat") String lat,
          @Field("lnf") String lnf, @Field("lnt") String lnt,
          Callback<List<WastePoint>> response);

  //get all waste points mk
  @FormUrlEncoded
  @POST(Constants.WASTEPOINT_EN)
  void wastePointsMk(
          @Field("laf") String laf, @Field("lat") String lat,
          @Field("lnf") String lnf, @Field("lnt") String lnt,
          Callback<List<WastePoint>> response);

  //add new waste point mk
  @Multipart
  @POST(Constants.NEW_POINT_MK)
  void newWastePoint(
          @Part("api_key") String apiKey,
          @Part("img") TypedFile file,
          @Part("lat") String latitude,
          @Part("lng") String longitude,
          @Part("desc") String description,
          @Part("comp") int composition,
          @Part("vol") String volume,
          //@PartMap Map<String, Object> params,
          Callback<AddWastePoint> callback);

  //add new waste point en
  @Multipart
  @POST(Constants.NEW_POINT_EN)
  void newWastePointEn(
          @Part("api_key") String apiKey,
          @Part("img") TypedFile file,
          @Part("lat") String latitude,
          @Part("lng") String longitude,
          @Part("desc") String description,
          @Part("comp") int composition,
          @Part("vol") String volume,
          //@PartMap Map<String, Object> params,
          Callback<AddWastePoint> callback);

  //get news mk
  @FormUrlEncoded
  @POST(Constants.NEWS_MK)
  void newsMk(
          @Field("category") String category,
          Callback<NewsList> response);

  //get news en
  @FormUrlEncoded
  @POST(Constants.NEWS_EN)
  void newsEn(
          @Field("category") String category,
          Callback<NewsList> response);

}
