package com.wasfat.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityEventCategoryBinding
import com.wasfat.databinding.ActivityGovtSchemesBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.GovtSchemesRVAdapter
import com.wasfat.ui.adapter.OrganicRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.*
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GovtSchemesActivity : BaseBindingActivity() {

    private var stateId: Int = -1
    val reqDataState: HashMap<String, Int> = HashMap()
    var stateList: ArrayList<Statelist> = ArrayList()
    var stateNameList: ArrayList<String> = ArrayList()


    var binding : ActivityGovtSchemesBinding? = null
    var onClickListener: View.OnClickListener? = null
    var govtSchemesList: ArrayList<GovtSchemes> = ArrayList()

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, GovtSchemesActivity::class.java)
            if(bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_govt_schemes)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

    }

    override fun initializeObject() {

        onClickListener = this

        binding!!.textTitle.text = getText(R.string.label_govt_schemes)

        fetchStateList()

        setAdapter()
    }

    override fun setListeners() {

        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.edtState.setOnClickListener(onClickListener)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.edtState -> {
                selectDialog(
                    getString(R.string.select_state),
                    stateNameList,
                    binding!!.edtState,
                    reqDataState
                )
            }
        }
    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvSchemes.layoutManager = layoutManager1
        binding!!.rvSchemes.setHasFixedSize(true)

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
        val dialog = AlertDialog.Builder(this@GovtSchemesActivity)
            .setTitle(title)
            .setView(view1).create()
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val edtSearch = view1.findViewById<View>(R.id.edtSearch) as EditText
        val arrayAdapter = ArrayAdapter(
            this@GovtSchemesActivity,
            android.R.layout.simple_list_item_1,
            stateList
        )

        lv.adapter = arrayAdapter

        lv.onItemClickListener =
            AdapterView.OnItemClickListener {
                    parent: AdapterView<*>, view: View?, position: Int, id: Long ->

                val entry = parent.adapter.getItem(position) as String
                if (title == getString(R.string.select_state)) {
                    edtState.setText(entry)
                    stateId = reqData[entry]!!
                    fetchSchemesForStateId(stateId)
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

    private fun fetchStateList() {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", "1")
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


    private fun schemesItemClicked(govtSchemes : GovtSchemes) {

        Log.d("c", "eventItemClicked: " + govtSchemes.GSName + "  " + govtSchemes.PKID)

        val bundle = Bundle()
        bundle.putString("url",govtSchemes.GSURL)
        bundle.putString("title",govtSchemes.GSName)

        WebViewActivity.startActivity(mActivity!!,bundle,false)

    }


    private fun fetchSchemesForStateId(stateId: Int) {

        Log.d("GOVT SCHEMES", "fetchSchemesForStateId: " + stateId)

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", "1")
        rootObject.addProperty("StateId", stateId)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject

        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<GovtSchemesResponsePOJO> = apiService1.getGovtSchemesByStateId(gsonObject)

        call1.enqueue(object : Callback<GovtSchemesResponsePOJO?> {
            override fun onResponse(
                call: Call<GovtSchemesResponsePOJO?>,
                response: Response<GovtSchemesResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {

                        govtSchemesList = response.body()!!.govtSchemetList

                        val govtSchemesRVAdapter = GovtSchemesRVAdapter(mActivity,
                            { govtSchemes -> schemesItemClicked(govtSchemes) },
                            govtSchemesList)

                        binding!!.rvSchemes.adapter = govtSchemesRVAdapter

                    }
                }
            }

            override fun onFailure(call: Call<GovtSchemesResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

}