package com.lu.magic.module.main.webview

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lu.magic.module.main.Contracts
import com.lu.magic.ui.BaseActivity
import com.lu.magic.util.log.LogUtil

class WebViewActivity : BaseActivity() {
    private var webUrl: String = URL_LOCAL_ABOUT
    private lateinit var webView: WebView

    companion object {
        const val URL_LOCAL_ABOUT = Contracts.URI_LOCAL_ABOUT

        @JvmStatic
        fun start(context: Context, webUrl: String) {
            val intent = Intent(context, WebViewActivity::class.java)
//            intent.putExtra("webUrl", webUrl)
            intent.putExtra("webUrl", "http://192.168.3.27:8000/about.html")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        WebView.setWebContentsDebuggingEnabled(true)
        val contentView = FrameLayout(this).also {
            it.addView(webView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        contentView.fitsSystemWindows = true
        ViewCompat.setOnApplyWindowInsetsListener(contentView, object : OnApplyWindowInsetsListener {
            override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
                val systemInsets = WindowInsetsCompat.Type.systemBars()
                val systemWindowInsetBottom = insets.getInsets(systemInsets).bottom
                LogUtil.d("systemWindowInsetBottom: $systemWindowInsetBottom")
                return insets
            }

        })
        setContentView(contentView)
        webUrl = intent.getStringExtra("webUrl") ?: URL_LOCAL_ABOUT

        //setting webview

        webView.settings.let {
            it.allowContentAccess = true
            it.allowFileAccess = true
            it.blockNetworkImage = false
            it.databaseEnabled = true
            it.domStorageEnabled = true
            it.javaScriptEnabled = true
            it.mediaPlaybackRequiresUserGesture = true
        }
        webView.webViewClient = MagicWebViewClient()
        webView.webChromeClient = MagicChromeClient()

        webView.addJavascriptInterface(
            MagicJavaScriptInterface(JsInterfaceProxy()),
            "Magic"
        )
        webView.loadUrl(webUrl)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    inner class JsInterfaceProxy : JsInterface {
        override fun goBack() {
            finishAfterTransition()
        }
    }

    class MagicWebViewClient : WebViewClient() {
        private var proceedReceivedSslError = false
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError?) {
            if (!proceedReceivedSslError) {
                AlertDialog.Builder(view.context).setNegativeButton("继续") { dialog, which ->
                    handler?.proceed()
                    proceedReceivedSslError = true
                }.setNeutralButton("取消") { dialog, which ->
                    super.onReceivedSslError(view, handler, error)
                }.show()
            } else {
                handler?.proceed()
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            if (URLUtil.isValidUrl(url)) {
                return super.shouldOverrideUrlLoading(view, request)
            }
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            LogUtil.d("onPageFinished：$url")
        }

    }

    class MagicChromeClient : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            if (consoleMessage != null) {
                LogUtil.d("${consoleMessage.messageLevel()} ${consoleMessage.message()} ${consoleMessage.sourceId()} ${consoleMessage.lineNumber()}")
            }
            return super.onConsoleMessage(consoleMessage)
        }
    }

}