package com.wasfat.utils

import android.content.Context
import android.content.SharedPreferences

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable


import android.content.Context.MODE_PRIVATE

class SessionManager : BaseObservable() {

    private val IS_LOGIN = "isLoggedIn"
    private val AUTH_TOKEN = "auth_token"
    private val FCM_TOKEN = "fcm_token"
    private val USERNAME = "username"
    private val API_TOKEN = "api_token"
    private val IMAGE = "image"
    private val LAST_NAME = "last_name"
    private val IS_MOBILE = "is_mobile"

    private val IS_FIRST_TIME_LOGIN = "isFirstTimeLogin"

    private val USER_ID = "id"
    private val EMAIL = "email"
    private val COUNTRY_CODE = "std_code"
    private val MOBILE = "phone"
    private val FIRST_NAME = "first_name"
    private val NAME = "name"
    private val LANGUAGE = "language"

    var language: String
        get() = shared!!.getString(LANGUAGE, "en").toString()
        set(language) {
            editor!!.putString(LANGUAGE, language)
            editor!!.commit()
        }


    var cartCount: String?
        get() = shared!!.getString("count", null)

    set(count) {
        editor!!.putString("count", count)
        editor!!.commit()
    }

    var currentAddress: String?
        get() = shared!!.getString("address", "")

        set(address) {
            editor!!.putString("address", address)
            editor!!.commit()
        }


    var latitude: String?
        get() = shared!!.getString("lat", "0.0")

        set(lat) {
            editor!!.putString("lat", lat)
            editor!!.commit()
        }

    var longitude: String?
        get() = shared!!.getString("long", "0.0")

        set(long) {
            editor!!.putString("long", long)
            editor!!.commit()
        }


    var userId: String?
        get() = shared!!.getString("userId", "")

        set(userId) {
            editor!!.putString("userId", userId)
            editor!!.commit()
        }


    var deliveryAddress: String?
        get() = shared!!.getString("deliveryAddress", "")

        set(deliveryAddress) {
            editor!!.putString("deliveryAddress", deliveryAddress)
            editor!!.commit()
        }


    val isLogin: Boolean
        get() = shared!!.getBoolean(IS_LOGIN, false)

    fun setLogin(status: Boolean) {
        editor!!.putBoolean(IS_LOGIN, status)
        editor!!.commit()
    }


    val isFistTimeLogin: Boolean
        get() = shared!!.getBoolean(IS_FIRST_TIME_LOGIN, false)

    fun setFirstTimeLogin(status: Boolean) {
        editor!!.putBoolean(IS_FIRST_TIME_LOGIN, status)
        editor!!.commit()
    }


    var authToken: String?
        get() = shared!!.getString(AUTH_TOKEN, "")
        set(authToken) {
            editor!!.putString(AUTH_TOKEN, authToken)
            editor!!.commit()
        }



    var fcmToken: String?
        get() = shared!!.getString(FCM_TOKEN, "")
        set(fcmToken) {
            editor!!.putString(FCM_TOKEN, fcmToken)
            editor!!.commit()
        }



    //        notifyPropertyChanged(com.os.urservice.BR.userData);
    /*var userData: DataResponse
        @Bindable("data")
        get() {
            val userData = DataResponse()
            userData.id = shared!!.getString(USER_ID, "").toString()
            userData.first_name = shared!!.getString(FIRST_NAME, "").toString()
            userData.last_name = shared!!.getString(LAST_NAME, "").toString()
            userData.name = shared!!.getString(NAME, "").toString()
            userData.email = shared!!.getString(EMAIL, "").toString()
            userData.phone = shared!!.getString(MOBILE, "").toString()
            userData.std_code = shared!!.getString(COUNTRY_CODE, "").toString()
            return userData
        }
        @Bindable("data")
        set(userData) {
            editor!!.putString(USER_ID, userData.id)
            editor!!.putString(FIRST_NAME, userData.first_name)
            editor!!.putString(LAST_NAME, userData.last_name)
            editor!!.putString(NAME, userData.name)
            editor!!.putString(EMAIL, userData.email)
            editor!!.putString(MOBILE, userData.phone)
            editor!!.putString(COUNTRY_CODE, userData.std_code)
            editor!!.commit()
        }
*/

    fun logout() {
        editor!!.putString(USER_ID, "")
        editor!!.putString(FIRST_NAME, "")
        editor!!.putString(LAST_NAME, "")
        editor!!.putString(USERNAME, "")
        editor!!.putString(EMAIL, "")
        editor!!.putString(MOBILE, "")
        editor!!.putString(COUNTRY_CODE, "")
        editor!!.putString(API_TOKEN, "")
        editor!!.putBoolean(IS_LOGIN, false)
        editor!!.putString(AUTH_TOKEN,"")
        editor!!.clear()
        editor!!.commit()
    }

    companion object {

        private var shared: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        private var session: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            if (session == null) {
                session = SessionManager()
            }
            if (shared == null) {
                shared = context.getSharedPreferences("URServiceApp", MODE_PRIVATE)
                editor = shared!!.edit()
            }
            return session as SessionManager
        }
    }
}