package com.wasfat.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityFoodGainBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.adapter.OrganicRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.ItemRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.ui.pojo.Unit
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class ItemListActivity : BaseBindingActivity() {

    var binding: ActivityFoodGainBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<Category> = ArrayList()
    var productList: ArrayList<UserProduct> = ArrayList()
    var unitList: ArrayList<Unit> = ArrayList()
    var unitNameList: ArrayList<String> = ArrayList()
    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null
    var rlImage: RelativeLayout? = null
    var rvImage: RecyclerView? = null
    var imvAddMore: ImageView? = null

    lateinit var itemRVAdapter : ItemRVAdapter

    lateinit var  parentCategory : Category
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
            val intent = Intent(activity, ItemListActivity::class.java)
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

        binding!!.textTitle.text = parentCategory.CategoryName

        if(type == BuySellType.BUY)
            binding!!.fabAdd.visibility = View.GONE
        else
            setupFabButton()

        getUnitListFromAPI()
        setAdapter()

    }

    private fun setupFabButton(){

        binding!!.fabAdd.visibility = View.VISIBLE
        binding!!.fabAdd.setOnClickListener{
            addOrEditItemDialog(null)
        }
    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(this)
        binding!!.rvProduct.layoutManager = layoutManager1
        binding!!.rvProduct.setHasFixedSize(true)


        fetchItemsFromAPI()

    }


    private fun fetchItemsFromAPI(){

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserId",sessionManager!!.userId)
       // rootObject.addProperty("LanguageId", "1")

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

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
        }
    }


    private fun categoryItemClicked(product: UserProduct) {

        showItemSelectionDialog(product)

    }

    private fun showItemSelectionDialog(product: UserProduct) {

        val options = arrayOf<CharSequence>("Edit Item", "Delete Item", "Details", "Cancel")
        val builder = AlertDialog.Builder(this@ItemListActivity)
        builder.setTitle("Item Menu")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Edit Item") {

                addOrEditItemDialog(product)

            } else if (options[item] == "Details") {

                ItemDetailsActivity.startActivity(mActivity!!, product, unitNameList[product.UnitId.toInt()], false)

            } else if (options[item] == "Delete Item") {

                deleteSelectedItem(product)

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun deleteSelectedItem(product: UserProduct){

        Toast.makeText(mActivity!!,"DELETE OPERATION HERE ",Toast.LENGTH_SHORT).show()

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

                        Log.d("UNITS", "onResponse: " + unitList)



                    }
                }
            }

            override fun onFailure(call: Call<UnitListResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }

    private fun addOrEditItemDialog(product: UserProduct?) {

        val view = layoutInflater.inflate(R.layout.bottomsheet_dialog_add_food_gain, null)
        val txtDialogTitle = view.findViewById(R.id.txtDialogTitle) as TextView
        val imvClose = view.findViewById(R.id.imvClose) as ImageView
        imvAddMore = view.findViewById(R.id.imvAddMore) as ImageView
        val edtName = view.findViewById(R.id.edtName) as EditText
        val spinnerUnit = view.findViewById(R.id.spinnerUnit) as Spinner
        val edtQty = view.findViewById(R.id.edtQty) as EditText
        rlImage = view.findViewById(R.id.rlImage) as RelativeLayout
        rvImage = view.findViewById(R.id.rvImage) as RecyclerView
        val edtSpecification = view.findViewById(R.id.edtSpecification) as EditText
        val btnAdd = view.findViewById(R.id.btnAdd) as Button
        val dialog = BottomSheetDialog(mActivity!!)
        imageList.clear()
        dialog!!.setContentView(view)
        dialog!!.show()

        imageList.clear()


        if(product == null){
            //Add new Item
            txtDialogTitle.text = getString(R.string.label_add_item_dialog_title)
            btnAdd.setText(R.string.label_add_item_dialog_button)

        }else{
            //Edit current Item
            txtDialogTitle.text = getString(R.string.label_edit_item_dialog_title)
            edtName.setText(product.ProductName)
            edtSpecification.setText(product.ProductSmallDesc)
            edtQty.setText(product.Qty)
            spinnerUnit.setSelection(product.UnitId.toInt())
            btnAdd.setText(R.string.label_edit_item_dialog_button)

            product.ImageList.forEach {
                if(it.ImageName.isNotEmpty()){
                    val image = it.Path + "/" + it.ImageName
                    imageList.add(image)
                }
            }
        }

        if(imageList.size >= 3){
            rvImage!!.visibility = View.VISIBLE
            rlImage!!.visibility = View.GONE
            imvAddMore!!.visibility = View.GONE
        }else if(imageList.size in 1..2){
            rvImage!!.visibility = View.VISIBLE
            rlImage!!.visibility = View.GONE
            imvAddMore!!.visibility = View.VISIBLE
        }else{
            rvImage!!.visibility = View.GONE
            rlImage!!.visibility = View.VISIBLE
            imvAddMore!!.visibility = View.GONE
        }

        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImage!!.layoutManager = layoutManager1
        rvImage!!.setHasFixedSize(true)

        imageListRVAdapter = ImageListRVAdapter(mActivity, onClickListener, imageList)
        rvImage!!.adapter = imageListRVAdapter

        val adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            unitNameList
        )
        spinnerUnit.adapter = adapter
        adapter.notifyDataSetChanged()

        imvClose.setOnClickListener {
            dialog!!.dismiss()
        }
        rlImage!!.setOnClickListener {
            if (checkingPermissionIsEnabledOrNot()) {
                showImageSelectionDialog()
            } else {
                requestMultiplePermission()
            }
        }
        imvAddMore!!.setOnClickListener {
            if (checkingPermissionIsEnabledOrNot()) {
                showImageSelectionDialog()
            } else {
                requestMultiplePermission()
            }
        }

        btnAdd.setOnClickListener {
            if (isValidFormData(
                    edtName.text.toString(),
                    spinnerUnit.selectedItem.toString(),
                    edtQty.text.toString()
                )
            ) {

                if(product == null){

                    addOrEditItemThroughAPI(
                        0,
                        edtName.text.toString(),
                        unitList.get(spinnerUnit.selectedItemPosition),
                        edtQty.text.toString()
                    )

                }else{

                    addOrEditItemThroughAPI(
                        product.ProductId,
                        edtName.text.toString(),
                        unitList.get(spinnerUnit.selectedItemPosition),
                        edtQty.text.toString()
                    )
                }

                dialog.dismiss()
            }
        }
    }


    private fun addOrEditItemThroughAPI(
        productId : Int,
        name: String,
        unit: Unit,
        qty: String
    ){

        val image1 = UtilityMethod.imageEncoder(imageList[0])
        val image2 = UtilityMethod.imageEncoder(imageList[1])
        val image3 = UtilityMethod.imageEncoder(imageList[2])

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("ProductId", productId)
        rootObject.addProperty("ProductName", name)
        rootObject.addProperty("CategoryId", parentCategory.PKID)
        rootObject.addProperty("Qty", qty)
        rootObject.addProperty("UnitId", unit.Id)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Published", "true")
        rootObject.addProperty("Image1", image1)
        rootObject.addProperty("Image2", image2)
        rootObject.addProperty("Image3", image3)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddSellItemResponse> = apiService1.addSellItem(gsonObject)
        call1.enqueue(object : Callback<AddSellItemResponse?> {
            override fun onResponse(
                call: Call<AddSellItemResponse?>,
                response: Response<AddSellItemResponse?>
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

            override fun onFailure(call: Call<AddSellItemResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

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
            this@ItemListActivity, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), RequestPermissionCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.isNotEmpty()) {
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
                        this@ItemListActivity,
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

    }

    private fun showImageSelectionDialog() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@ItemListActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                EasyImage.openCameraForImage(this@ItemListActivity, 100)
            } else if (options[item] == "Choose from Gallery") {
                EasyImage.openGallery(this@ItemListActivity, 200)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this@ItemListActivity,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {
                    if (type == 100) {
                        val compressImage: String? =
                            UtilityMethod.compressImage(mActivity!!, imageFiles[0].absolutePath)
                        //   if (compressImage != null) fileLogo = File(compressImage)
                        rlImage!!.visibility = View.GONE
                        rvImage!!.visibility = View.VISIBLE
                        imvAddMore!!.visibility = View.VISIBLE
                        imageList.add(imageFiles[0].absolutePath)
                        imageListRVAdapter!!.notifyDataSetChanged()
                    } else {
                        val compressImage: String? =
                            UtilityMethod.compressImage(mActivity!!, imageFiles[0].absolutePath)
                        //   if (compressImage != null) fileLogo = File(compressImage)
                        rlImage!!.visibility = View.GONE
                        rvImage!!.visibility = View.VISIBLE
                        imvAddMore!!.visibility = View.VISIBLE
                        imageList.add(imageFiles[0].absolutePath)
                        imageListRVAdapter!!.notifyDataSetChanged()
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
                            EasyImage.lastlyTakenButCanceledPhoto(this@ItemListActivity)
                        photoFile?.delete()
                    }
                }
            })
    }

    private fun isValidFormData(
        name: String,
        unit: String,
        qty: String
    ): Boolean {

        if (TextUtils.isEmpty(name)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_full_name))
            return false
        }

        if (TextUtils.isEmpty(unit)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_unit_value))
            return false
        }

        if (TextUtils.isEmpty(qty)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_quantity))
            return false
        }

        if(imageList.size != 3){

            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.label_add_item_images_count_warning)
            )

            return false
        }


        /* if (!UtilityMethod.isValidEmail(email)) {
             UtilityMethod.showToastMessageError(
                 mActivity!!,
                 getString(R.string.enter_valid_email_id)
             )
             return false
         }*/

        /*  val pattern: Pattern
          val regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
          pattern = Pattern.compile(regex)
  */
        /*    if (!pattern.matcher(password).matches()) {
                UtilityMethod.showToastMessageError(
                    mActivity!!,
                    getString(R.string.password_should_contain_char_digit_special)
                )
                return false
            }*/

        return true
    }

}