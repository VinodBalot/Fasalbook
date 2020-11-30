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
    fun getHomeCategories(@Body jsonData: JsonObject): Call<CategoryResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("category/GetCategoryByParentId")
    fun getCategoriesByParentId(@Body jsonData: JsonObject): Call<CategoryResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("category/shareurideascategory")
    fun shareIdeaCategory(@Body jsonData: JsonObject): Call<CategoryResponsePOJO>


    @Headers("Content-Type: application/json")
    @POST("events/categorylist")
    fun getEventCategories(@Body jsonData: JsonObject): Call<EventCategoryResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("events/list")
    fun getEventsById(@Body jsonData: JsonObject): Call<EventResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("govtschemes/list")
    fun getGovtSchemesByStateId(@Body jsonData: JsonObject): Call<GovtSchemesResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("products/addedit")
    fun addSellItem(@Body jsonData: JsonObject): Call<AddSellItemResponse>

    @Headers("Content-Type: application/json")
    @POST("products/unitlist")
    fun getProductUnitList(): Call<UnitListResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("products/useragriproducts")
    fun getUserProducts(@Body jsonData: JsonObject): Call<UserProductsResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("products/delete")
    fun deleteUserItem(@Body jsonData: JsonObject): Call<DeleteItemResponse>

    @Headers("Content-Type: application/json")
    @POST("products/search")
    fun getProductsFromSearch(@Body jsonData: JsonObject): Call<UserProductsResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("farmtourism/add")
    fun addFarmTourismItem(@Body jsonData: JsonObject): Call<AddFarmItemResponse>

    @Headers("Content-Type: application/json")
    @POST("cms/about")
    fun getAboutApp(@Body jsonData: JsonObject): Call<AboutAppResponse>


    /*
  @Headers("Content-Type: application/json")
  @POST("signup")
  fun signup(@Body reqData: HashMap<String, String>): Observable<UserResponsePOJO>
*/

}
