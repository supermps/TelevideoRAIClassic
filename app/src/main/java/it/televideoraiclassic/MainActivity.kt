
package it.televideoraiclassic

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var pageInput: EditText
    private lateinit var progress: ProgressBar

    private var currentPage = 100

    private fun pageUrl(page: Int): String =
        "https://origintelevideo.rai.it/televideo/pub/solotesto.jsp?pagina=$page"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF000000.toInt())
        }

        val header = TextView(this).apply {
            text = "TELEVIDEO RAI"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(0xFF000000.toInt())
            setBackgroundColor(0xFFFFFF00.toInt())
            setPadding(8, 12, 8, 12)
        }
        root.addView(header)

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(5, 5, 5, 5)
            setBackgroundColor(0xFF202020.toInt())
        }

        pageInput = EditText(this).apply {
            hint = "Pagina"
            setText("100")
            textSize = 18f
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_GO
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFFBBBBBB.toInt())
            setBackgroundColor(0xFF404040.toInt())
            setPadding(10, 0, 10, 0)
        }

        controls.addView(
            pageInput,
            LinearLayout.LayoutParams(0, 48, 1f)
        )

        controls.addView(Button(this).apply {
            text = "VAI"
            setOnClickListener { openTypedPage() }
        })

        controls.addView(Button(this).apply {
            text = "AGGIORNA"
            setTextSize(11f)
            setOnClickListener { loadPage(currentPage) }
        })

        root.addView(controls)

        
val navigation = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL
    gravity = Gravity.CENTER
    setPadding(4, 2, 4, 5)
    setBackgroundColor(0xFF202020.toInt())
}

val arrowWidth =
    (64 * resources.displayMetrics.density).toInt()

val arrowHeight =
    (48 * resources.displayMetrics.density).toInt()

navigation.addView(Button(this).apply {
    text = "<"
    textSize = 24f
    isAllCaps = false
    setOnClickListener {
        if (currentPage > 100) {
            loadPage(currentPage - 1)
        } else {
            Toast.makeText(
                this@MainActivity,
                "Pagina minima: 100",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}, LinearLayout.LayoutParams(arrowWidth, arrowHeight))

navigation.addView(Button(this).apply {
    text = ">"
    textSize = 24f
    isAllCaps = false
    setOnClickListener {
        if (currentPage < 899) {
            loadPage(currentPage + 1)
        } else {
            Toast.makeText(
                this@MainActivity,
                "Pagina massima: 899",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}, LinearLayout.LayoutParams(arrowWidth, arrowHeight))

root.addView(navigation)


        progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            visibility = View.GONE
        }

        root.addView(
            progress,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                4
            )
        )

        webView = WebView(this).apply {
            setBackgroundColor(0xFF000000.toInt())

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    super.onPageFinished(view, url)

                    val css = """
                        html, body {
                            background-color: #000000 !important;
                            color: #FFFFFF !important;
                        }
                        body {
                            color: #FFFFFF !important;
                        }
                        a, a:visited {
                            color: #FFFF00 !important;
                        }
                    """.trimIndent()

                    val js = """
                        (function() {
                            var style = document.createElement('style');
                            style.innerHTML = ${JSONObject.quote(css)};
                            document.head.appendChild(style);
                        })();
                    """.trimIndent()

                    view?.evaluateJavascript(js, null)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {
                    this@MainActivity.progress.progress = newProgress
                    this@MainActivity.progress.visibility =
                        if (newProgress >= 100) View.GONE
                        else View.VISIBLE
                }
            }
        }

        root.addView(
            webView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        pageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                openTypedPage()
                true
            } else {
                false
            }
        }

        setContentView(root)
        loadPage(100)
    }

    private fun loadPage(page: Int) {
        currentPage = page
        pageInput.setText(page.toString())
        webView.loadUrl(pageUrl(page))
    }

    private fun openTypedPage() {
        val page = pageInput.text.toString().trim().toIntOrNull()

        if (page == null || page !in 100..899) {
            Toast.makeText(
                this,
                "Inserisci una pagina da 100 a 899",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        loadPage(page)
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
