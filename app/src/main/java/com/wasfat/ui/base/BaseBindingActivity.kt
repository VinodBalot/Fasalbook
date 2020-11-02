package com.wasfat.ui.base

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.os.urservice.ui.base.ToolbarCallback
import com.wasfat.R
import com.wasfat.utils.SessionManager

abstract class BaseBindingActivity : AppCompatActivity(), ToolbarCallback, View.OnClickListener {

    protected var mActivity: AppCompatActivity? = null

    protected var sessionManager: SessionManager? = null

    protected var toolBar: Toolbar? = null

    protected var fragment: Fragment? = null

    protected abstract fun setBinding()

    protected abstract fun createActivityObject()

    protected abstract fun initializeObject()

    protected abstract fun setListeners()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager.getInstance(this)
        createActivityObject()
        setBinding()
        initializeObject()
        setListeners()
    }

    override fun setToolbarCustomTitle(titleKey: String) {

    }

    override fun showUpIconVisibility(isVisible: Boolean) {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(isVisible)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        if (mActivity == null) throw NullPointerException("must create activty object")
    }


    fun changeFragment(fragment: Fragment, isAddToBack: Boolean) {
        this.fragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, fragment.javaClass.name)
        if (isAddToBack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }
}