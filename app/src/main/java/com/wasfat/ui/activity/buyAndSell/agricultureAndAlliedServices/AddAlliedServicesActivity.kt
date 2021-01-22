package com.wasfat.ui.activity.buyAndSell.agricultureAndAlliedServices

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityAddAlliedServicesBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.*
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAlliedServicesActivity : BaseBindingActivity() {

    private lateinit var parentCategoryId: String
    var binding: ActivityAddAlliedServicesBinding? = null
    var onClickListener: View.OnClickListener? = null
    var productName = ""
    val reqDataState: HashMap<String, Int> = HashMap()
    val reqDataCity: HashMap<String, Int> = HashMap()
    var stateList: ArrayList<Statelist> = ArrayList()
    var stateNameList: ArrayList<String> = ArrayList()
    var cityList: ArrayList<Citylist> = ArrayList()
    var cityNameList: ArrayList<String> = ArrayList()
    private var cityId: Int = -1
    private var stateId: Int = -1

    companion object {

        fun startActivity(
            activity: Activity,
            categoryId: Int,
            productName: String,
            isClear: Boolean
        ) {
            val intent = Intent(activity, AddAlliedServicesActivity::class.java)
            intent.putExtra("categoryId", categoryId.toString())
            intent.putExtra("productName", productName)
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
        productName = intent.getStringExtra("productName").toString()

    }

    override fun initializeObject() {
        onClickListener = this
        callGetStateListByCountryAPI()
    }


    private fun callGetStateListByCountryAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", sessionManager!!.language)
        rootObject.addProperty("countryId", Constants.COUNTRYID)
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<StateResponsePOJO> = apiService1.getStateListByCountryId(gsonObject)
        call1.enqueue(object : Callback<StateResponsePOJO?> {
            override fun onResponse(
                call: Call<StateResponsePOJO?>,
                response: Response<StateResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        stateList = response.body()!!.statelist
                        for (state in stateList) {
                            stateNameList.add(state.Name)
                            reqDataState[state.Name] = state.Id
                        }
                    }
                }
            }

            override fun onFailure(call: Call<StateResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })


    }

    override fun setListeners() {
        binding!!.imvClose.setOnClickListener(onClickListener)
        binding!!.btnAddLandscape.setOnClickListener(onClickListener)
        binding!!.edtProductName.setText(productName)
        binding!!.edtState.setOnClickListener(onClickListener)
        binding!!.edtCity.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvClose -> {
                finish()
            }
            R.id.btnAddLandscape -> {
                if (isValidFormData(
                        binding!!.edtProductName.text.toString(),
                        binding!!.edtServicesOffered.text.toString(),
                        binding!!.edtSpecification.text.toString(),
                        binding!!.edtOfficeName.text.toString(),
                        cityId,
                        stateId
                    )
                ) {
                    addLandscapeItemThroughAPI(
                        binding!!.edtProductName.text.toString(),
                        binding!!.edtServicesOffered.text.toString(),
                        binding!!.edtSpecification.text.toString(),
                        binding!!.edtOfficeName.text.toString()
                    )
                }
            }
            R.id.edtState -> {
                selectDialog(
                    getString(R.string.select_state),
                    stateNameList,
                    binding!!.edtState,
                    reqDataState
                )
            }
            R.id.edtCity -> {
                selectDialog(
                    getString(R.string.select_City),
                    cityNameList,
                    binding!!.edtCity,
                    reqDataCity
                )
            }

        }

    }

    private fun selectDialog(
        title: String,
        stateList: List<String>,
        edtState: EditText,
        reqData: HashMap<String, Int>
    ) {
        val view1: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.list_search_item,
                null,
                false
            )
        val dialog = AlertDialog.Builder(this@AddAlliedServicesActivity)
            .setTitle(title)
            .setView(view1).create()
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val edtSearch = view1.findViewById<View>(R.id.edtSearch) as EditText
        val arrayAdapter = ArrayAdapter(
            this@AddAlliedServicesActivity,
            android.R.layout.simple_list_item_1,
            stateList
        )
        lv.adapter = arrayAdapter
        lv.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                val entry = parent.adapter.getItem(position) as String
                if (title == getString(R.string.select_state)) {
                    edtState.setText(entry)
                    edtCity.setText("")
                    stateId = reqData[entry]!!
                    callGetCityListByStateAPI()
                } else if (title == getString(R.string.select_City)) {
                    edtCity.setText(entry)
                    cityId = reqData[entry]!!
                }
                dialog.cancel()
            }
        dialog.show()
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arrayAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun isValidFormData(
        name: String,
        servicesOffered: String,
        specification: String,
        office: String,
        cityId: Int,
        stateId: Int
    ): Boolean {

        if (TextUtils.isEmpty(name)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_full_name))
            return false
        }

        if (TextUtils.isEmpty(servicesOffered)) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.enter_service_offered)
            )
            return false
        }

        if (TextUtils.isEmpty(specification)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_specification))
            return false
        }

        if (TextUtils.isEmpty(office)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_office_name))
            return false
        }

        if (stateId == -1) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.select_state)
            )
            return false
        }
        if (cityId == -1) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.select_City)
            )
            return false
        }

        return true
    }

    private fun addLandscapeItemThroughAPI(
        name: String,
        servicesOffered: String,
        specification: String,
        officeName: String
    ) {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("ProductId", 0)
        rootObject.addProperty("ProductName", name)
        rootObject.addProperty("Specification", specification)
        rootObject.addProperty("ServiceOffered", servicesOffered)
        rootObject.addProperty("CategoryId", parentCategoryId)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Published", binding!!.cbPublished.isChecked)
        rootObject.addProperty("CityId", cityId)
        rootObject.addProperty("OfficeName", officeName)

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


    private fun callGetCityListByStateAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", sessionManager!!.language)
        rootObject.addProperty("stateId", stateId)
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<CityResponsePOJO> = apiService1.getCityListByStateId(gsonObject)
        call1.enqueue(object : Callback<CityResponsePOJO?> {
            override fun onResponse(
                call: Call<CityResponsePOJO?>,
                response: Response<CityResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        cityList = response.body()!!.citylist
                        for (city in cityList) {
                            cityNameList.add(city.Name)
                            reqDataCity[city.Name] = city.Id
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CityResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })


    }


}