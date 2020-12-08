package com.wasfat.ui.activity.buyAndSell.agricultureAndAlliedServices

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityAddAlliedServicesBinding
import com.wasfat.databinding.ActivityAddLandscapeBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.buyAndSell.landscapeAndGardening.AddLandscapeActivity
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AddFarmItemResponse
import com.wasfat.ui.pojo.BuySellType
import com.wasfat.ui.pojo.Category
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAlliedServicesActivity : BaseBindingActivity() {

    private lateinit var parentCategoryId: String
    var binding: ActivityAddAlliedServicesBinding? = null
    var onClickListener: View.OnClickListener? = null

    companion object {

        fun startActivity(
            activity: Activity,
            categoryId: Int,
            isClear: Boolean
        ) {
            val intent = Intent(activity, AddAlliedServicesActivity::class.java)
            intent.putExtra("categoryId", categoryId)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_allied_services)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        parentCategoryId = intent.getStringExtra("categoryId").toString()

    }

    override fun initializeObject() {
        onClickListener = this

    }

    override fun setListeners() {
        binding!!.imvClose.setOnClickListener(onClickListener)
        binding!!.btnAddLandscape.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvClose -> {
                finish()
            }
            R.id.btnAddLandscape -> {
                if (isValidFormData(
                        binding!!.edtProductName.text.toString(),
                        binding!!.edtSpecification.text.toString(),
                        binding!!.edtServices.text.toString()
                    )
                ) {
                    addLandscapeItemThroughAPI(
                        binding!!.edtProductName.text.toString(),
                        binding!!.edtSpecification.text.toString(),
                        binding!!.edtServices.text.toString()
                    )
                }
            }

        }

    }

    private fun isValidFormData(
        name: String,
        specification: String,
        servicesOffered : String
    ): Boolean {

        if (TextUtils.isEmpty(name)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_full_name))
            return false
        }

        if (TextUtils.isEmpty(specification)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_unit_value))
            return false
        }

        if (TextUtils.isEmpty(servicesOffered)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_unit_value))
            return false
        }

        return true
    }

    private fun addLandscapeItemThroughAPI(
        name: String,
        specification: String,
        servicesOffered : String
    ) {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("ProductId", 0)
        rootObject.addProperty("ProductName", name)
        rootObject.addProperty("Specification", specification )
        rootObject.addProperty("ServiceOffered", servicesOffered )
        rootObject.addProperty("CategoryId", parentCategoryId)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Published", binding!!.cbPublished.isChecked)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddFarmItemResponse> = apiService1.addEditServiceItem(gsonObject)
        call1.enqueue(object : Callback<AddFarmItemResponse?> {
            override fun onResponse(
                call: Call<AddFarmItemResponse?>,
                response: Response<AddFarmItemResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Response == "success") {
                            UtilityMethod.showToastMessageSuccess(
                                mActivity!!,
                                getString(R.string.label_sell_item_add_successful)
                            )
                            finish()
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_sell_item_add_unsuccessful)
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<AddFarmItemResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


}