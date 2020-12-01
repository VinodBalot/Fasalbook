package com.wasfat.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityEditFarmBinding
import com.wasfat.databinding.ActivityEditItemBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.FarmAttractionsAdapter
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.*
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import kotlinx.android.synthetic.main.activity_register.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditFarmActivity : BaseBindingActivity() {

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

    var categoryList: ArrayList<Category> = ArrayList()
    var selectedAttractionsList: ArrayList<String> = ArrayList()

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    var userFarmImageList : ArrayList<String> = ArrayList()

    var binding: ActivityEditFarmBinding? = null
    var onClickListener: View.OnClickListener? = null

    var selectedAttractionString : String = ""

    lateinit var farm: UserFarms
    lateinit var parentCategoryId: String

    companion object {

        fun startActivity(
            activity: Activity,
            farm: UserFarms,
            categoryId: String,
            isClear: Boolean
        ) {
            val intent = Intent(activity, EditFarmActivity::class.java)
            intent.putExtra("farm", farm)
            intent.putExtra("categoryId", categoryId)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_farm)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        parentCategoryId = intent.getStringExtra("categoryId").toString()

        //Getting parent category from parent
        farm = (intent.getSerializableExtra("farm") as? UserFarms)!!
    }

    override fun initializeObject() {
        onClickListener = this

        setActivityForEdit()
    }

    override fun setListeners() {

        binding!!.imvClose.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener (onClickListener)
        //binding!!.imvRemoveImage.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener (onClickListener)
        binding!!.imvAddMore.setOnClickListener(onClickListener)
        binding!!.btnAddFarmTourism.setOnClickListener(onClickListener)
        binding!!.btnAddressMap.setOnClickListener(onClickListener)
        binding!!.edtState.setOnClickListener(onClickListener)
        binding!!.edtCity.setOnClickListener(onClickListener)
        binding!!.edtBlock.setOnClickListener(onClickListener)

        binding!!.edtAtraction.setOnClickListener(onClickListener)

    }

    private fun setActivityForEdit() {

        stateId = farm.StateId
        cityId = farm.CityId
        blockId = farm.BlockId

        callGetStateListByCountryAPI()
        callGetCityListByStateAPI()
        callGetBlockListByStateAPI()
        fetchCategoriesOfParentFromAPI()

        binding!!.edtFarmName.setText( farm.FarmName )
        binding!!.edtAddress.setText(farm.Address )
        binding!!.edtContactNumber.setText(farm.ContactNo)
        binding!!.edtEmail.setText(farm.EmailId)
        binding!!.edtFacilities.setText(farm.Facilities)
        binding!!.edtWebsite.setText(farm.Website )
        binding!!.edtPrice.setText(farm.Price.toString())
        binding!!.edtAtraction.text = farm.Attraction

        selectedAttractionString = farm.Attraction

        binding!!.cbPublished.isChecked = farm.Published

        farm.ImageList.forEach {

            if (it.ImageName.isNotEmpty()) {
                val image = it.Path + "/" + it.ImageName
                imageList.add(image)
                userFarmImageList.add(image)
            }
        }

        setVisibiltyForImageSelection()

        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.rvImage.layoutManager = layoutManager1
        binding!!.rvImage.setHasFixedSize(true)

        imageListRVAdapter = ImageListRVAdapter(mActivity, onClickListener, imageList)
        binding!!.rvImage.adapter = imageListRVAdapter

    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.edtAtraction ->{
                ProgressDialog.showProgressDialog(mActivity!!)
                openAttractionDialog()
            }
            R.id.imvClose -> {
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
            R.id.btnAddressMap ->{
                //TODO: Open MAP for address
                Toast.makeText(mActivity!!,"Map Will Open Here",Toast.LENGTH_SHORT).show()
            }
            R.id.rlImage -> {
                if (checkingPermissionIsEnabledOrNot()) {
                    showImageSelectionDialog()
                } else {
                    requestMultiplePermission()
                }
            }
            R.id.imvAddMore -> {
                if (checkingPermissionIsEnabledOrNot()) {
                    showImageSelectionDialog()
                } else {
                    requestMultiplePermission()
                }
            }
            R.id.imvRemoveImage -> {
                removeImageSelection(view.tag as Int)
            }
            R.id.btnAddFarmTourism -> {

                Log.d("TAG", "onClick: BTN CLICKED")

                if (isValidFormData(
                        binding!!.edtFarmName.text.toString(),
                        binding!!.edtAddress.text.toString(),
                        binding!!.edtContactNumber.text.toString(),
                        binding!!.edtFacilities.text.toString(),
                        binding!!.edtAtraction.text.toString(),
                        binding!!.edtEmail.text.toString(),
                        binding!!.edtWebsite.text.toString(),
                        binding!!.edtPrice.text.toString()
                    )
                ) {

                    editFarmTourismItem(
                        binding!!.edtFarmName.text.toString(),
                        binding!!.edtAddress.text.toString(),
                        binding!!.edtContactNumber.text.toString(),
                        binding!!.edtFacilities.text.toString(),
                        binding!!.edtAtraction.text.toString(),
                        binding!!.cbPublished.isChecked,
                        binding!!.edtEmail.text.toString(),
                        binding!!.edtWebsite.text.toString(),
                        binding!!.edtPrice.text.toString()
                    )

                }
            }
        }
    }

    private fun removeImageSelection(position: Int) {

        val imageUrl : String = imageList[position]

        if(userFarmImageList.contains(imageUrl)){

            ProgressDialog.showProgressDialog(mActivity!!)

            farm.ImageList.forEach {
                if (it.ImageName.isNotEmpty()) {
                    val image = it.Path + "/" + it.ImageName
                    if(image == imageUrl){
                        deleteImage(it.PKID,position)
                    }
                }
            }
        }else{

            imageList.removeAt(position)
            imageListRVAdapter!!.notifyItemRemoved(position)
            imageListRVAdapter!!.notifyItemRangeChanged(position, imageList.size)
            imageListRVAdapter!!.notifyDataSetChanged()

            setVisibiltyForImageSelection()

        }



    }

    private fun setVisibiltyForImageSelection() {

        if (imageList.size > 0) {
            binding!!.rvImage.visibility = View.VISIBLE
            binding!!.rlImage.visibility = View.GONE
            binding!!.imvAddMoreLayout.visibility = View.VISIBLE
        } else {
            binding!!.rvImage.visibility = View.GONE
            binding!!.rlImage.visibility = View.VISIBLE
            binding!!.imvAddMoreLayout.visibility = View.GONE
        }

    }

    private fun deleteImage(imageId :  String, position: Int){

        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("FarmId", farm.PKID)
        rootObject.addProperty("ImageId", imageId)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddFarmItemResponse> = apiService1.deleteUserFarmImage(gsonObject)
        call1.enqueue(object : Callback<AddFarmItemResponse?> {
            override fun onResponse(
                call: Call<AddFarmItemResponse?>,
                response: Response<AddFarmItemResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Response == "success") {
                            Log.d("TAG", "onResponse: Image delete Successful")
                            imageList.removeAt(position)
                            imageListRVAdapter!!.notifyItemRemoved(position)
                            imageListRVAdapter!!.notifyItemRangeChanged(position, imageList.size)
                            imageListRVAdapter!!.notifyDataSetChanged()

                            setVisibiltyForImageSelection()

                            UtilityMethod.showToastMessageError(mActivity!!,"Image Deleted")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AddFarmItemResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

    private fun uploadImage(imageBase64 :  String){

        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("FarmId", farm.PKID)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Image", imageBase64)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddFarmItemResponse> = apiService1.addUserFarmImage(gsonObject)
        call1.enqueue(object : Callback<AddFarmItemResponse?> {
            override fun onResponse(
                call: Call<AddFarmItemResponse?>,
                response: Response<AddFarmItemResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Response == "success") {
                            Log.d("TAG", "onResponse: Image upload Successful")
                            UtilityMethod.showToastMessageError(mActivity!!,"New Image Uploaded")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AddFarmItemResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

    private fun editFarmTourismItem(
        farmName: String,
        address: String,
        contactNumber: String,
        facilities: String,
        attractions: String,
        published : Boolean,
        email : String,
        website : String,
        price : String
    ) {

        ProgressDialog.showProgressDialog(mActivity!!)

        imageList.forEach {

            if(!userFarmImageList.contains(it)){
                val imageBase64 = UtilityMethod.imageEncoder(it)
                uploadImage(imageBase64)
            }

        }

        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("PKID", farm.PKID)
        if(farmName != farm.FarmName){
            rootObject.addProperty("FarmName", farmName)
        }
        if(address != farm.Address){
            rootObject.addProperty("Address", address)
        }
        if(contactNumber != farm.ContactNo){
            rootObject.addProperty("ContactNo", contactNumber)
        }
        if(facilities != farm.Facilities){
            rootObject.addProperty("Facilities", facilities)
        }
        if(attractions != farm.Attraction){
            rootObject.addProperty("Attraction",attractions)
        }
        if(email != farm.EmailId){
            rootObject.addProperty("EmailId", email)
        }
        if(website != farm.Website){
            rootObject.addProperty("Website", website)
        }
        if(price != farm.Price.toString()){
            rootObject.addProperty("Price", price)
        }
        if(published != farm.Published){
            rootObject.addProperty("Published", published)
        }

        rootObject.addProperty("lat", 0)
        rootObject.addProperty("lmg", 0)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("BlockID", blockId)


        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddFarmItemResponse> = apiService1.editUserFarms(gsonObject)
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


    private fun fetchCategoriesOfParentFromAPI(){

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("CategoryId",parentCategoryId)
        rootObject.addProperty("LanguageId", "1")

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<CategoryResponsePOJO> = apiService1.getCategoriesByParentId(gsonObject)
        call1.enqueue(object : Callback<CategoryResponsePOJO?> {
            override fun onResponse(
                call: Call<CategoryResponsePOJO?>,
                response: Response<CategoryResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {

                        categoryList = response.body()!!.categoryList

                    }
                }
            }

            override fun onFailure(call: Call<CategoryResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })


    }


    private fun openAttractionDialog(){
        val view1: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.list_farm_attraction_item,
                null,
                false
            )

        val dialog = BottomSheetDialog(mActivity!!)
        dialog.setContentView(view1)
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val submitButton = view1.findViewById<View>(R.id.btnSubmitAttraction) as Button
        val arrayAdapter = FarmAttractionsAdapter(
            mActivity!!,
            { checked: Boolean, category: Category -> onAttractionCheckBoxClicked(checked,category) },
            categoryList,
            selectedAttractionString
        )
        lv.adapter = arrayAdapter
        dialog.show()
        ProgressDialog.hideProgressDialog()

        submitButton.setOnClickListener{
            selectedAttractionString = ""

            selectedAttractionsList.forEach {
                selectedAttractionString +=
                    if(selectedAttractionString == ""){
                        it
                    }else{
                        ", $it"
                    }

            }

            binding!!.edtAtraction.text = selectedAttractionString

            dialog.dismiss()
        }
    }

    private fun onAttractionCheckBoxClicked(isChecked: Boolean,category: Category){

        if (isChecked){
            selectedAttractionsList.add(category.CategoryName)
        }else{
            selectedAttractionsList.remove(category.CategoryName)
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
        val dialog = android.app.AlertDialog.Builder(this@EditFarmActivity)
            .setTitle(title)
            .setView(view1).create()
        val lv = view1.findViewById<View>(R.id.listView) as ListView
        val edtSearch = view1.findViewById<View>(R.id.edtSearch) as EditText
        val arrayAdapter = ArrayAdapter(
            this@EditFarmActivity,
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
                        stateNameList.clear()
                        reqDataState.clear()
                        for (state in stateList) {
                            stateNameList.add(state.Name)
                            reqDataState[state.Name] = state.Id
                            if(state.Id == stateId){
                                binding!!.edtState.setText(state.Name)
                            }
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
                        cityNameList.clear()
                        reqDataCity.clear()
                        for (city in cityList) {
                            cityNameList.add(city.Name)
                            reqDataCity[city.Name] = city.Id

                            if(city.Id == cityId){
                                binding!!.edtCity.setText(city.Name)
                            }
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
                        blockNameList.clear()
                        reqDataBlock.clear()
                        for (block in blockList) {
                            blockNameList.add(block.Name)
                            reqDataBlock[block.Name] = block.Id
                            if(block.Id == blockId){
                                binding!!.edtBlock.setText(block.Name)
                            }
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

    private fun isValidFormData(
        farmName: String,
        address: String,
        contactNumber: String,
        facilities: String,
        attractions: String,
        email : String,
        website : String,
        price : String
    ): Boolean {

        Log.d("TAG", "isValidFormData: Validation is working" )

        if (TextUtils.isEmpty(farmName)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_name))
            return false
        }

        if (TextUtils.isEmpty(address)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_address))
            return false
        }

        if (TextUtils.isEmpty(contactNumber)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_contact_number))
            return false
        }

        if (TextUtils.isEmpty(facilities)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_facitilites))
            return false
        }

        if (TextUtils.isEmpty(attractions)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_attraction))
            return false
        }

        if (TextUtils.isEmpty(email)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_contact_email))
            return false
        }

        if (TextUtils.isEmpty(website)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_website))
            return false
        }

        if (TextUtils.isEmpty(price)) {
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.enter_farm_price))
            return false
        }

        if(imageList.size == 0){
            UtilityMethod.showToastMessageError(mActivity!!,"Please add at least one Image")
            return false
        }

        return true
    }


    private fun showImageSelectionDialog() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                EasyImage.openCameraForImage(mActivity!!, 100)
            } else if (options[item] == "Choose from Gallery") {
                EasyImage.openGallery(mActivity!!, 200)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    fun checkingPermissionIsEnabledOrNot(): Boolean {
        val FirstPermissionResult: Int =
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
        val SecondPermissionResult: Int =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val ThirdPermissionResult: Int =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && ThirdPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    //Permission function starts from here
    private fun requestMultiplePermission() {
        ActivityCompat.requestPermissions(
           mActivity!!, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 1000
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            1000 -> if (grantResults.isNotEmpty()) {
                val CameraPermission =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val ReadExtranalStorage =
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                val WriteExtranalStorage =
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (CameraPermission && ReadExtranalStorage && WriteExtranalStorage) {
                    showImageSelectionDialog()
                } else {
                    Toast.makeText(
                        mActivity,
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            mActivity,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {

                    if (type == 100) {

                        imageList.add(imageFiles[0].absolutePath)
                        imageListRVAdapter!!.notifyDataSetChanged()
                        setVisibiltyForImageSelection()

                    } else {
                        imageList.add(imageFiles[0].absolutePath)
                        imageListRVAdapter!!.notifyDataSetChanged()
                        setVisibiltyForImageSelection()
                    }
                }

                override fun onImagePickerError(
                    e: java.lang.Exception,
                    source: EasyImage.ImageSource,
                    type: Int
                ) {
                    e.printStackTrace()
                }

                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                    if (source == ImageProvider.CAMERA) {
                        val photoFile =
                            EasyImage.lastlyTakenButCanceledPhoto(mActivity!!)
                        photoFile?.delete()
                    }
                }
            })
    }


}
