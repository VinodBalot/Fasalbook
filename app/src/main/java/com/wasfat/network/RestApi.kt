package com.wasfat.network

import com.google.gson.JsonObject
import com.wasfat.ui.pojo.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.*


interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("GetAllBanners")
    fun getAllBanner(@Body reqData: HashMap<String, String>): Call<BannerResponse>


    @Headers("Content-Type: application/json")
    @POST("GetCountryList")
    fun getCountryList(@Body reqData: HashMap<String, String>): Call<LoginResponse>


    @Headers("Content-Type: application/json")
    @POST("state/list")
    fun getStateListByCountryId(@Body jsonData: JsonObject): Call<StateResponsePOJO>


    @Headers("Content-Type: application/json")
    @POST("city/list")
    fun getCityListByStateId(@Body jsonData: JsonObject): Call<CityResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("block/list")
    fun getBlockListByStateId(@Body jsonData: JsonObject): Call<BlockResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("AddUser")
    fun registerUser(@Body jsonData: JsonObject): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("users/validateuser")
    fun loginUser(@Body jsonData: JsonObject): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("users/changepassword")
    fun changePassword(@Body jsonData: JsonObject): Call<ChangePasswordResponse>


    @Headers("Content-Type: application/json")
    @POST("category/homecategory")
    fun getCategories(@Body jsonData: JsonObject): Call<CategoryResponsePOJO>

    /*
  @Headers("Content-Type: application/json")
  @POST("signup")
  fun signup(@Body reqData: HashMap<String, String>): Observable<UserResponsePOJO>
*/

}
