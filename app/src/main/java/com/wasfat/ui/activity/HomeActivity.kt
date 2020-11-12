package com.wasfat.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.RelativeLayout
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
import com.wasfat.R
import com.wasfat.utils.Constants
import com.wasfat.utils.SessionManager
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var drawerLayout: DrawerLayout? = null
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sessionManager = SessionManager()
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        /*   val toggle = ActionBarDrawerToggle(
               this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
           )
           drawerLayout.setDrawerListener(toggle)
           toggle.syncState()
   */


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        initializeView(navView)
    }

    private fun initializeView(navView: NavigationView) {

        var view = navView.inflateHeaderView(R.layout.nav_header_main);
        val rlProfile = view.findViewById<RelativeLayout>(R.id.rlProfile)
        val rlChangePassword = view.findViewById<RelativeLayout>(R.id.rlChangePassword)
        val rlAboutApp = view.findViewById<RelativeLayout>(R.id.rlAboutApp)
        val rlShareApp = view.findViewById<RelativeLayout>(R.id.rlShareApp)
        val rlLogout = view.findViewById<RelativeLayout>(R.id.rlLogout)
        val rlSuggestions = view.findViewById<RelativeLayout>(R.id.rlSuggestions)
        rlChangePassword.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            ChangePasswordActivity.startActivity(this, null, false)
        }
        rlAboutApp.setOnClickListener {
            drawerLayout!!.closeDrawer(Gravity.LEFT)
            val intent1 = Intent(
                this,
                StaticPageActivity::class.java
            )
            intent1.putExtra("pageURL", Constants.PrivacyPolicy)
            intent1.putExtra("title", "About App")
            startActivity(intent1)
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