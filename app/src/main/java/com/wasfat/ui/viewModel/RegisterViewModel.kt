package com.wasfat.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wasfat.network.ApiResponse
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.pojo.StateResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RegisterViewModel : ViewModel() {

    var responseStateByCountryIdData = MutableLiveData<ApiResponse<StateResponse>>()
    var apiStateByCountryIdResponse: ApiResponse<StateResponse>? = null


    var restApi: RestApi? = null
    var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiStateByCountryIdResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun getStateByCountryId(reqData: HashMap<String, String>) {
        subscription = restApi!!.getStateListByCountryId(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseStateByCountryIdData.postValue(apiStateByCountryIdResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseStateByCountryIdData.postValue(
                        apiStateByCountryIdResponse!!.success(
                            result
                        )
                    )
                },
                { throwable ->
                    responseStateByCountryIdData.postValue(
                        apiStateByCountryIdResponse!!.error(
                            throwable
                        )
                    )
                }
            )
    }


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}