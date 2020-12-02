package com.wasfat.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
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
import com.wasfat.databinding.ActivityUploadIdeasBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AddWriteUpIdeaResponse
import com.wasfat.ui.pojo.Category
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadIdeasActivity : BaseBindingActivity() {

    private var isPublished: Int = 0
    var binding: ActivityUploadIdeasBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null
    var imageBase64 = ""

    lateinit var parentCategory: Category

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            isClear: Boolean
        ) {
            val intent = Intent(activity, UploadIdeasActivity::class.java)
            intent.putExtra("category", category)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_ideas)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this
        parentCategory = (intent.getSerializableExtra("category") as? Category)!!
    }

    override fun initializeObject() {
        onClickListener = this
        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.rvImage.layoutManager = layoutManager1
        binding!!.rvImage.setHasFixedSize(true)
        imageListRVAdapter = ImageListRVAdapter(mActivity, onClickListener, imageList)
        binding!!.rvImage.adapter = imageListRVAdapter

    }

    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btShareVideo.setOnClickListener(onClickListener)
        binding!!.btShareText.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener(onClickListener)
        binding!!.imvAddMore.setOnClickListener(onClickListener)
        binding!!.cbPublished.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                isPublished = 1
            } else
                isPublished = 0
        }
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvBack -> {
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
            R.id.imvRemoveImage -> {
                removeImageSelection(view.tag as Int)
            }
            R.id.btShareText -> {

                if (TextUtils.isEmpty(binding!!.edtTitle.text.toString())) {
                    UtilityMethod.showErrorToastMessage(
                        mActivity!!,
                        getString(R.string.label_enter_title)
                    )
                } else if (TextUtils.isEmpty(binding!!.edtShareIdeaText.text.toString())) {
                    UtilityMethod.showErrorToastMessage(
                        mActivity!!,
                        getString(R.string.label_enter_short_description)
                    )
                } else {
                    shareIdeaInText(
                        binding!!.edtTitle.text.toString(),
                        binding!!.edtShareIdeaText.text.toString()
                    )
                }
            }
            R.id.btShareVideo -> {
                shareIdeaVideo()
            }
        }

    }

    private fun shareIdeaVideo() {
        //TODO : Handle Share Videos here
    }

    private fun shareIdeaInText(title: String, shortDescription: String) {
        if (imageList.size != 0) {
            imageList.forEach {
                imageBase64 = UtilityMethod.imageEncoder(it)
            }
        }
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("PKID", "0")
        rootObject.addProperty("CategoryId", parentCategory.PKID)
        rootObject.addProperty("UserId", sessionManager!!.userId)
        rootObject.addProperty("Title", title)
        rootObject.addProperty("Details", shortDescription)
        rootObject.addProperty("Published", isPublished)
        if (imageBase64.isNotEmpty())
            rootObject.addProperty("Image", "1")
        rootObject.addProperty("News", "1")
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<AddWriteUpIdeaResponse> = apiService1.ideasAdd(gsonObject)
        call1.enqueue(object : Callback<AddWriteUpIdeaResponse?> {
            override fun onResponse(
                call: Call<AddWriteUpIdeaResponse?>,
                response: Response<AddWriteUpIdeaResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        Toast.makeText(mActivity!!, "Successful done.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddWriteUpIdeaResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }


    private fun uploadImage(imageBase64: String) {


    }


    private fun removeImageSelection(position: Int) {

        imageList.removeAt(position)
        imageListRVAdapter!!.notifyItemRemoved(position)
        imageListRVAdapter!!.notifyItemRangeChanged(position, imageList.size)
        imageListRVAdapter!!.notifyDataSetChanged()

        setVisibiltyForImageSelection()
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