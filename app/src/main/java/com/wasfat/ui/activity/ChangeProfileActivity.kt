package com.wasfat.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
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
import com.wasfat.databinding.ActivityChangeProfileBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.authentication.LoginActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.*
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChangeProfileActivity : BaseBindingActivity() {

    var binding: ActivityChangeProfileBinding? = null
    var onClickListener: View.OnClickListener? = null
    var stateList: ArrayList<Statelist> = ArrayList()
    var stateNameList: ArrayList<String> = ArrayList()
    var cityList: ArrayList<Citylist> = ArrayList()
    var cityNameList: ArrayList<String> = ArrayList()
    var blockList: ArrayList<Blocklist> = ArrayList()
    var blockNameList: ArrayList<String> = ArrayList()
    val reqDataState: HashMap<String, Int> = HashMap()
    val reqDataCity: HashMap<String, Int> = HashMap()
    val reqDataBlock: HashMap<String, Int> = HashMap()
    private var gender: Int = -1
    private var cityId: Int = -1
    private var stateId: Int = -1
    private var blockId: Int = -1

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ChangeProfileActivity::class.java)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_profile)
        // viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        getUserProfile()
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

    private fun getUserProfile() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("UserId", sessionManager!!.userId)
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getClient()!!.create(RestApi::class.java)
        val call1: Call<GetUserProfilePOJO> = apiService1.getUserInfo(gsonObject)
        call1.enqueue(object : Callback<GetUserProfilePOJO?> {
            override fun onResponse(
                call: Call<GetUserProfilePOJO?>,
                response: Response<GetUserProfilePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        setData(response.body()!!)
                    } else {
                        UtilityMethod.showToastMessageError(
                            mActivity!!,
                            getString(R.string.label_user_already_registerd)
                        )
                    }

                }
            }

            override fun onFailure(call: Call<GetUserProfilePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

    fun setData(body: GetUserProfilePOJO) {
        binding!!.edtName.setText(body.FullName)
        binding!!.edtDOB.setText(body.DOB)
        binding!!.edtAddress.setText(body.Address)
        binding!!.edtVillage.setText(body.Village)
        binding!!.edtMobile.setText(body.Mobile)
        binding!!.edtEmail.setText(body.Email)
        binding!!.edtEmail.setText(body.Office)
        binding!!.edtPincode.setText(body.PostCode)
        binding!!.edtNameOFCompany.setText(body.Office)
        if (body.Sex == "Male") {
            binding!!.rbMale.isSelected = true
        } else {
            binding!!.rbFemale.isSelected = true
        }
    }

    override fun setListeners() {
        binding!!.btnEditProfile.setOnClickListener(onClickListener)
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.edtDOB.setOnClickListener(onClickListener)
        binding!!.edtState.setOnClickListener(onClickListener)
        binding!!.edtCity.setOnClickListener(onClickListener)
        binding!!.edtBlock.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.btnEditProfile -> {
                if (isValidFormData(
                        binding!!.edtName.text.toString(),
                        binding!!.edtMobile.text.toString()
                    )
                ) {
                    callEditProfileAPI()
                }
            }
            R.id.edtDOB -> {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dialog = DatePickerDialog(
                    mActivity!!,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val month = monthOfYear + 1
                        val date = "$dayOfMonth/$month/$year"
                        binding!!.edtDOB.setText(date)
                    },
                    year,
                    month,
                    day
                )
                dialog.datePicker.maxDate = System.currentTimeMillis();
                dialog.show()

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
            R.id.edtBlock -> {
                selectDialog(
                    getString(R.string.select_block),
                    blockNameList,
                    binding!!.edtBlock,
                    reqDataBlock
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
        val dialog = AlertDialog.Builder(this@ChangeProfileActivity)
            .setTitle(title)
            .setView(view1).create()
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val edtSearch = view1.findViewById<View>(R.id.edtSearch) as EditText
        val arrayAdapter = ArrayAdapter(
            this@ChangeProfileActivity,
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
                    edtBlock.setText("")
                    stateId = reqData[entry]!!
                    callGetCityListByStateAPI()
                } else if (title == getString(R.string.select_City)) {
                    edtCity.setText(entry)
                    edtBlock.setText("")
                    cityId = reqData[entry]!!
                    callGetBlockListByStateAPI()
                } else {
                    edtBlock.setText(entry)
                    blockId = reqData[entry]!!
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

    private fun callEditProfileAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("FullName", binding!!.edtName.text.toString())
        rootObject.addProperty("Village", binding!!.edtVillage.text.toString())
        rootObject.addProperty("Address", binding!!.edtAddress.text.toString())
        rootObject.addProperty("Sex", gender)
        rootObject.addProperty("BlockId", "")
        rootObject.addProperty("PostCode", binding!!.edtPincode.text.toString())
        rootObject.addProperty("MobilePhone", binding!!.edtMobile.text.toString())
        rootObject.addProperty("EmailId", binding!!.edtEmail.text.toString())
        rootObject.addProperty("OfficeName", binding!!.edtNameOFCompany.text.toString())
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getClient()!!.create(RestApi::class.java)
        val call1: Call<RegisterResponse> = apiService1.registerUser(gsonObject)
        call1.enqueue(object : Callback<RegisterResponse?> {
            override fun onResponse(
                call: Call<RegisterResponse?>,
                response: Response<RegisterResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Message == "Success") {
                            UtilityMethod.showToastMessageSuccess(
                                mActivity!!,
                                getString(R.string.label_user_registerd)
                            )
                            val intent = Intent(mActivity!!, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        } else {

                            Log.d("RegisterActivity", "onResponse: " + response.message())
                            Log.d("RegisterActivity", "onResponse: " + response.body())
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_user_already_registerd)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


    private fun isValidFormData(
        name: String,
        mobile: String
    ): Boolean {

        if (TextUtils.isEmpty(name)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_name))
            return false
        }

        if (TextUtils.isEmpty(mobile)) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.enter_mobile_number)
            )
            return false
        }

        return true
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

    private fun callGetBlockListByStateAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", sessionManager!!.language)
        rootObject.addProperty("cityId", cityId)
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<BlockResponsePOJO> = apiService1.getBlockListByStateId(gsonObject)
        call1.enqueue(object : Callback<BlockResponsePOJO?> {
            override fun onResponse(
                call: Call<BlockResponsePOJO?>,
                response: Response<BlockResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        blockList = response.body()!!.blocklist
                        for (block in blockList) {
                            blockNameList.add(block.Name)
                            reqDataBlock[block.Name] = block.Id
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BlockResponsePOJO?>, t: Throwable) {

                Log.d("RegisterActivity", "onFailure: " + t.localizedMessage)

                ProgressDialog.hideProgressDialog()
            }
        })
    }
}