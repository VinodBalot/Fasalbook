package com.wasfat.ui.home.viewModel

import androidx.lifecycle.ViewModel
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import io.reactivex.disposables.Disposable

class CartViewModel : ViewModel() {

    /* var responseData = MutableLiveData<ApiResponse<CartResponse>>()
     var apiResponse: ApiResponse<CartResponse>? = null*/


    var restApi: RestApi? = null
    var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        // apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    /*  fun getCartDetails(reqData: HashMap<String, String>) {
          subscription = restApi!!.getCart(reqData)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .doOnSubscribe {
                  responseData.postValue(apiResponse!!.loading())
              }
              .subscribe(
                  { result ->
                      responseData.postValue(apiResponse!!.success(result))
                  },
                  { throwable ->
                      responseData.postValue(apiResponse!!.error(throwable))
                  }
              )
      }*/


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}