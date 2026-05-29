package com.yunmemorial.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yunmemorial.app.databinding.ActivityMainBinding

/**
 * 主页面：WebView 封装云纪念 Web 应用
 * 服务器地址：http://47.96.125.42
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = "http://47.96.125.42"
    }

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        setupSwipeRefresh()
        loadUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView
        val settings = webView.settings

        // 基础设置
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.allowFileAccess = true

        // 缩放与视口
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(false)
        settings.displayZoomControls = false
        settings.builtInZoomControls = false

        // 缓存策略
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // 允许 HTTP（已在 Manifest 开启 usesCleartextTraffic）
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // 设置 UA，方便后端识别来自 Android App
        settings.userAgentString = settings.userAgentString + " YunMemorialApp/1.0 Android"

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.progressBar.visibility = View.VISIBLE
                binding.errorLayout.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                if (!isNetworkAvailable()) {
                    showErrorPage()
                }
            }

            // 处理页面内链接跳转（全部在 WebView 内打开）
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return if (url.startsWith("http://47.96.125.42") || url.startsWith(BASE_URL)) {
                    false  // 放行，在 WebView 内加载
                } else {
                    // 其他外链忽略
                    true
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.progressBar.progress = newProgress
            }
        }

        // Cookie 策略
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
        }
    }

    private fun loadUrl() {
        if (isNetworkAvailable()) {
            binding.webView.loadUrl(BASE_URL)
        } else {
            showErrorPage()
        }
    }

    private fun showErrorPage() {
        binding.errorLayout.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE
        binding.retryBtn.setOnClickListener {
            if (isNetworkAvailable()) {
                binding.errorLayout.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
                binding.webView.loadUrl(BASE_URL)
            } else {
                Toast.makeText(this, "网络仍不可用，请检查网络连接", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /** 返回键处理：有历史则后退，否则退出 App */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /** 保存 WebView 状态，防止旋转屏幕丢失页面 */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }
}
