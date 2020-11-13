package com.wasfat.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityAgricultureBinding.inflate
import com.wasfat.databinding.ActivityBuySearchBinding
import com.wasfat.databinding.ActivityFoodGainBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.adapter.OrganicRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.ItemRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.ui.pojo.Unit
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BuySearchActivity : BaseBindingActivity() {

    var binding: ActivityBuySearchBinding? = null

    private var cityId: Int = -1
    private var stateId: Int = -1
    private var blockId: Int = -1

    var stateList: ArrayList<Statelist> = ArrayList()
    var stateNameList: ArrayList<String> = ArrayList()
    var cityList: ArrayList<Citylist> = ArrayList()
    var cityNameList: ArrayList<String> = ArrayList()
    var blockList: ArrayList<Blocklist> = ArrayList()
    var blockNameList: ArrayList<String> = ArrayList()

    val reqDataState: HashMap<String, Int> = HashMap()
    val reqDataCity: HashMap<String, Int> = HashMap()
    val reqDataBlock: HashMap<String, Int> = HashMap()

    lateinit var  parentCategory : Category

    var productList: ArrayList<UserProduct> = ArrayList()
    var itemRVAdapter: ItemRVAdapter? = null
    var unitList: ArrayList<Unit> = ArrayList()
    var unitNameList: ArrayList<String> = ArrayList()

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            type: BuySellType,
            isClear: Boolean
        ) {
            val intent = Intent(activity, BuySearchActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_buy_search)
    }

    override fun createActivityObject() {

        mActivity = this

        //Getting parent category from parent
        parentCategory = (intent.getSerializableExtra("category") as? Category)!!

    }

    override fun initializeObject() {

        binding!!.textTitle.text = parentCategory.CategoryName

        getUnitListFromAPI()
        callGetStateListByCountryAPI()

    }

    override fun setListeners() {

        binding!!.fabSearch.setOnClickListener {

            openSearchItemDialog()

        }

    }

    override fun onClick(p0: View?) {

    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvProduct.layoutManager = layoutManager1
        binding!!.rvProduct.setHasFixedSize(true)

        fetchAllProductsOfCategory(0,0,0)

    }

    private fun categoryItemClicked(product: UserProduct) {

        //showItemSelectionDialog(product)

    }


    private fun selectDialog(
        title: String,
        stateList: List<String>,
        edtState: EditText,
        edtCity: EditText,
        edtBlock: EditText,
        reqData: HashMap<String, Int>
    ) {
        val view1: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.list_search_item,
                null,
                false
            )
        val dialog = AlertDialog.Builder(this@BuySearchActivity)
            .setTitle(title)
            .setView(view1).create()
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val edtSearch = view1.findViewById<View>(R.id.edtSearch) as EditText
        val arrayAdapter = ArrayAdapter(
            this@BuySearchActivity,
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

    private fun openSearchItemDialog() {

        val view = layoutInflater.inflate(R.layout.bottomsheet_dialog_search_product, null)
        val txtDialogTitle = view.findViewById(R.id.txtDialogTitle) as TextView
        val imvClose = view.findViewById(R.id.imvClose) as ImageView

        val edtState = view.findViewById(R.id.edtState) as EditText
        val edtCity = view.findViewById(R.id.edtCity) as EditText
        val edtBlock = view.findViewById(R.id.edtBlock) as EditText
        val btnSearch = view.findViewById(R.id.btnAdd) as Button

        val dialog = BottomSheetDialog(mActivity!!)
        dialog!!.setContentView(view)
        dialog!!.show()

        edtState.setOnClickListener {
            selectDialog(
                getString(R.string.select_state),
                stateNameList,
                edtState,
                edtCity,
                edtBlock,
                reqDataState
            )
        }

        edtCity.setOnClickListener {

            selectDialog(
                getString(R.string.select_City),
                cityNameList,
                edtState,
                edtCity,
                edtBlock,
                reqDataCity
            )
        }

        edtBlock.setOnClickListener {
            selectDialog(
                getString(R.string.select_block),
                blockNameList,
                edtState,
                edtCity,
                edtBlock,
                reqDataBlock
            )
        }

        imvClose.setOnClickListener {
            dialog!!.dismiss()
        }

        btnSearch.setOnClickListener {

            fetchAllProductsOfCategory(stateId, cityId, blockId)
            dialog!!.dismiss()

        }
    }

    private fun fetchAllProductsOfCategory(
        stateId : Int,
        cityId  : Int,
        blockId : Int
    ){

        ProgressDialog.showProgressDialog(mActivity!!)
        val rootObject = JsonObject()
        rootObject.addProperty("StateId",stateId)
        rootObject.addProperty("CityId", cityId)
        rootObject.addProperty("BlockId",blockId)
        rootObject.addProperty("CategoryId", parentCategory.PKID)

        val jsonParser = JsonParser()
        val gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<UserProductsResponsePOJO> = apiService1.getProductsFromSearch(gsonObject)
        call1.enqueue(object : Callback<UserProductsResponsePOJO?> {
            override fun onResponse(
                call: Call<UserProductsResponsePOJO?>,
                response: Response<UserProductsResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {

                        productList.clear()

                        productList = response.body()!!.productList

                        Log.d("ITEMS", "onResponse: " + productList)

                        itemRVAdapter = ItemRVAdapter(
                            mActivity!!,
                            { product -> categoryItemClicked(product) },
                            productList, unitList
                        )

                        binding!!.rvProduct.adapter = itemRVAdapter

                        itemRVAdapter!!.notifyDataSetChanged()

                        if(productList.size == 0){
                            binding!!.rvProduct.visibility = View.GONE
                            binding!!.txtNoProductFound.visibility = View.VISIBLE
                        }else{
                            binding!!.rvProduct.visibility = View.VISIBLE
                            binding!!.txtNoProductFound.visibility = View.GONE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserProductsResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })


    }

    private fun getUnitListFromAPI(){

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

                        setAdapter()
                        Log.d("UNITS", "onResponse: " + unitList)

                    }
                }
            }

            override fun onFailure(call: Call<UnitListResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }

    private fun callGetStateListByCountryAPI() {

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

    private fun callGetCityListByStateAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", "1")
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
        rootObject.addProperty("languageId", "1")
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