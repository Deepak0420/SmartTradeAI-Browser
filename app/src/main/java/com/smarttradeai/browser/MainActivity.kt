
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt

private val Bg = Color(0xFF080D18)
private val Panel = Color(0xFF141E30)
private val Accent = Color(0xFF00E0B0)
private val Muted = Color(0xFF91A0B8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SmartTradeBrowser() }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SmartTradeBrowser() {
    val context = LocalContext.current

    var address by remember { mutableStateOf("https://www.google.com") }
    var pageTitle by remember { mutableStateOf("SmartTradeAI") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var loading by remember { mutableStateOf(false) }
    var assistantOpen by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }
    var desktopMode by remember { mutableStateOf(false) }
    var reply by remember {
        mutableStateOf("नमस्ते! मैं SmartTradeAI Assistant हूँ।")
    }

    var fabX by remember { mutableFloatStateOf(0f) }
    var fabY by remember { mutableFloatStateOf(0f) }

    fun openPage(input: String) {
        val value = input.trim()
        if (value.isBlank()) return

        val url = when {
            value.startsWith("https://") || value.startsWith("http://") -> value
            value.contains(".") && !value.contains(" ") -> "https://$value"
            else -> "https://www.google.com/search?q=" +
                java.net.URLEncoder.encode(value, "UTF-8")
        }

        address = url
        webView?.loadUrl(url)
    }

    BackHandler(enabled = assistantOpen || webView?.canGoBack() == true) {
        if (assistantOpen) assistantOpen = false
        else webView?.goBack()
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Bg,
            surface = Panel
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
        ) {
            Column(Modifier.fillMaxSize()) {

                // Premium header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Accent.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("S", color = Accent, fontSize = 27.sp)
                    }

                    Spacer(Modifier.width(11.dp))

                    Column {
                        Text(
                            "SmartTradeAI",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            "INTELLIGENT BROWSER",
                            color = Accent,
                            fontSize = 9.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.End) {
                        Text("AI CORE", color = Accent, fontSize = 11.sp)
                        Text("BROWSER", color = Muted, fontSize = 9.sp)
                    }
                }

                // Address bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .background(Panel, RoundedCornerShape(16.dp))
                        .padding(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⌕", color = Accent, fontSize = 23.sp)
                    Spacer(Modifier.width(7.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = {
                            Text("Search or enter website", color = Muted)
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
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Accent
                        )
                    )

                    TextButton(onClick = { openPage(address) }) {
                        Text("GO", color = Accent)
                    }
                }

                // Browser toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        if (webView?.canGoBack() == true) webView?.goBack()
                    }) { Text("‹", color = Color.White, fontSize = 20.sp) }

                    TextButton(onClick = {
                        if (webView?.canGoForward() == true) webView?.goForward()
                    }) { Text("›", color = Color.White, fontSize = 20.sp) }

                    TextButton(onClick = { webView?.reload() }) {
                        Text("↻", color = Color.White, fontSize = 20.sp)
                    }

                    TextButton(onClick = {
                        openPage("https://www.google.com")
                    }) { Text("⌂ Home", color = Accent) }

                    TextButton(onClick = {
                        desktopMode = !desktopMode
                        webView?.settings?.useWideViewPort = desktopMode
                        webView?.settings?.loadWithOverviewMode = desktopMode
                        webView?.settings?.userAgentString =
                            if (desktopMode) {
                                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/131.0.0.0 Safari/537.36"
                            } else {
                                null
                            }
                        webView?.reload()
                    }) {
                        Text(
                            if (desktopMode) "Mobile" else "Desktop",
                            color = Accent,
                            fontSize = 11.sp
                        )
                    }
                }

                // Loading indicator
                if (loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Accent,
                        trackColor = Panel
                    )
                }

                // Website area
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadsImagesAutomatically = true
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                            settings.javaScriptCanOpenWindowsAutomatically = true

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(
                                    view: WebView?,
                                    newProgress: Int
                                ) {
                                    loading = newProgress < 100
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(
                                    view: WebView?,
                                    url: String?
                                ) {
                                    super.onPageFinished(view, url)
                                    url?.let { address = it }
                                    pageTitle = view?.title ?: "SmartTradeAI"
                                }
                            }

                            webView = this
                            loadUrl("https://www.google.com")
                        }
                    },
                    update = { webView = it }
                )

                // Bottom navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Panel)
                        .navigationBarsPadding()
                        .padding(vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        openPage("https://www.google.com")
                    }) {
                        Text("⌂\nHome", color = Accent, fontSize = 11.sp)
                    }

                    TextButton(onClick = {
                        assistantOpen = true
                    }) {
                        Text("✦\nAI", color = Accent, fontSize = 11.sp)
                    }

                    TextButton(onClick = {
                        webView?.reload()
                    }) {
                        Text("↻\nRefresh", color = Color.White, fontSize = 11.sp)
                    }

                    TextButton(onClick = {
                        assistantOpen = true
                    }) {
                        Text("◉\nAssistant", color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            // Draggable floating AI button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset {
                        IntOffset(fabX.roundToInt(), fabY.roundToInt())
                    }
                    .padding(bottom = 82.dp, end = 18.dp)
                    .size(58.dp)
                    .background(Accent, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            fabX += drag.x
                            fabY += drag.y
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { assistantOpen = !assistantOpen },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("AI", color = Bg, fontSize = 19.sp)
                }
            }

            // Assistant panel
            if (assistantOpen) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 14.dp, bottom = 145.dp)
                        .fillMaxWidth(0.90f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Panel)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✦", color = Accent, fontSize = 23.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "SmartTrade AI Assistant",
                                color = Color.White,
                                fontSize = 17.sp
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = {
                                assistantOpen = false
                            }) {
                                Text("✕", color = Muted)
                            }
                        }

                        Text(
                            "Current page: $pageTitle",
                            color = Muted,
                            fontSize = 11.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            reply,
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = question,
                            onValueChange = { question = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Ask your assistant...", color = Muted)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Muted
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                reply = if (question.isBlank()) {
                                    "अपना सवाल लिखिए। AI analysis engine अभी connect नहीं है।"
                                } else {
                                    "आपका सवाल मिला: $question\n\nअभी AI engine connect करना बाकी है। इसलिए मैं अभी वास्तविक chart analysis या trading signal नहीं दे सकता।"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Accent,
                                contentColor = Bg
                            )
                        ) {
                            Text("SEND TO AI")
                        }

                        Text(
                            "AI analysis connection pending • No automatic trades",
                            color = Muted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
