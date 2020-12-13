package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import com.wasfat.R
import com.wasfat.databinding.ActivitySelectLanguageBinding
import com.wasfat.ui.activity.authentication.LoginActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.utils.UtilityMethod


class SelectLanguageActivity : BaseBindingActivity() {

    var binding: ActivitySelectLanguageBinding? = null
    var onClickListener: View.OnClickListener? = null

    companion object {

        fun startActivity(
            activity: Activity,
            isClear: Boolean
        ) {
            val intent = Intent(activity, SelectLanguageActivity::class.java)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_language)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
    }


    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btnSelectLanguage.setOnClickListener(onClickListener)


        binding!!.rgLanguage.setOnCheckedChangeListener { group, checkedId ->
            val rb = findViewById<View>(checkedId) as RadioButton
            if (rb.id == R.id.rbEnglish) {
                binding!!.btnSelectLanguage.setBackgroundResource(R.drawable.bg_round_green)
                binding!!.btnSelectLanguage.isClickable = true
                binding!!.btnSelectLanguage.isEnabled = true
                sessionManager!!.language = "1"
                UtilityMethod.setLocate("1", baseContext)
            } else {
                binding!!.btnSelectLanguage.setBackgroundResource(R.drawable.bg_round_green)
                binding!!.btnSelectLanguage.isClickable = true
                binding!!.btnSelectLanguage.isEnabled = true
                sessionManager!!.language = "2"
                UtilityMethod.setLocate("2", baseContext)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.btnSelectLanguage -> {
                LoginActivity.startActivity(mActivity!!, null, false)
            }
        }
    }
}