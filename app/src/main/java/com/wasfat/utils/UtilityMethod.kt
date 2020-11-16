package com.wasfat.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.os.Environment
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.wasfat.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.regex.Pattern

object UtilityMethod {

    /* fun setTitleBarViews(
         mActivity: Activity,
         isImageViewRightVisible: Boolean,
         titleText: String
     ) {
         if (mActivity is HomeActivity) {

             mActivity.binding!!.layoutMain.appBar.imvRight.visibility =
                 if (isImageViewRightVisible) View.VISIBLE else View.GONE
             mActivity.binding!!.layoutMain.appBar.txtTitle.text = titleText
         } else {
             Log.e("Error : ", "Error : mActivity is not instance of HomeActivity")
         }
     }*/

    /*fun handleErrorMessage(
        mActivity: Activity,
        errorMessage: String,
        sessionManager: SessionManager
    ) {
        if (errorMessage == "HTTP 401 Unauthorized") {
            sessionManager.logout()
            SignInActivity.startActivity(mActivity, null, true)
        } else {
            showToastMessageFailed(mActivity, errorMessage)
        }
    }*/


    @SuppressLint("MissingPermission")
    fun hasInternet(context: Context): Boolean {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getTime(time: Long): String {

        val timeStamp = java.sql.Timestamp(time)
        val date: Date = timeStamp
        return DateFormat.format("hh:mm aa", date).toString()
    }

    fun getDate(time: Long): String {

        val timeStamp = java.sql.Timestamp(time)
        val date: Date = timeStamp
        return DateFormat.format("dd-MMM-yyyy", date).toString()
    }

    fun underlineTextView(mTextView: TextView) {
        val content = SpannableString(mTextView.text.toString())
        content.setSpan(UnderlineSpan(), 0, mTextView.text.toString().length, 0)
        mTextView.text = content
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(target).matches();
    }

    fun isValidPassword(target: CharSequence): Boolean {
        return Pattern.compile(
            "^((?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%]).{5,20})"
        ).matcher(target).matches();
    }

    fun setZeroBeforeNine(digit: Int): String {
        return if (digit <= 9) "0$digit" else "" + digit
    }

    fun convertnewDateFormat(date: Date, format: String): String {
        var formattedDate = ""
        try {
            val outputFormat = SimpleDateFormat(format)
            formattedDate = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formattedDate
    }

    fun showToastMessageDefault(activity: Activity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun showToastMessageSuccess(activity: Activity, message: String) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG;
        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_green)

        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        toast.view = toastView
        toast.show()
    }

    @JvmStatic
    fun compressImage(context: Context, imageUri: String): String? {

        // String filePath = getRealPathFromURI(imageUri);
        var scaledBitmap: Bitmap? = null
        val options =
            BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp =
            BitmapFactory.decodeFile(imageUri, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(imageUri, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(
                actualWidth,
                actualHeight,
                Bitmap.Config.ARGB_8888
            )
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageUri)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename: String = getFilename(context).toString()
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    fun imageEncoder(filePath: String): String{

        var base64 : String = ""

        val bytes = File(filePath).readBytes()
        base64 = android.util.Base64.encodeToString(bytes, 0)


        return base64
    }


     fun isLocalPath(p: String): Boolean {

        return !(p.startsWith("http://")
                || p.startsWith("https://"))

    }


    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio =
                Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio =
                Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getFilename(context: Context): String? {
        val file = File(
            Environment.getExternalStorageDirectory().path,
            context.getString(R.string.app_name)
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }


    fun showToastMessageFailed(activity: Activity, message: String) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG;
        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_red)

        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        toast.view = toastView
        toast.show()
    }

    fun showToastMessageError(activity: Activity, message: String) {

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

//        val toast = Toast(activity)
//        toast.duration = Toast.LENGTH_LONG;
//        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
//        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_red)
//
//        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
//        txtMessage.text = message
//
//        toast.view = toastView
//        toast.show()
    }

    @JvmStatic
    fun loadImage(view: ImageView?, imageUrl: String?) {
        Glide.with(view!!.context)
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(ContextCompat.getDrawable(view.context, R.mipmap.ic_launcher))
            .into(view)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED, 0)
    }

    fun isNumeric(str: String): Boolean {
        return str.matches(Regex("-?\\d+(.\\d+)?"))
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    fun formateMilliSeccond(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""
        var minutesString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000)

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        secondsString = if (seconds < 10) "0$seconds" else "" + seconds
        minutesString = if (minutes < 10) "0$minutes" else "" + minutes

        finalTimerString = finalTimerString + minutesString + ":" + secondsString
        return finalTimerString
    }

    fun checLoginValidation(activity: Activity, uName: String, uPass: String): Boolean {
        var makeLogin: Boolean = false
        if (TextUtils.isEmpty(uName) && TextUtils.isEmpty(uPass)) {
            showToastMessageError(activity, "Please enter email address and password")
        } else if (TextUtils.isEmpty(uName)) {
            showToastMessageError(activity, "Please enter your email address")
        } else if (TextUtils.isEmpty(uPass)) {
            showToastMessageError(activity, "Please enter password")
        } else if (!TextUtils.isEmpty(uName) && !isValidEmail(uName.toString())) {
            showToastMessageError(activity, "Please enter valid email address")
        }else if (!TextUtils.isEmpty(uPass) && uPass.toString().length<4) {
            showToastMessageError(activity, "Password should be more then 4 digit")
        }else if (!isValidPassword(uPass.toString())&& uPass.toString().length>=4) {
            makeLogin = true
        }

    return makeLogin
}

    fun multiTextClicked(
        textView: TextView,
        all: String,
        arr_text: Array<String>,
        arr_color: Array<String?>,
        arr_ratio: FloatArray,
        arr_isClick: BooleanArray,
        arr_isUnderLine: BooleanArray,
        textClickListener: TextClickListener
    ) {

        // initialize constructor with all string
        val spannableString = SpannableString(all)
        for (pos in arr_text.indices) {
            // to change ratio of text in percentage
            spannableString.setSpan(
                RelativeSizeSpan(arr_ratio[pos]),
                all.indexOf(arr_text[pos]),
                all.indexOf(arr_text[pos]) + arr_text[pos].length,
                0
            )
            // to change color of text
            spannableString.setSpan(
                ForegroundColorSpan(
                    Color.parseColor(
                        arr_color[pos]
                    )
                ),
                all.indexOf(arr_text[pos]),
                all.indexOf(arr_text[pos]) + arr_text[pos].length,
                0
            )
            if (arr_isClick[pos]) {
                // click specific string of text view
                spannableString.setSpan(
                    ClickSpanText(
                        arr_text[pos],
                        all.indexOf(arr_text[pos]),
                        arr_color[pos],
                        arr_isUnderLine[pos],
                        textClickListener
                    ),
                    all.indexOf(arr_text[pos]),
                    all.indexOf(arr_text[pos]) + arr_text[pos].length,
                    0
                )
            }
        }
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    @JvmStatic
    fun showSuccessToastMessage(activity: Activity, message: String) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG;
        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_green)

        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        toast.view = toastView
        toast.show()
    }

    @JvmStatic
    fun showErrorToastMessage(activity: Activity, message: String) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG;
        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_red)

        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        toast.view = toastView
        toast.show()
    }


    private class ClickSpanText(
        clickString: String,
        private val clickPosition: Int,
        private val color: String?,
        isUnderLine: Boolean,
        mTextClickListener: TextClickListener
    ) :
        ClickableSpan() {
        private val clickString: String
        private val mTextClickListener: TextClickListener
        private val isUnderLine: Boolean
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = isUnderLine
            ds.color = Color.parseColor(color)
        }

        override fun onClick(view: View) {
            mTextClickListener.getClickedString(clickString)
        }

        init {
            this.mTextClickListener = mTextClickListener
            this.clickString = clickString
            this.isUnderLine = isUnderLine
        }
    }


}