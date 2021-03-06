package com.wasfat.network

import com.google.gson.JsonObject
import com.wasfat.ui.pojo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
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
    @POST("ideas/listbyuserid")
    fun knowledgeCenter(@Body jsonData: JsonObject): Call<UserIdeasResponsePOJO>


    @Headers("Content-Type: application/json")
    @POST("ideas/add")
    fun ideasAdd(@Body jsonData: JsonObject): Call<AddWriteUpIdeaResponse>

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
    @POST("farmtourism/userfarms")
    fun getUserFarms(@Body jsonData: JsonObject): Call<UserFarmsResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("farmtourism/edit")
    fun editUserFarms(@Body jsonData: JsonObject): Call<AddFarmItemResponse>

    @Headers("Content-Type: application/json")
    @POST("farmtourism/addimages")
    fun addUserFarmImage(@Body jsonData: JsonObject): Call<AddFarmItemResponse>

    @Headers("Content-Type: application/json")
    @POST("farmtourism/deletefarmimage")
    fun deleteUserFarmImage(@Body jsonData: JsonObject): Call<AddFarmItemResponse>

    @Headers("Content-Type: application/json")
    @POST("farmtourism/deletefarm")
    fun deleteUserFarm(@Body jsonData: JsonObject): Call<DeleteFarmResponse>

    @Headers("Content-Type: application/json")
    @POST("cms/about")
    fun getAboutApp(@Body jsonData: JsonObject): Call<AboutAppResponse>

    @Headers("Content-Type: application/json")
    @POST("users/getuserprofile")
    fun getUserInfo(@Body jsonData: JsonObject): Call<GetUserProfilePOJO>

    @Headers("Content-Type: application/json")
    @POST("cms/privacypolicy")
    fun getPrivacyPolicy(@Body jsonData: JsonObject): Call<AboutAppResponse>


    @Headers("Content-Type: application/json")
    @POST("products/addeditlg")
    fun addEditLandscapeItem(@Body jsonData: JsonObject): Call<AddFarmItemResponse>

    @Headers("Content-Type: application/json")
    @POST("products/userlglisting")
    fun getLandscapeItems(@Body jsonData: JsonObject): Call<UserLandscapeProductsResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("products/deletelg")
    fun deleteLandscapeItem(@Body jsonData: JsonObject): Call<DeleteFarmResponse>

    @Headers("Content-Type: application/json")
    @POST("products/addeditservice")
    fun addEditServiceItem(@Body jsonData: JsonObject): Call<AddFarmItemResponse>

    @Headers("Content-Type: application/json")
    @POST("products/userservicelisting")
    fun getServiceItems(@Body jsonData: JsonObject): Call<UserServicesResponsePOJO>

    @Headers("Content-Type: application/json")
    @POST("products/deleteService")
    fun deleteServiceItem(@Body jsonData: JsonObject): Call<DeleteFarmResponse>


    @Headers("Content-Type: application/json")
    @POST("users/newusersms")
    fun sendOtpToThisNumber(@Body jsonData: JsonObject): Call<OTPVerificationResponse>

    @Headers("Content-Type: application/json")
    @POST("users/forgetpasswordsms")
    fun sendOtpForForgetPassword(@Body jsonData: JsonObject): Call<OTPVerificationResponse>

    @Headers("Content-Type: application/json")
    @POST("users/forgetpassword")
    fun updateAccountPassword(@Body jsonData: JsonObject): Call<OTPVerificationResponse>

    @Headers("Content-Type: application/json")
    @POST("users/feedback")
    fun feedback(@Body jsonData: JsonObject): Call<AddWriteUpIdeaResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST("media/addIdea")
    fun shareIdea(
        @Part("userid") userid: RequestBody,
        @Part("categoryid") categoryid: RequestBody,
        @Part("ideatype") ideatype: RequestBody,
        @Part("title") title: RequestBody,
        @Part("details") details: RequestBody,
        @Part("published") published: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<AddWriteUpIdeaResponse>

}
