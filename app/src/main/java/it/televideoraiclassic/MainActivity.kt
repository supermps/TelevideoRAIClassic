
package it.televideoraiclassic

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var televideoImage: ImageView
    private lateinit var pageInput: EditText

    private var currentPage = 100

    private fun pageUrl(page: Int): String =
        "https://www.televideo.rai.it/televideo/pub/tt4web/Nazionale/16_9_page-$page.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        val header = TextView(this).apply {
            text = "TELEVIDEO RAI"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.YELLOW)
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
            setTextColor(Color.WHITE)
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
            textSize = 11f
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
            setOnClickListener {
                if (currentPage > 100) {
                    loadPage(currentPage - 1)
                } else {
                    showMessage("Pagina minima: 100")
                }
            }
        }, LinearLayout.LayoutParams(arrowWidth, arrowHeight))

        navigation.addView(Button(this).apply {
            text = ">"
            textSize = 24f
            setOnClickListener {
                if (currentPage < 899) {
                    loadPage(currentPage + 1)
                } else {
                    showMessage("Pagina massima: 899")
                }
            }
        }, LinearLayout.LayoutParams(arrowWidth, arrowHeight))

        root.addView(navigation)


televideoImage = ImageView(this).apply {
    setBackgroundColor(Color.BLACK)
    scaleType = ImageView.ScaleType.FIT_CENTER
    adjustViewBounds = true
}

root.addView(
    televideoImage,
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

    webView.settings.apply {
        javaScriptEnabled = false
        domStorageEnabled = false
        useWideViewPort = true
        loadWithOverviewMode = true
        builtInZoomControls = true
        displayZoomControls = false
    }

    webView.setBackgroundColor(android.graphics.Color.BLACK)

    webView.loadUrl(pageUrl(page))
}


    private fun openTypedPage() {
        val page = pageInput.text.toString()
            .trim()
            .toIntOrNull()

        if (page == null || page !in 100..899) {
            showMessage("Inserisci una pagina da 100 a 899")
            return
        }

        loadPage(page)
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
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
