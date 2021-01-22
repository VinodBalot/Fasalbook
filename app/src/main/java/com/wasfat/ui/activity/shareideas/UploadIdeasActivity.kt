package com.wasfat.ui.activity.shareideas

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.wasfat.R
import com.wasfat.databinding.ActivityUploadIdeasBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AddWriteUpIdeaResponse
import com.wasfat.ui.pojo.Category
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URI
import java.net.URLEncoder

class UploadIdeasActivity : BaseBindingActivity() {

    private var isShare: String = "image"
    private var videoPath: String = ""
    private var isPublished: Int = 0
    var binding: ActivityUploadIdeasBinding? = null
    var onClickListener: View.OnClickListener? = null
    var imageBase64 = ""
    var imageFile: File? = null
    var newCuttingFile: File? = null
    var videoFile: File? = null

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
    }

    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btShareIdea.setOnClickListener(onClickListener)
        binding!!.llUploadImage.setOnClickListener(onClickListener)
        binding!!.llUploadNewsCutting.setOnClickListener(onClickListener)
        binding!!.llUploadVideo.setOnClickListener(onClickListener)
        binding!!.rbUploadImage.setOnClickListener(onClickListener)
        binding!!.rbUploadNewCutting.setOnClickListener(onClickListener)
        binding!!.rbUploadVideo.setOnClickListener(onClickListener)
        binding!!.rlImage.setOnClickListener(onClickListener)
        binding!!.rlUploadNewCutting.setOnClickListener(onClickListener)
        binding!!.imvVideoImage.setOnClickListener(onClickListener)
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
            R.id.btShareIdea -> {
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
                    if (isShare == "image") {
                        if (imageFile != null) {
                            shareIdeaInText(
                                binding!!.edtTitle.text.toString(),
                                binding!!.edtShareIdeaText.text.toString(),
                                imageFile!!,
                                "1"
                            )
                        } else {
                            UtilityMethod.showErrorToastMessage(
                                mActivity!!,
                                getString(R.string.please_upload_image)
                            )
                        }
                    } else if (isShare == "newcutting") {
                        if (newCuttingFile != null) {
                            shareIdeaInText(
                                binding!!.edtTitle.text.toString(),
                                binding!!.edtShareIdeaText.text.toString(),
                                newCuttingFile!!,
                                "2"
                            )
                        } else {
                            UtilityMethod.showErrorToastMessage(
                                mActivity!!,
                                getString(R.string.please_upload_new_cutting)
                            )
                        }


                    } else if (isShare == "video") {
                        if (videoFile != null) {
                            shareIdeaInText(
                                binding!!.edtTitle.text.toString(),
                                binding!!.edtShareIdeaText.text.toString(),
                                videoFile!!,
                                "3"
                            )
                        } else {
                            UtilityMethod.showErrorToastMessage(
                                mActivity!!,
                                getString(R.string.please_upload_video)
                            )
                        }
                    }
                }
            }
            R.id.llUploadImage -> {
                isShare = "image"
                newCuttingFile = null
                videoFile = null
                binding!!.rbUploadImage.isChecked = true
                binding!!.rbUploadNewCutting.isChecked = false
                binding!!.rbUploadVideo.isChecked = false
                binding!!.llMainImage.visibility = View.VISIBLE
                binding!!.llMainVideo.visibility = View.GONE
                binding!!.llMainNewCutting.visibility = View.GONE
            }
            R.id.rbUploadImage -> {
                isShare = "image"
                newCuttingFile = null
                videoFile = null
                binding!!.rbUploadImage.isChecked = true
                binding!!.rbUploadNewCutting.isChecked = false
                binding!!.rbUploadVideo.isChecked = false
                binding!!.llMainImage.visibility = View.VISIBLE
                binding!!.llMainVideo.visibility = View.GONE
                binding!!.llMainNewCutting.visibility = View.GONE
            }
            R.id.llUploadNewsCutting -> {
                isShare = "newcutting"
                imageFile = null
                videoFile = null
                binding!!.rbUploadImage.isChecked = false
                binding!!.rbUploadNewCutting.isChecked = true
                binding!!.rbUploadVideo.isChecked = false
                binding!!.llMainImage.visibility = View.GONE
                binding!!.llMainVideo.visibility = View.GONE
                binding!!.llMainNewCutting.visibility = View.VISIBLE
            }
            R.id.rbUploadNewCutting -> {
                isShare = "newcutting"
                imageFile = null
                videoFile = null
                binding!!.rbUploadImage.isChecked = false
                binding!!.rbUploadNewCutting.isChecked = true
                binding!!.rbUploadVideo.isChecked = false
                binding!!.llMainImage.visibility = View.GONE
                binding!!.llMainVideo.visibility = View.GONE
                binding!!.llMainNewCutting.visibility = View.VISIBLE
            }
            R.id.llUploadVideo -> {
                isShare = "video"
                imageFile = null
                newCuttingFile = null
                binding!!.rbUploadImage.isChecked = false
                binding!!.rbUploadNewCutting.isChecked = false
                binding!!.rbUploadVideo.isChecked = true
                binding!!.llMainImage.visibility = View.GONE
                binding!!.llMainNewCutting.visibility = View.GONE
                binding!!.llMainVideo.visibility = View.VISIBLE
            }
            R.id.rbUploadVideo -> {
                isShare = "video"
                imageFile = null
                newCuttingFile = null
                binding!!.rbUploadImage.isChecked = false
                binding!!.rbUploadNewCutting.isChecked = false
                binding!!.rbUploadVideo.isChecked = true
                binding!!.llMainImage.visibility = View.GONE
                binding!!.llMainNewCutting.visibility = View.GONE
                binding!!.llMainVideo.visibility = View.VISIBLE
            }
            R.id.rlUploadNewCutting -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "file/*"
                startActivityForResult(intent, 101)
            }
            R.id.imvVideoImage -> {
                shareIdeaVideo()
            }
            R.id.rlImage -> {
                if (checkingPermissionIsEnabledOrNot()) {
                    showImageSelectionDialog()
                } else {
                    requestMultiplePermission()
                }
            }
        }

    }

    private fun shareIdeaVideo() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, 1)
    }

    private fun shareIdeaInText(
        title: String,
        shortDescription: String,
        file: File,
        ideatype: String
    ) {

        Log.d("1234", "file : "+file.absolutePath)

        val title = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            title
        )

        val shortDescription = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            shortDescription
        )
        val userId = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            sessionManager!!.userId!!
        )

        val categoryid = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            parentCategory.PKID.toString()
        )

        val ideatype = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            ideatype
        )

        val published = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            isPublished.toString()
        )

        var body: MultipartBody.Part? = null
        if (file != null) {
            Log.d("1234", "file name : " + file!!.name)
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            body =
                MultipartBody.Part.createFormData("file", file.name, requestFile)
        }

        ProgressDialog.showProgressDialog(mActivity!!)

        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<AddWriteUpIdeaResponse> = apiService1.shareIdea(
            userId,
            categoryid,
            ideatype,
            title,
            shortDescription,
            published,
            body
        )
        call1.enqueue(object : Callback<AddWriteUpIdeaResponse?> {
            override fun onResponse(
                call: Call<AddWriteUpIdeaResponse?>,
                response: Response<AddWriteUpIdeaResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        ProgressDialog.hideProgressDialog()
                        UtilityMethod.showToastMessageSuccess(
                            mActivity!!,
                            getString(R.string.label_successful)
                        )
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<AddWriteUpIdeaResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })


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
        if (requestCode === 1 && resultCode === RESULT_OK && null != data) {
            val selectedVideo: Uri? = data.data
            val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
            val cursor: Cursor =
                contentResolver.query(selectedVideo!!, filePathColumn, null, null, null)!!
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            videoPath = cursor.getString(columnIndex)
            videoFile = File(videoPath)
            binding!!.imvVideoImage.setImageBitmap(
                getThumbnailPathForLocalFile(
                        mActivity!!,
                selectedVideo
            )
            );
            cursor.close()

        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val path: String = getRealPathFromURI(this@UploadIdeasActivity, uri)!!
            newCuttingFile = File(URI(path))
        }
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
                        imageFile = imageFiles[0].absoluteFile
                        Glide.with(mActivity!!).load(imageFiles[0].absolutePath)
                            .into(binding!!.imvImage)
                    } else {
                        imageFile = imageFiles[0].absoluteFile
                        Glide.with(mActivity!!).load(imageFiles[0].absolutePath)
                            .into(binding!!.imvImage)
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

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        Log.d("imin", "onClick: in image conversion")
        var cursor: Cursor? = null
        return try {
            val proj =
                arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            Log.d("imin", "onClick: in image conversion try")
            cursor.getString(column_index)
        } finally {
            Log.d("imin", "onClick: in image conversion finally")
            cursor?.close()
        }
    }

    private fun getThumbnailPathForLocalFile(context: Activity, fileUri: Uri?): Bitmap? {
        val fileId: Long = getFileId(context, fileUri)
        return MediaStore.Video.Thumbnails.getThumbnail(
            context.contentResolver,
            fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null
        )
    }

    private fun getFileId(context: Activity, fileUri: Uri?): Long {
        val cursor =
            context.managedQuery(fileUri, null, null, null, null)
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            return cursor.getInt(columnIndex).toLong()
        }
        return 0
    }
}