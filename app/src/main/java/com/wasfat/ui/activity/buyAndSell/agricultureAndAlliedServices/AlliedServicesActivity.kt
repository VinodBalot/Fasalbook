package com.wasfat.ui.activity.buyAndSell.agricultureAndAlliedServices

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityAlliedServicesBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.activity.buyAndSell.landscapeAndGardening.AddLandscapeActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.ServiceRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlliedServicesActivity : BaseBindingActivity() {

    var binding: ActivityAlliedServicesBinding? = null
    var onClickListener: View.OnClickListener? = null

    var serviceList: ArrayList<UserServiceProduct> = ArrayList()
    lateinit var serviceRVAdapter: ServiceRVAdapter

    lateinit var parentCategory: Category
    lateinit var type: BuySellType

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            type: BuySellType,
            isClear: Boolean
        ) {
            val intent = Intent(activity, AlliedServicesActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_allied_services)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        type = intent.getSerializableExtra("type") as BuySellType

        //Getting parent category from parent
        parentCategory = (intent.getSerializableExtra("category") as? Category)!!
    }

    override fun initializeObject() {
        onClickListener = this
        binding!!.textTitle.text = parentCategory.CategoryName

        if (type == BuySellType.BUY)
            binding!!.fabAdd.visibility = View.GONE
        else
            setupFabButton()

        setAdapter()
    }

    private fun setupFabButton() {
        binding!!.fabAdd.visibility = View.VISIBLE
        binding!!.fabAdd.setOnClickListener {
            AddAlliedServicesActivity.startActivity(mActivity!!, parentCategory.PKID, false)
        }
    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(this)
        binding!!.rvUserFarms.layoutManager = layoutManager1
        binding!!.rvUserFarms.setHasFixedSize(true)

        fetchUserLandscapeFromAPI()

    }

    private fun serviceItemClicked(service: UserServiceProduct) {

        Log.d("TAG", "farmItemClicked: " + service)

        if (type == BuySellType.SELL)
            showItemSelectionDialog(service)
        else
            DetailsAlliedServicesActivity.startActivity(
                mActivity!!,
                service,
                false
            )
    }

    private fun showItemSelectionDialog(service: UserServiceProduct) {

        val options = arrayOf<CharSequence>("Edit Item", "Delete Item", "Details", "Cancel")
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("Landscape Item Menu")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Edit Item") {

                EditAlliedServicesActivity.startActivity(
                    mActivity!!,
                    parentCategory.PKID.toString(),
                    service,
                    false
                )

            } else if (options[item] == "Delete Item") {

                deleteSelectedItem(service)

            } else if (options[item] == "Details") {

                DetailsAlliedServicesActivity.startActivity(
                    mActivity!!,
                    service,
                    false
                )

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun deleteSelectedItem(service: UserServiceProduct) {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("ProductId", service.ProductId)
        rootObject.addProperty("UserId", sessionManager!!.userId)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<DeleteFarmResponse> = apiService1.deleteServiceItem(gsonObject)
        call1.enqueue(object : Callback<DeleteFarmResponse?> {
            override fun onResponse(
                call: Call<DeleteFarmResponse?>,
                response: Response<DeleteFarmResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Response == "success") {
                            UtilityMethod.showToastMessageSuccess(
                                mActivity!!,
                                getString(R.string.label_item_delete_successful)
                            )
                            finish()
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_item_delete_unsuccessful)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<DeleteFarmResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

    private fun fetchUserLandscapeFromAPI() {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserId", sessionManager!!.userId)
        // rootObject.addProperty("LanguageId", "1")

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<UserServicesResponsePOJO> = apiService1.getServiceItems(gsonObject)
        call1.enqueue(object : Callback<UserServicesResponsePOJO?> {
            override fun onResponse(
                call: Call<UserServicesResponsePOJO?>,
                response: Response<UserServicesResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {

                    if (response.isSuccessful) {

                        serviceList = response.body()!!.serviceList

                        Log.d("ITEMS", "onResponse: " + serviceList)

                        serviceRVAdapter = ServiceRVAdapter(
                            mActivity!!,
                            { service -> serviceItemClicked(service) },
                            serviceList
                        )

                        binding!!.rvUserFarms.adapter = serviceRVAdapter

                        if (serviceList.size == 0) {
                            binding!!.rvUserFarms.visibility = View.GONE
                            binding!!.txtNoProductFound.visibility = View.VISIBLE
                        } else {
                            binding!!.rvUserFarms.visibility = View.VISIBLE
                            binding!!.txtNoProductFound.visibility = View.GONE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserServicesResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
                Log.d("ITEMS", "onResponse: " + t.localizedMessage)
            }
        })
    }

    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btnSubmit.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.btnSubmit ->{
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}