package com.wasfat.ui.activity.buyAndSell.farmtourism

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
import com.wasfat.databinding.ActivityUserFarmTourismBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.FarmRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FarmTourismActivity : BaseBindingActivity() {

    var binding: ActivityUserFarmTourismBinding? = null
    var onClickListener: View.OnClickListener? = null

    var farmList: ArrayList<UserFarms> = ArrayList()
    lateinit var farmRVAdapter: FarmRVAdapter

    lateinit var parentCategory: Category
    lateinit var type: BuySellType

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            type: BuySellType,
            isClear: Boolean
        ) {
            val intent = Intent(activity, FarmTourismActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_farm_tourism)
        //  viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
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
            AddFarmTourismActivity.startActivity(mActivity!!,parentCategory, BuySellType.SELL, false)
        }
    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(this)
        binding!!.rvUserFarms.layoutManager = layoutManager1
        binding!!.rvUserFarms.setHasFixedSize(true)

        fetchUserFarmsFromAPI()

    }

    private fun farmItemClicked(userFarm: UserFarms) {

        Log.d("TAG", "farmItemClicked: " + userFarm)

        if (type == BuySellType.SELL)
            showItemSelectionDialog(userFarm)
        else
            DetailsFarmTourismActivity.startActivity(
                mActivity!!,
                userFarm,
                false
            )
    }

    private fun showItemSelectionDialog(userFarm: UserFarms) {

        val options = arrayOf<CharSequence>("Edit Item", "Delete Item", "Details", "Cancel")
        val builder = AlertDialog.Builder(this@FarmTourismActivity)
        builder.setTitle("Farm Item Menu")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Edit Item") {

                EditFarmActivity.startActivity(
                    mActivity!!,
                    userFarm,
                    parentCategory.PKID.toString(),
                    false
                )

            } else if (options[item] == "Delete Item") {

                deleteSelectedItem(userFarm)

            } else if (options[item] == "Details") {
                DetailsFarmTourismActivity.startActivity(
                    mActivity!!,
                    userFarm,
                    false
                )

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun deleteSelectedItem(userFarm: UserFarms) {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("FarmId", userFarm.PKID)
        rootObject.addProperty("UserId", sessionManager!!.userId)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<DeleteFarmResponse> = apiService1.deleteUserFarm(gsonObject)
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


    private fun fetchUserFarmsFromAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserId", sessionManager!!.userId)
        // rootObject.addProperty("languageId", sessionManager!!.language)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<UserFarmsResponsePOJO> = apiService1.getUserFarms(gsonObject)
        call1.enqueue(object : Callback<UserFarmsResponsePOJO?> {
            override fun onResponse(
                call: Call<UserFarmsResponsePOJO?>,
                response: Response<UserFarmsResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {

                    if (response.isSuccessful) {

                        farmList = response.body()!!.FarmList

                        Log.d("ITEMS", "onResponse: " + farmList)

                        farmRVAdapter = FarmRVAdapter(
                            mActivity!!,
                            { farm -> farmItemClicked(farm) },
                            farmList
                        )

                        binding!!.rvUserFarms.adapter = farmRVAdapter

                        if (farmList.size == 0) {
                            binding!!.rvUserFarms.visibility = View.GONE
                            binding!!.txtNoProductFound.visibility = View.VISIBLE
                        } else {
                            binding!!.rvUserFarms.visibility = View.VISIBLE
                            binding!!.txtNoProductFound.visibility = View.GONE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserFarmsResponsePOJO?>, t: Throwable) {
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