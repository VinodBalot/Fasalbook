package com.wasfat.ui.activity.buyAndSell.agricultureAndFarming

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityEditItemBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.*
import com.wasfat.ui.pojo.Unit
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditAgricultureActivity : BaseBindingActivity() {

    var binding: ActivityEditItemBinding? = null
    var onClickListener: View.OnClickListener? = null


    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null
    var unitList: ArrayList<Unit> = ArrayList()
    var unitNameList: ArrayList<String> = ArrayList()

    lateinit var product: UserProduct
    lateinit var parentCategoryId: String

    companion object {

        fun startActivity(
            activity: Activity,
            product: UserProduct,
            categoryId: String,
            isClear: Boolean
        ) {
            val intent = Intent(activity, EditAgricultureActivity::class.java)
            intent.putExtra("product", product)
            intent.putExtra("categoryId", categoryId)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_item)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        parentCategoryId = intent.getStringExtra("categoryId").toString()

        //Getting parent category from parent
        product = (intent.getSerializableExtra("product") as? UserProduct)!!

    }

    override fun initializeObject() {
        onClickListener = this

        getUnitListFromAPI()

    }

    override fun setListeners() {

        binding!!.imvClose.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener (onClickListener)
        binding!!.imvAddMore.setOnClickListener(onClickListener)
        binding!!.btnAdd.setOnClickListener(onClickListener)

    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvClose -> {
                finish()
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
            R.id.btnAdd -> {
                if (isValidFormData(
                        binding!!.edtName.text.toString(),
                        binding!!.spinnerUnit.selectedItem.toString(),
                        binding!!.edtQty.text.toString(),
                        binding!!.spinnerPriceUnit.selectedItem.toString(),
                        binding!!.edtPrice.text.toString()
                    )
                ) {
                    editItemThroughAPI(
                        product.ProductId,
                        binding!!.edtName.text.toString(),
                        unitList.get(binding!!.spinnerUnit.selectedItemPosition),
                        binding!!.edtQty.text.toString(),
                        unitList.get(binding!!.spinnerPriceUnit.selectedItemPosition),
                        binding!!.edtPrice.text.toString()
                    )

                }
            }
            R.id.imvRemoveImage -> {
                removeImageSelection(view.tag as Int)
            }
        }

    }

    private fun removeImageSelection(position: Int) {

        imageList.removeAt(position)
        imageListRVAdapter!!.notifyItemRemoved(position)
        imageListRVAdapter!!.notifyItemRangeChanged(position, imageList.size)
        imageListRVAdapter!!.notifyDataSetChanged()

        setVisibiltyForImageSelection()

    }

    private fun editItemSetup() {

        val adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            unitNameList
        )
        binding!!.spinnerUnit.adapter = adapter
        binding!!.spinnerPriceUnit.adapter = adapter
        adapter.notifyDataSetChanged()

        //Edit current Item
        binding!!.txtDialogTitle.text = getString(R.string.label_edit_item_dialog_title)
        binding!!.edtName.setText(product.ProductName)
        binding!!.edtSpecification.setText(product.ProductSmallDesc)
        binding!!.edtQty.setText(product.Qty)
        binding!!.edtPrice.setText(product.Rate)
        binding!!.spinnerUnit.setSelection(product.UnitId.toInt())
        binding!!.spinnerPriceUnit.setSelection(product.RateUnitId.toInt())
        binding!!.btnAdd.setText(R.string.label_edit_item_dialog_button)
        product.ImageList.forEach {
            if (it.ImageName.isNotEmpty()) {
                val image = it.Path + "/" + it.ImageName
                imageList.add(image)
            }
        }
        Log.d("TAGImageList", "addOrEditItemDialog: " + imageList)

        setVisibiltyForImageSelection()

        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.rvImage.layoutManager = layoutManager1
        binding!!.rvImage.setHasFixedSize(true)

        imageListRVAdapter = ImageListRVAdapter(mActivity, onClickListener, imageList)
        binding!!.rvImage.adapter = imageListRVAdapter

        }

    private fun setVisibiltyForImageSelection() {
        if (imageList.size == 1) {
            binding!!.rvImage.visibility = View.VISIBLE
            binding!!.rlImage.visibility = View.GONE
            binding!!.imvAddMoreLayout.visibility = View.GONE
        }else {
            binding!!.rvImage.visibility = View.GONE
            binding!!.rlImage.visibility = View.VISIBLE
            binding!!.imvAddMoreLayout.visibility = View.GONE
        }

    }

    private fun editItemThroughAPI(
        productId: Int,
        name: String,
        unit: Unit,
        qty: String,
        priceUnit : Unit,
        price : String
    ) {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        var image1 = ""
        var image2 = ""
        var image3 = ""

        if (UtilityMethod.isLocalPath(imageList[0])) {

            image1 = UtilityMethod.imageEncoder(imageList[0])

        }

        rootObject.addProperty("ProductId", productId)
        rootObject.addProperty("ProductName", name)
        rootObject.addProperty("CategoryId", parentCategoryId )
        rootObject.addProperty("Qty", qty)
        rootObject.addProperty("UnitId", unit.Id)
        rootObject.addProperty("Rate", price)
        rootObject.addProperty("RateUnitId", priceUnit.Id)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Published", "true")
        rootObject.addProperty("Image1", image1)
        rootObject.addProperty("Image2", image2)
        rootObject.addProperty("Image3", image3)

        val jsonParser = JsonParser()
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

    private fun isValidFormData(
        name: String,
        unit: String,
        qty: String,
        priceUnit: String,
        price : String
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

        if (TextUtils.isEmpty(priceUnit)) {
            UtilityMethod.showToastMessageError(mActivity!!, "Please Enter Price unit of Product")
            return false
        }

        if (TextUtils.isEmpty(price)) {
            UtilityMethod.showToastMessageError(mActivity!!, "Please Enter Price of Product")
            return false
        }

        if (imageList.size ==0) {

            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.label_add_item_images_count_warning)
            )

            return false
        }

        return true
    }

    private fun getUnitListFromAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
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
                        editItemSetup()
                        Log.d("UNITS", "onResponse: " + unitList)
                    }
                }
            }

            override fun onFailure(call: Call<UnitListResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }

    private fun showImageSelectionDialog() {

        val options = arrayOf<CharSequence>(getString(R.string.label_image_dialog_item_take_photo), getString(
            R.string.label_image_dialog_item_choose_from_gallery), getString(R.string.label_image_dialog_cancel))
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle(getString(R.string.label_image_dialog_title))
        builder.setItems(options) { dialog, item ->
            if (options[item] == getString(R.string.label_image_dialog_item_take_photo)) {
                EasyImage.openCameraForImage(mActivity!!, 100)
            } else if (options[item] == getString(
                    R.string.label_image_dialog_item_choose_from_gallery)) {
                EasyImage.openGallery(mActivity!!, 200)
            } else if (options[item] ==  getString(R.string.label_image_dialog_cancel)) {
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
            this@EditAgricultureActivity,
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
                            EasyImage.lastlyTakenButCanceledPhoto(this@EditAgricultureActivity)
                        photoFile?.delete()
                    }
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
            this@EditAgricultureActivity, arrayOf(
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
                        this@EditAgricultureActivity,
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

    }

}