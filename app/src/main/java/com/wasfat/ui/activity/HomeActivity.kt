package com.wasfat.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.wasfat.R
import com.wasfat.ui.activity.authentication.ChangePasswordActivity
import com.wasfat.ui.activity.authentication.LoginActivity
import com.wasfat.utils.SessionManager
import com.wasfat.utils.UtilityMethod


class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var drawerLayout: DrawerLayout? = null
    var sessionManager: SessionManager? = null
    var toolbar: Toolbar? = null
    var imgMenu: ImageView? = null
    var imgSearch: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager()
        UtilityMethod.setLocate(sessionManager!!.language, baseContext)

        drawerLayout = findViewById(R.id.drawer_layout)
        imgMenu = findViewById(R.id.imgMenu)
        imgSearch = findViewById(R.id.imgSearch)
        val toggle =
            ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
        drawerLayout!!.setDrawerListener(toggle)
        toggle.syncState()

        val navView: NavigationView = findViewById(R.id.nav_view)
        initializeView(navView)

        imgMenu!!.setOnClickListener {
            drawerLayout!!.openDrawer(Gravity.LEFT)
        }

        imgSearch!!.setOnClickListener {
            SearchActivity.startActivity(this, null, false)
        }
    }

    private fun initializeView(navView: NavigationView) {

        var view = navView.inflateHeaderView(R.layout.nav_header_main);
        val rlProfile = view.findViewById<RelativeLayout>(R.id.rlProfile)
        val rlChangePassword = view.findViewById<RelativeLayout>(R.id.rlChangePassword)
        val rlChangeLanguage = view.findViewById<RelativeLayout>(R.id.rlChangeLanguage)
        val rlAboutApp = view.findViewById<RelativeLayout>(R.id.rlAboutApp)
        val rlShareApp = view.findViewById<RelativeLayout>(R.id.rlShareApp)
        val rlLogout = view.findViewById<RelativeLayout>(R.id.rlLogout)
        val rlSuggestions = view.findViewById<RelativeLayout>(R.id.rlSuggestions)
        val rlPrivacyPolicy = view.findViewById<RelativeLayout>(R.id.rlPrivacyPolicy)

        rlPrivacyPolicy.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            val bundle = Bundle()
            bundle.putString("title", "Privacy Policy")
            bundle.putString("url", "http://fasalbook.com/privacy-policy.aspx")
            WebViewActivity.startActivity(this, bundle, false)
        }

        rlChangeLanguage.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            showChangeLang()
        }


        rlChangePassword.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            ChangePasswordActivity.startActivity(this, null, false)
        }
        rlAboutApp.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            StaticPageActivity.startActivity(this, "about", false)

        }

        rlSuggestions.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            FeedbackActivity.startActivity(this, null, false)
        }

        rlProfile.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            ChangeProfileActivity.startActivity(this, null, false)
        }


        rlShareApp.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Welcome to Fasalbook");
            startActivity(Intent.createChooser(shareIntent, "Send to"))
        }
        rlLogout.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            logoutDialog()
        }

    }


    private fun showChangeLang() {
        val listItmes = arrayOf("हिंदी", "English")
        val mBuilder = AlertDialog.Builder(this@HomeActivity)
        mBuilder.setTitle(getString(R.string.label_choose_language_dialog))

        var checkedItem: Int = -1
        if (sessionManager!!.language == "1") {
            checkedItem = 1
        } else if (sessionManager!!.language == "2") {
            checkedItem = 0
        }
        mBuilder.setSingleChoiceItems(listItmes, checkedItem) { dialog, which ->
            if (which == 0) {
                UtilityMethod.setLocate("2", baseContext)
                recreate()
            } else if (which == 1) {
                UtilityMethod.setLocate("1", baseContext)
                recreate()
            }
            dialog.dismiss()
        }
        val mDialog = mBuilder.create()

        mDialog.show()

    }

    fun logoutDialog() {
        val dialogBuilder =
            AlertDialog.Builder(this)
        dialogBuilder.setTitle(resources.getString(R.string.app_name))
        dialogBuilder.setMessage(resources.getString(R.string.label_logout_message))
        dialogBuilder.setPositiveButton(
            resources.getString(R.string.label_ok)
        ) { dialogInterface, i ->
            dialogInterface.dismiss()
            sessionManager!!.setLogin(false)
            LoginActivity.startActivity(this, null, false)
            finish()
        }
        dialogBuilder.setNegativeButton(
            resources.getString(R.string.label_cancel)
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        val alertDialog1 = dialogBuilder.create()
        alertDialog1.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}