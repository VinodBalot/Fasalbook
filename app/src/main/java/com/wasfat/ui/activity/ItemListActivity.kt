package com.wasfat.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
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
import com.wasfat.R
import com.wasfat.databinding.ActivityFoodGainBinding
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.ItemRVAdapter
import com.wasfat.utils.UtilityMethod
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

class ItemListActivity : BaseBindingActivity() {

    var binding: ActivityFoodGainBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<String> = ArrayList()
    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null
    var rlImage: RelativeLayout? = null
    var rvImage: RecyclerView? = null
    var imvAddMore: ImageView? = null

    //  var viewModel: VendorViewModel? = null
    val RequestPermissionCode = 7

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ItemListActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
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
    }

    override fun initializeObject() {
        onClickListener = this
        setAdapter()
    }

    private fun setAdapter() {
        val layoutManager1 = LinearLayoutManager(this)
        binding!!.rvProduct.layoutManager = layoutManager1
        binding!!.rvProduct.setHasFixedSize(true)
        val itemRVAdapter = ItemRVAdapter(mActivity, onClickListener)
        binding!!.rvProduct.adapter = itemRVAdapter
    }


    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.fabAdd.setOnClickListener(onClickListener)
    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.fabAdd -> {
                callAddFoodGrainsDialog()
            }
            R.id.llMain -> {
                ItemDetailsActivity.startActivity(mActivity!!, null, false)
            }
        }
    }

    private fun callAddFoodGrainsDialog() {
        val view = layoutInflater.inflate(R.layout.bottomsheet_dialog_add_food_gain, null)
        val imvClose = view.findViewById(R.id.imvClose) as ImageView
        imvAddMore = view.findViewById(R.id.imvAddMore) as ImageView
        val edtName = view.findViewById(R.id.edtName) as EditText
        val edtUnit = view.findViewById(R.id.edtUnit) as EditText
        val edtQty = view.findViewById(R.id.edtQty) as EditText
        rlImage = view.findViewById(R.id.rlImage) as RelativeLayout
        rvImage = view.findViewById(R.id.rvImage) as RecyclerView
        val edtSpecification = view.findViewById(R.id.edtSpecification) as EditText
        val btnAdd = view.findViewById(R.id.btnAdd) as Button
        val dialog = BottomSheetDialog(mActivity!!)
        dialog!!.setContentView(view)
        dialog!!.show()


        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImage!!.layoutManager = layoutManager1
        rvImage!!.setHasFixedSize(true)
        imageListRVAdapter = ImageListRVAdapter(mActivity, onClickListener, imageList)
        rvImage!!.adapter = imageListRVAdapter

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
                    edtUnit.text.toString(),
                    edtQty.text.toString()
                )
            ) {
                dialog.dismiss()
            }
        }
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