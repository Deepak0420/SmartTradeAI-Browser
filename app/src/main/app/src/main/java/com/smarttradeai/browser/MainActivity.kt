package com.smarttradeai.browser

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartTradeBrowser()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SmartTradeBrowser() {
    val darkBackground = Color(0xFF080D19)
    val panelColor = Color(0xFF151D2E)
    val accentColor = Color(0xFF00D9A6)

    var address by remember { mutableStateOf("https://www.google.com") }
    var currentUrl by remember { mutableStateOf("https://www.google.com") }
    var pageTitle by remember { mutableStateOf("SmartTradeAI Browser") }
    var webView by remember { mutableStateOf<WebView?>(null) }

    fun openPage(input: String) {
        val value = input.trim()
        if (value.isEmpty()) return

        val url = when {
            value.startsWith("http://") ||
                value.startsWith("https://") -> value
            value.contains(".") && !value.contains(" ") ->
                "https://$value"
            else ->
                "https://www.google.com/search?q=" +
                    java.net.URLEncoder.encode(value, "UTF-8")
        }

        address = url
        webView?.loadUrl(url)
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "S",
                color = accentColor,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "SmartTradeAI",
                    color = Color.White,
                    fontSize = 19.sp
                )
                Text(
                    text = "INTELLIGENT BROWSER",
                    color = accentColor,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "AI",
                color = accentColor,
                fontSize = 17.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .background(panelColor, RoundedCornerShape(16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text("Search or enter website")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { openPage(address) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = accentColor
                )
            )

            TextButton(
                onClick = { openPage(address) }
            ) {
                Text("GO", color = accentColor)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = {
                if (webView?.canGoBack() == true) webView?.goBack()
            }) {
                Text("‹ Back", color = Color.White)
            }

            TextButton(onClick = {
                if (webView?.canGoForward() == true) webView?.goForward()
            }) {
                Text("Forward ›", color = Color.White)
            }

            TextButton(onClick = {
                webView?.reload()
            }) {
                Text("↻ Refresh", color = Color.White)
            }

            TextButton(onClick = {
                openPage("https://www.google.com")
            }) {
                Text("⌂ Home", color = accentColor)
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true

                    webChromeClient = WebChromeClient()

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(
                            view: WebView?,
                            url: String?
                        ) {
                            super.onPageFinished(view, url)
                            url?.let {
                                currentUrl = it
                                address = it
                            }
                            pageTitle = view?.title ?: "SmartTradeAI Browser"
                        }
                    }

                    webView = this
                    loadUrl("https://www.google.com")
                }
            },
            update = {
                webView = it
            }
        )
    }
}
