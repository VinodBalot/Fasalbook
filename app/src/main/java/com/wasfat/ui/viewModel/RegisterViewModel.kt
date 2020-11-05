package com.wasfat.ui.viewModel

import androidx.lifecycle.ViewModel
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import io.reactivex.disposables.Disposable

class RegisterViewModel : ViewModel() {


    var restApi: RestApi? = null
    var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()

    }

    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}