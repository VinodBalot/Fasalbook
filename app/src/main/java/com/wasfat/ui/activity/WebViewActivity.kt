package com.wasfat.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.wasfat.R
import com.wasfat.ui.pojo.Event
import com.wasfat.utils.ProgressDialog

class WebViewActivity : AppCompatActivity() {

    lateinit var url: String
    lateinit var title : String
    lateinit var webView : WebView


    companion object {

        fun startActivity(activity: Activity, bundle: Bundle, isClear: Boolean) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        ProgressDialog.showProgressDialog(this)

        initialize()

    }

     @SuppressLint("SetJavaScriptEnabled")
     private fun initialize() {

         val bundle = intent.getBundleExtra("bundle") as Bundle

         url = bundle.getString("url").toString()
         title = bundle.getString("title").toString()

        val ivBack = findViewById<ImageView>(R.id.imvBack)
         ivBack.setOnClickListener{
             finish()
         }

         val txtTitle = findViewById<TextView>(R.id.textTitle)
         txtTitle.text = title

         Log.d("WEBVIEW", "initialize: " + url)

         webView = findViewById(R.id.webView)

         webView.webViewClient = MyWebViewClient(this)
         webView.loadUrl(url)

         val webSettings = webView.settings
         webSettings.javaScriptEnabled = true

    }


    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            ProgressDialog.hideProgressDialog()
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            ProgressDialog.hideProgressDialog()
            super.onPageFinished(view, url)
        }
    }

}