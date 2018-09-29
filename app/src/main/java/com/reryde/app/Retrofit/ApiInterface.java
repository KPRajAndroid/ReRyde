package com.reryde.app.Retrofit;

import com.reryde.app.Helper.URLHelper;
import com.reryde.app.Models.MatrixModel;
import com.reryde.app.Models.NotificatonModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by CSS on 8/4/2017.
 */

public interface ApiInterface {

    @GET("distancematrix/json")
    Call<MatrixModel> getDistanceAndDuration(@Query("units") String units, @Query("origins") String origins, @Query("destinations") String destinations);

    //synchronous.
    @GET("json?")
    Call<ResponseBody> getResponse(@Query("latlng") String param1, @Query("key") String param2);

    @POST(URLHelper.EXTEND_TRIP)
    @FormUrlEncoded
    Call<ResponseBody> extendTrip(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                  @Field("request_id") String request_id, @Field("latitude") String latitude, @Field("longitude") String longitude, @Field("address") String address);

    @GET(URLHelper.SAVE_LOCATION)
    Call<ResponseBody> getFavoriteLocations(@Header("X-Requested-With") String xmlRequest,
                                            @Header("Authorization") String strToken);

    @POST(URLHelper.SAVE_LOCATION)
    @FormUrlEncoded
    Call<ResponseBody> updateFavoriteLocations(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                               @Field("type") String type, @Field("latitude") String latitude, @Field("longitude") String longitude, @Field("address") String address);

    @DELETE(URLHelper.SAVE_LOCATION+"/"+"{id}")
    Call<ResponseBody> deleteFavoriteLocations(@Path("id") String id, @Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                               @Query("type") String type, @Query("latitude") String latitude, @Query("longitude") String longitude, @Query("address") String address);

    @POST(URLHelper.access_login)
    @FormUrlEncoded
    Call<ResponseBody> accessAPI(@Header("X-Requested-With") String xmlRequest, @Field("username") String username, @Field("accesskey") String accesskey);

    @FormUrlEncoded
    @POST("api/user/share")
    Call<ResponseBody> sendReferal(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String Authorization, @FieldMap() Map<String, String> partMap, @Field("type") String user, @Field("device_type") String android);


    @GET("/api/user/promocode/list")
    Call<List<NotificatonModel>> getNotification(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String Authorization);

}
