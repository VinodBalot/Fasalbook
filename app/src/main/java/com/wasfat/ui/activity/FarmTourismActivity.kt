package com.wasfat.ui.activity

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
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityAgricultureBinding
import com.wasfat.databinding.ActivityFarmTourismBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
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

class FarmTourismActivity : BaseBindingActivity() {

    var binding: ActivityFarmTourismBinding? = null
    var onClickListener: View.OnClickListener? = null

    var selectedImageFilePath : String? = null

    companion object {
        fun startActivity(activity: Activity, type : BuySellType, isClear: Boolean) {
            val intent = Intent(activity, FarmTourismActivity::class.java)
            intent.putExtra("type",type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_farm_tourism)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
    }

    override fun setListeners() {
        binding!!.imvClose.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener (onClickListener)
        binding!!.imvRemoveImage.setOnClickListener(onClickListener)
        binding!!.btnAddFarmTourism.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvClose -> {
                finish()
            }
            R.id.imvRemoveImage -> {
                selectedImageFilePath = null
                binding!!.rvImage.visibility = View.GONE
                binding!!.rlImage.visibility = View.VISIBLE
            }
            R.id.rlImage -> {
                if (checkingPermissionIsEnabledOrNot()) {
                    showImageSelectionDialog()
                } else {
                    requestMultiplePermission()
                }
            }
            R.id.btnAddFarmTourism -> {

                Log.d("TAG", "onClick: BTN CLICKED")

                Log.d("TAG", "onClick: " + isValidFormData(
                    binding!!.edtFarmName.text.toString(),
                    binding!!.edtAddress.text.toString(),
                    binding!!.edtContactNumber.text.toString(),
                    binding!!.edtFacilities.text.toString(),
                    binding!!.edtAtraction.text.toString()
                ))

                if (isValidFormData(
                        binding!!.edtFarmName.text.toString(),
                        binding!!.edtAddress.text.toString(),
                        binding!!.edtContactNumber.text.toString(),
                        binding!!.edtFacilities.text.toString(),
                        binding!!.edtAtraction.text.toString()
                    )
                ) {

                    selectedImageFilePath?.let { UtilityMethod.imageEncoder(it) }?.let {
                        addFarmTourismItem(
                            binding!!.edtFarmName.text.toString(),
                            binding!!.edtAddress.text.toString(),
                            binding!!.edtContactNumber.text.toString(),
                            binding!!.edtFacilities.text.toString(),
                            binding!!.edtAtraction.text.toString(),
                            binding!!.cbPublished.isChecked,
                            it
                        )
                    }
                }
            }
        }
    }

    private fun addFarmTourismItem(
        farmName: String,
        address: String,
        contactNumber: String,
        facilities: String,
        attractions: String,
        published : Boolean,
        imageBase64 : String
    ) {


        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("FarmName", farmName)
        rootObject.addProperty("Address", address)
        rootObject.addProperty("ContactNo", contactNumber)
        rootObject.addProperty("Facilities", facilities)
        rootObject.addProperty("Attraction",attractions)
        rootObject.addProperty("lat", 0)
        rootObject.addProperty("lmg", 0)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Published", published)
        rootObject.addProperty("Image", imageBase64)


        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddFarmItemResponse> = apiService1.addFarmTourismItem(gsonObject)
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

    private fun isValidFormData(
        farmName: String,
        address: String,
        contactNumber: String,
        facilities: String,
        attractions: String
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

        if(selectedImageFilePath == null){
            UtilityMethod.showToastMessageError(mActivity!!,getString(R.string.select_farm_image))
            return false
        }

        return true
    }


    private fun showImageSelectionDialog() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@FarmTourismActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                EasyImage.openCameraForImage(this@FarmTourismActivity, 100)
            } else if (options[item] == "Choose from Gallery") {
                EasyImage.openGallery(this@FarmTourismActivity, 200)
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
            this@FarmTourismActivity, arrayOf(
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
                        this@FarmTourismActivity,
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
            this@FarmTourismActivity,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {

                    Log.d("TAG", "onImagesPicked: " + selectedImageFilePath + type)
                    if (type == 100) {

                        selectedImageFilePath = imageFiles[0].absolutePath
                        Log.d("TAG", "onImagesPicked: " + selectedImageFilePath)
                        binding!!.imvImage.setImageURI(imageFiles[0].absolutePath.toUri())
                        binding!!.rvImage.visibility = View.VISIBLE
                        binding!!.rlImage.visibility = View.GONE

                    } else {
                        selectedImageFilePath = imageFiles[0].absolutePath
                        Log.d("TAG", "onImagesPicked: " + selectedImageFilePath)
                        binding!!.imvImage.setImageURI(imageFiles[0].absolutePath.toUri())
                        binding!!.rvImage.visibility = View.VISIBLE
                        binding!!.rlImage.visibility = View.GONE
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
                            EasyImage.lastlyTakenButCanceledPhoto(this@FarmTourismActivity)
                        photoFile?.delete()
                    }
                }
            })
    }


}