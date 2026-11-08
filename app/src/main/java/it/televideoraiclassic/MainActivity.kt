
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

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var pageInput: EditText
    private lateinit var progress: ProgressBar

    private fun pageUrl(page: String): String =
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
            setPadding(6, 6, 6, 6)
            setBackgroundColor(0xFF202020.toInt())
        }

        pageInput = EditText(this).apply {
            hint = "Pagina"
            setText("100")
            textSize = 18f
            singleLine = true
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_GO
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFFBBBBBB.toInt())
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
            setOnClickListener { webView.reload() }
        })

        root.addView(controls)

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
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            webViewClient = WebViewClient()

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {
                    progress.progress = newProgress
                    progress.visibility =
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
        webView.loadUrl(pageUrl("100"))
    }

    private fun openTypedPage() {
        val page = pageInput.text.toString().trim()

        if (page.isEmpty() || page.length !in 1..3) {
            Toast.makeText(
                this,
                "Inserisci un numero di pagina valido",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        webView.loadUrl(pageUrl(page))
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
