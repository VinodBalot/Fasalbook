package com.wasfat.ui.activity.buyAndSell.landscapeAndGardening

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
import com.wasfat.databinding.ActivityLandscapeBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.LandscapeRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LandscapeActivity : BaseBindingActivity() {

    var binding: ActivityLandscapeBinding? = null
    var onClickListener: View.OnClickListener? = null

    var landscapeList: ArrayList<UserLandscapeProduct> = ArrayList()
    lateinit var landscapeRVAdapter: LandscapeRVAdapter

    lateinit var parentCategory: Category
    lateinit var type: BuySellType

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            type: BuySellType,
            isClear: Boolean
        ) {
            val intent = Intent(activity, LandscapeActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_landscape)
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
            AddLandscapeActivity.startActivity(mActivity!!, parentCategory.PKID, false)
        }
    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(this)
        binding!!.rvUserFarms.layoutManager = layoutManager1
        binding!!.rvUserFarms.setHasFixedSize(true)

        fetchUserLandscapeFromAPI()

    }


    private fun landscapeItemClicked(userLandscapeProduct: UserLandscapeProduct) {

        Log.d("TAG", "farmItemClicked: " + userLandscapeProduct)

        if (type == BuySellType.SELL)
            showItemSelectionDialog(userLandscapeProduct)
        else
            DetailsLandscapeActivity.startActivity(
                mActivity!!,
                userLandscapeProduct,
                false
            )
    }

    private fun showItemSelectionDialog(userLandscapeProduct: UserLandscapeProduct) {

        val options = arrayOf<CharSequence>("Edit Item", "Delete Item", "Details", "Cancel")
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("Landscape Item Menu")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Edit Item") {

                EditLandscapeActivity.startActivity(
                    mActivity!!,
                    parentCategory.PKID.toString(),
                    userLandscapeProduct,
                    false
                )

            } else if (options[item] == "Delete Item") {

                deleteSelectedItem(userLandscapeProduct)

            } else if (options[item] == "Details") {

                DetailsLandscapeActivity.startActivity(
                    mActivity!!,
                    userLandscapeProduct,
                    false
                )

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun deleteSelectedItem(userLandscapeProduct: UserLandscapeProduct) {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("ProductId", userLandscapeProduct.ProductId)
        rootObject.addProperty("UserId", sessionManager!!.userId)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<DeleteFarmResponse> = apiService1.deleteLandscapeItem(gsonObject)
        call1.enqueue(object : Callback<DeleteFarmResponse?> {
            override fun onResponse(
                call: Call<DeleteFarmResponse?>,
                response: Response<DeleteFarmResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        UtilityMethod.showToastMessageSuccess(
                            mActivity!!,
                            getString(R.string.label_item_delete_successful)
                        )
                        finish()
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
        rootObject.addProperty("Categoryid", parentCategory.PKID)
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<UserLandscapeProductsResponsePOJO> =
            apiService1.getLandscapeItems(gsonObject)
        call1.enqueue(object : Callback<UserLandscapeProductsResponsePOJO?> {
            override fun onResponse(
                call: Call<UserLandscapeProductsResponsePOJO?>,
                response: Response<UserLandscapeProductsResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {

                    if (response.isSuccessful) {

                        landscapeList = response.body()!!.productList

                        Log.d("ITEMS", "onResponse: " + landscapeList)

                        landscapeRVAdapter = LandscapeRVAdapter(
                            mActivity!!,
                            { landscape -> landscapeItemClicked(landscape) },
                            landscapeList
                        )

                        binding!!.rvUserFarms.adapter = landscapeRVAdapter

                        if (landscapeList.size == 0) {
                            binding!!.rvUserFarms.visibility = View.GONE
                            binding!!.txtNoProductFound.visibility = View.VISIBLE
                        } else {
                            binding!!.rvUserFarms.visibility = View.VISIBLE
                            binding!!.txtNoProductFound.visibility = View.GONE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserLandscapeProductsResponsePOJO?>, t: Throwable) {
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
            R.id.btnSubmit -> {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}