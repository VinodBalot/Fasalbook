package com.wasfat.ui.activity.buyAndSell.landscapeAndGardening

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityAddLandscapeBinding
import com.wasfat.databinding.ActivityEditLandscapeBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AddFarmItemResponse
import com.wasfat.ui.pojo.UserLandscapeProduct
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditLandscapeActivity : BaseBindingActivity() {

    var binding: ActivityEditLandscapeBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    private lateinit var userLandscapeProduct: UserLandscapeProduct
    private lateinit var parentCategoryId: String

    companion object {

        fun startActivity(
            activity: Activity,
            categoryId: String,
            userLandscapeProduct: UserLandscapeProduct,
            isClear: Boolean
        ) {
            val intent = Intent(activity, EditLandscapeActivity::class.java)
            intent.putExtra("categoryId", categoryId)
            intent.putExtra("userLandscapeProduct", userLandscapeProduct)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_landscape)
        binding!!.lifecycleOwner = this
    }


    override fun createActivityObject() {
        mActivity = this

        userLandscapeProduct = intent.getStringExtra("userLandscapeProduct") as UserLandscapeProduct
        parentCategoryId = intent.getStringExtra("categoryId").toString()

    }

    override fun initializeObject() {
        onClickListener = this

        editItemSetup()
    }


    private fun editItemSetup() {

        //Edit current Item
        binding!!.txtDialogTitle.text = getString(R.string.label_edit_item_dialog_title)
        binding!!.edtProductName.setText(userLandscapeProduct.ProductName)
        binding!!.edtSpecification.setText(userLandscapeProduct.ProductSmallDesc)

        userLandscapeProduct.ImageList.forEach {
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


    override fun setListeners() {
        binding!!.imvClose.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener(onClickListener)
        binding!!.imvAddMore.setOnClickListener(onClickListener)
        binding!!.btnAddLandscape.setOnClickListener(onClickListener)
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
            R.id.btnAddLandscape -> {
                if (isValidFormData(
                        binding!!.edtProductName.text.toString(),
                        binding!!.edtProductName.text.toString()
                    )
                ) {
                    addLandscapeItemThroughAPI(
                        binding!!.edtProductName.text.toString(),
                        binding!!.edtProductName.text.toString()
                    )
                }
            }
            R.id.imvRemoveImage -> {
                removeImageSelection(view.tag as Int)
            }
        }

    }

    private fun addLandscapeItemThroughAPI(
        name: String,
        specification: String
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

        if (UtilityMethod.isLocalPath(imageList[1])) {

            image2 = UtilityMethod.imageEncoder(imageList[1])

        }

        if (UtilityMethod.isLocalPath(imageList[2])) {

            image3 = UtilityMethod.imageEncoder(imageList[2])

        }

        rootObject.addProperty("ProductId", userLandscapeProduct.ProductId)
        rootObject.addProperty("ProductName", name)
        rootObject.addProperty("Specification", specification )
        rootObject.addProperty("CategoryId", parentCategoryId)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Published", binding!!.cbPublished.isChecked)
        rootObject.addProperty("Image1", image1)
        rootObject.addProperty("Image2", image2)
        rootObject.addProperty("Image3", image3)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddFarmItemResponse> = apiService1.addEditLandscapeItem(gsonObject)
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

    private fun removeImageSelection(position: Int) {

        imageList.removeAt(position)
        imageListRVAdapter!!.notifyItemRemoved(position)
        imageListRVAdapter!!.notifyItemRangeChanged(position, imageList.size)
        imageListRVAdapter!!.notifyDataSetChanged()

        setVisibiltyForImageSelection()

    }

    private fun setVisibiltyForImageSelection() {

        if (imageList.size >= 3) {
            binding!!.rvImage.visibility = View.VISIBLE
            binding!!.rlImage.visibility = View.GONE
            binding!!.imvAddMoreLayout.visibility = View.GONE
        } else if (imageList.size in 1..2) {
            binding!!.rvImage.visibility = View.VISIBLE
            binding!!.rlImage.visibility = View.GONE
            binding!!.imvAddMoreLayout.visibility = View.VISIBLE
        } else {
            binding!!.rvImage.visibility = View.GONE
            binding!!.rlImage.visibility = View.VISIBLE
            binding!!.imvAddMoreLayout.visibility = View.GONE
        }

    }

    private fun isValidFormData(
        name: String,
        specification: String
    ): Boolean {

        if (TextUtils.isEmpty(name)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_full_name))
            return false
        }

        if (TextUtils.isEmpty(specification)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_unit_value))
            return false
        }

        if (imageList.size != 3) {

            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.label_add_item_images_count_warning)
            )

            return false
        }

        return true
    }

    private fun showImageSelectionDialog() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder( mActivity!!)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                EasyImage.openCameraForImage( mActivity!!, 100)
            } else if (options[item] == "Choose from Gallery") {
                EasyImage.openGallery( mActivity!!, 200)
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
            mActivity!!,
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
                            EasyImage.lastlyTakenButCanceledPhoto( mActivity!!)
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
                        mActivity!!,
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

    }


}