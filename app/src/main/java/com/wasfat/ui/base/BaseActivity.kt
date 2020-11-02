package com.wasfat.ui.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wasfat.R
import com.wasfat.utils.UtilityMethod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity() : AppCompatActivity() {
    public var isBackground = false
    open val RC_SIGN_IN = 9001
    var currentActivity: Context? = null
    protected var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = this
    }
    protected override fun onResume() {
        super.onResume()
        isBackground = false
        currentActivity = this
    }

    protected override fun onPause() {
        super.onPause()
        UtilityMethod.hideKeyboard(this)
        isBackground = true
    }


    fun changeFragment(fragment: Fragment, isAddToBack: Boolean) {
        this.fragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, fragment.javaClass.name)
        if (isAddToBack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }
}