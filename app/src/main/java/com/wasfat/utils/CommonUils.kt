package com.wasfat.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast

fun Context.extToastMe(toastMsg:String){
    Toast.makeText(this,toastMsg,Toast.LENGTH_LONG).show()

}

fun Context.extIsValidUserName(targetUserName: String?): String {
    var validMsg = "VALID"
    if (TextUtils.isEmpty(targetUserName)) {
        validMsg = "User Name Can not be blank"
    } else if (targetUserName.toString().contains("@") && !extIsValidEmail(targetUserName.toString())) {
        validMsg = "Please Enter Valid Email Address"
    }else if(targetUserName.toString().length<9) {
        validMsg = "Mobile Number should be in 10 digits"
    }
    return validMsg

}

fun Context.extIsValidUserPassword(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target)
}
fun Context.extIsValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}


