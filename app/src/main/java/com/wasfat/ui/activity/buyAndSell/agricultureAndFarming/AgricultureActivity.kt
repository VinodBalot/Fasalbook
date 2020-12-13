package com.wasfat.ui.activity.buyAndSell.agricultureAndFarming

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
import com.wasfat.databinding.ActivityFoodGainBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.ItemRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.ui.pojo.Unit
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgricultureActivity : BaseBindingActivity() {

    var binding: ActivityFoodGainBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<Category> = ArrayList()
    var productList: ArrayList<UserProduct> = ArrayList()
    var unitList: ArrayList<Unit> = ArrayList()
    var unitNameList: ArrayList<String> = ArrayList()
    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    lateinit var itemRVAdapter: ItemRVAdapter

    lateinit var parentCategory: Category
    lateinit var type: BuySellType

    //  var viewModel: VendorViewModel? = null
    val RequestPermissionCode = 7

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            type: BuySellType,
            isClear: Boolean
        ) {
            val intent = Intent(activity, AgricultureActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_gain)
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
        getUnitListFromAPI()
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
            AddAndEditFormAgricultureActivity.startActivity(
                mActivity!!,
                parentCategory,
                type,
                false
            )
        }
    }

    private fun setAdapter() {
        val layoutManager1 = LinearLayoutManager(this)
        binding!!.rvProduct.layoutManager = layoutManager1
        binding!!.rvProduct.setHasFixedSize(true)
    }

    private fun fetchItemsFromAPI() {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("CategoryId", parentCategory.PKID)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<UserProductsResponsePOJO> = apiService1.getUserProducts(gsonObject)
        call1.enqueue(object : Callback<UserProductsResponsePOJO?> {
            override fun onResponse(
                call: Call<UserProductsResponsePOJO?>,
                response: Response<UserProductsResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {

                    if (response.isSuccessful) {

                        productList = response.body()!!.productList

                        Log.d("ITEMS", "onResponse: " + productList)

                        itemRVAdapter = ItemRVAdapter(
                            mActivity!!,
                            { product -> categoryItemClicked(product) },
                            productList, unitList
                        )

                        binding!!.rvProduct.adapter = itemRVAdapter

                        if (productList.size == 0) {
                            binding!!.rvProduct.visibility = View.GONE
                            binding!!.txtNoProductFound.visibility = View.VISIBLE
                        } else {
                            binding!!.rvProduct.visibility = View.VISIBLE
                            binding!!.txtNoProductFound.visibility = View.GONE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserProductsResponsePOJO?>, t: Throwable) {
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
            R.id.imvRemoveImage -> {
                removeImageSelection(view.tag as Int)
            }
            R.id.btnSubmit -> {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun removeImageSelection(position: Int) {
        imageList.removeAt(position)
        imageListRVAdapter!!.notifyItemRemoved(position)
        imageListRVAdapter!!.notifyItemRangeChanged(position, imageList.size)
        imageListRVAdapter!!.notifyDataSetChanged()

    }

    private fun categoryItemClicked(product: UserProduct) {
        showItemSelectionDialog(product)

    }

    private fun showItemSelectionDialog(product: UserProduct) {
        val options = arrayOf<CharSequence>("Edit Item", "Delete Item", "Details", "Cancel")
        val builder = AlertDialog.Builder(this@AgricultureActivity)
        builder.setTitle("Item Menu")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Edit Item") {

                EditAgricultureActivity.startActivity(
                    mActivity!!,
                    product,
                    parentCategory.PKID.toString(),
                    false
                )

            } else if (options[item] == "Delete Item") {
                deleteSelectedItem(product)

            } else if (options[item] == "Details") {
                if (unitNameList.isNotEmpty()) {
                    DetailsAgricultureActivity.startActivity(
                        mActivity!!,
                        product,
                        unitNameList.get(product.UnitId.toInt()),
                        unitNameList.get(product.RateUnitId.toInt()),
                        false
                    )
                }
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun deleteSelectedItem(product: UserProduct) {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("ProductId", product.ProductId)
        rootObject.addProperty("UserId", sessionManager!!.userId)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<DeleteItemResponse> = apiService1.deleteUserItem(gsonObject)
        call1.enqueue(object : Callback<DeleteItemResponse?> {
            override fun onResponse(
                call: Call<DeleteItemResponse?>,
                response: Response<DeleteItemResponse?>
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

            override fun onFailure(call: Call<DeleteItemResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        fetchItemsFromAPI()
    }

    private fun getUnitListFromAPI() {
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<UnitListResponsePOJO> = apiService1.getProductUnitList()

        call1.enqueue(object : Callback<UnitListResponsePOJO?> {
            override fun onResponse(
                call: Call<UnitListResponsePOJO?>,
                response: Response<UnitListResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        var list: ArrayList<Unit> = response.body()!!.unitList
                        list.forEach {
                            unitNameList.add(it.Name)
                            unitList.add(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UnitListResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }


}