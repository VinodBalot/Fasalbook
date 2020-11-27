package com.wasfat.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.pojo.AboutAppResponse
import com.wasfat.ui.pojo.ChangePasswordResponse
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.SessionManager
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var drawerLayout: DrawerLayout? = null
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager()
        UtilityMethod.setLocate(sessionManager!!.language,baseContext)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);


        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        initializeView(navView)

        val navController = findNavController(R.id.nav_host_fragment)

        val toggle = ActionBarDrawerToggle(
               this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
           )

           drawerLayout!!.addDrawerListener(toggle)
           toggle.isDrawerIndicatorEnabled = true;
           toggle.syncState()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


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

            StaticPageActivity.startActivity(this,"about",false)

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

        val listItmes = arrayOf( "हिंदी", "English")

        val mBuilder = AlertDialog.Builder(this@HomeActivity)
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listItmes, -1) { dialog, which ->
           if (which == 0) {
                UtilityMethod.setLocate("hi",baseContext)
                recreate()
            }else if (which == 1) {
               UtilityMethod.setLocate("en",baseContext)
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