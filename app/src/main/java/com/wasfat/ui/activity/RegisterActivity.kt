package com.wasfat.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.ViewModelProvider
import com.wasfat.R
import com.wasfat.databinding.ActivityRegisterBinding
import com.wasfat.network.ApiResponse
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.StateResponse
import com.wasfat.ui.pojo.StateResponseItem
import com.wasfat.ui.viewModel.RegisterViewModel
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import java.util.*
import kotlin.collections.ArrayList

class RegisterActivity : BaseBindingActivity() {

    var binding: ActivityRegisterBinding? = null
    var onClickListener: View.OnClickListener? = null
    var stateList: ArrayList<StateResponseItem> = ArrayList()
    var stateNameList: ArrayList<String> = ArrayList()
    var cityNameList: ArrayList<String> = ArrayList()
    var viewModel: RegisterViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, RegisterActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        //  callGetStateListByCountryAPI()
        viewModel!!.responseStateByCountryIdData.observe(this, androidx.lifecycle.Observer {
            handleResult(it)
        })
    }

    private fun handleResult(result: ApiResponse<StateResponse>?) {
        when (result!!.status) {
            ApiResponse.Status.ERROR -> {
                ProgressDialog.hideProgressDialog()
                UtilityMethod.showToastMessageFailed(mActivity!!, result.error!!.message!!)
            }
            ApiResponse.Status.LOADING -> {
                ProgressDialog.showProgressDialog(this)
            }
            ApiResponse.Status.SUCCESS -> {
                ProgressDialog.hideProgressDialog()
                // stateList.addAll(result.data[0])
                if (stateList.isNotEmpty()) {
                    for (name in stateList) {
                        stateNameList.add(name.StateName)
                    }
                    setAdapter()
                }

            }
        }
    }

    private fun selectDialog(
        title: String,
        stateList: List<String>,
        edtState: EditText
    ) {
        val view1: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.list_search_item,
                null,
                false
            )
        val dialog = AlertDialog.Builder(this@RegisterActivity)
            .setTitle(title)
            .setView(view1).create()
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val edtSearch = view1.findViewById<View>(R.id.edtSearch) as EditText
        val arrayAdapter = ArrayAdapter(
            this@RegisterActivity,
            android.R.layout.simple_list_item_1,
            stateList
        )
        lv.adapter = arrayAdapter
        lv.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                val entry = parent.adapter.getItem(position) as String
                edtState.setText(entry)
                // agentId = hashMap.get(entry)
                dialog.cancel()
            }
        dialog.show()
        edtSearch.addTextChangedListener(object : TextViewBindingAdapter.OnTextChanged)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun callGetStateListByCountryAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        val myMap = HashMap<String, String>()
        myMap["languageId"] = Constants.LANGUAGE
        myMap["countryId"] = Constants.COUNTRYID
        viewModel!!.getStateByCountryId(myMap)

    }

    private fun setAdapter() {
        var stateAdapter =
            ArrayAdapter(mActivity!!, android.R.layout.simple_spinner_item, stateNameList)
        binding!!.spinnerState.adapter = stateAdapter
    }

    override fun setListeners() {
        binding!!.btnSubmit.setOnClickListener(onClickListener)
        binding!!.edtState.setOnClickListener(onClickListener)
        binding!!.edtCity.setOnClickListener(onClickListener)
    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.btnSubmit -> {
                LoginActivity.startActivity(mActivity!!, null, false);
            }
            R.id.edtState -> {
                stateNameList.clear()
                stateNameList.add("Rajasthan")
                stateNameList.add("Uttar pradesh")
                stateNameList.add("Jammu Kashmir")
                stateNameList.add("Gujrat")
                stateNameList.add("Madhya pradesh")
                stateNameList.add("Udisa")
                stateNameList.add("Bihar")
                stateNameList.add("jharkhand")
                selectDialog(getString(R.string.select_state), stateNameList, binding!!.edtState)
            }
            R.id.edtCity -> {
                cityNameList.clear()
                cityNameList.add("Mumbai")
                cityNameList.add("Jaipur")
                cityNameList.add("Ahamdabad")
                cityNameList.add("Surat")
                cityNameList.add("Jodhpur")
                cityNameList.add("Udaipur")
                cityNameList.add("Jalore")
                cityNameList.add("Alwar")
                selectDialog(getString(R.string.select_state), cityNameList, binding!!.edtCity)
            }
        }

    }


    /*   private fun isValidFormData(
           name: String,
           password: String,
           confirmPassword: String
       ): Boolean {

           if (TextUtils.isEmpty(name)) {
               UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_full_name))
               return false
           }

           if (TextUtils.isEmpty(password)) {
               UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_password))
               return false
           }


           *//* if (!UtilityMethod.isValidEmail(email)) {
             UtilityMethod.showToastMessageError(
                 mActivity!!,
                 getString(R.string.enter_valid_email_id)
             )
             return false
         }*//*

        *//*  val pattern: Pattern
          val regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
          pattern = Pattern.compile(regex)
  *//*
        *//*    if (!pattern.matcher(password).matches()) {
                UtilityMethod.showToastMessageError(
                    mActivity!!,
                    getString(R.string.password_should_contain_char_digit_special)
                )
                return false
            }*//*

        if (password.length < 6) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.minimum_6_characters)
            )
            return false
        }



        if (TextUtils.isEmpty(confirmPassword)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_cpassword))
            return false
        }
        if (password != confirmPassword) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.password_not_match))
            return false
        }

        return true
    }
    */


}