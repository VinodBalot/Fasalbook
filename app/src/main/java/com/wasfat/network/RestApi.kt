package com.wasfat.network

import com.wasfat.ui.pojo.BannerResponse
import com.wasfat.ui.pojo.LoginResponse
import com.wasfat.ui.pojo.StateResponse
import io.reactivex.Observable
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
    @POST("AddUser")
    fun addUser(@Body reqData: HashMap<String, String>): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("GetCountryList")
    fun getCountryList(@Body reqData: HashMap<String, String>): Call<LoginResponse>


    @Headers("Content-Type: application/json")
    @POST("GetStateListListbyCountryId")
    fun getStateListByCountryId(@Body reqData: HashMap<String, String>): Observable<StateResponse>

    /*
  @Headers("Content-Type: application/json")
  @POST("signup")
  fun signup(@Body reqData: HashMap<String, String>): Observable<UserResponsePOJO>
*/

}