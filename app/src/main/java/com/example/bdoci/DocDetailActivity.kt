package com.example.bdoci

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.example.bdoci.models.Doc
import com.example.bdoci.utils.QRUtils
import android.widget.ImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider

class DocDetailActivity : AppCompatActivity() {

    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable {
        val zoomCard = findViewById<View>(R.id.zoomCard)
        val fabZoom = findViewById<View>(R.id.fabZoom)
        val root = zoomCard.parent as ViewGroup
        
        TransitionManager.beginDelayedTransition(root, AutoTransition().apply {
            duration = 300
        })
        
        zoomCard.visibility = View.GONE
        fabZoom.visibility = View.VISIBLE
    }

    private fun resetHideTimer() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 3000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doc_detail)

        // Handle window insets for edge-to-edge
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get views
        val titleText = findViewById<TextView>(R.id.detailTitle)
        val categoryText = findViewById<TextView>(R.id.detailCategory)
        val descText = findViewById<TextView>(R.id.detailDescription)
        val codeText = findViewById<TextView>(R.id.detailCode)
        val codeHeader = findViewById<View>(R.id.codeHeader)
        val codeScroll = findViewById<View>(R.id.codeScroll)
        val btnCopyCode = findViewById<MaterialButton>(R.id.btnCopyCode)
        val btnShareQR = findViewById<MaterialButton>(R.id.btnShareQR)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        val zoomSlider = findViewById<Slider>(R.id.zoomSlider)
        val zoomCard = findViewById<View>(R.id.zoomCard)
        val fabZoom = findViewById<FloatingActionButton>(R.id.fabZoom)

        // Setup Zoom Slider
        zoomSlider.addOnChangeListener { _, value, _ ->
            descText.textSize = value
            codeText.textSize = value - 2f // Keep code slightly smaller
            resetHideTimer()
        }

        fabZoom.setOnClickListener {
            val root = zoomCard.parent as ViewGroup
            TransitionManager.beginDelayedTransition(root, AutoTransition().apply {
                duration = 300
            })

            fabZoom.visibility = View.GONE
            zoomCard.visibility = View.VISIBLE
            resetHideTimer()
        }

        zoomCard.setOnClickListener { resetHideTimer() }

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // Keep it clean
        toolbar.setNavigationOnClickListener { finish() }

        // Extract the data passed from the Intent
        val id = intent.getStringExtra("EXTRA_ID") ?: ""
        val title = intent.getStringExtra("EXTRA_TITLE") ?: ""
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: ""
        val description = intent.getStringExtra("EXTRA_DOCUMENT") ?: ""
        val code = intent.getStringExtra("EXTRA_CODE")

        val currentDoc = Doc(id, title, description, code, category)

        btnShareQR.setOnClickListener {
            showQRCodeDialog(currentDoc)
        }

        // Set the data to the views
        titleText.text = title
        categoryText.text = category.uppercase()
        descText.text = description

        // If code exists, show it with syntax highlighting.
        if (!code.isNullOrBlank()) {
            codeText.text = highlightCode(code, category)
            codeHeader.visibility = View.VISIBLE
            codeScroll.visibility = View.VISIBLE

            btnCopyCode.setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = android.content.ClipData.newPlainText("Code", code)
                clipboard.setPrimaryClip(clip)

                // Copy animation
                val originalText = btnCopyCode.text
                val originalIcon = btnCopyCode.icon
                btnCopyCode.text = getString(R.string.copied)
                btnCopyCode.setIconResource(R.drawable.ic_check)

                btnCopyCode.postDelayed({
                    btnCopyCode.text = originalText
                    btnCopyCode.icon = originalIcon
                }, 2000)
            }
        } else {
            codeHeader.visibility = View.GONE
            codeScroll.visibility = View.GONE
        }
    }

    private fun showQRCodeDialog(doc: Doc) {
        val payload = QRUtils.encodeDocToBase64(doc)
        val uri = "bdoci://share?payload=$payload"

        // QR Code max capacity is ~3KB. If it's too large, notify the user.
        if (uri.length > 2900) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Document Too Large")
                .setMessage("This document is too large to share via QR code. Try reducing the content.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val bitmap = QRUtils.generateQRCode(uri)

        if (bitmap != null) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_qr_code, null)
            val qrImageView = dialogView.findViewById<ImageView>(R.id.qrImageView)
            qrImageView.setImageBitmap(bitmap)

            MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show()
        } else {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun highlightCode(code: String, categoryHint: String? = null): Spannable {
        val spannable = SpannableString(code)
        val lang = detectLanguage(code, categoryHint)

        // Gruvbox Dark Theme Palette
        val colorBg = "#282828"
        val colorFg = "#EBDBB2"
        val colorRed = "#FB4934"
        val colorGreen = "#B8BB26"
        val colorYellow = "#FABD2F"
        val colorBlue = "#83A598"
        val colorPurple = "#D3869B"
        val colorAqua = "#8EC07C"
        val colorOrange = "#FE8019"
        val colorGray = "#928374"

        val patterns = when (lang) {
            "python" -> listOf(
                "\\b(def|class|if|else|elif|for|while|return|import|from|as|try|except|finally|with|lambda|None|True|False|and|or|not|is|in|pass|break|continue|yield|async|await|global|nonlocal|assert|del)\\b" to colorRed,
                "(?m)#.*$" to colorGray,
                "(['\"])(?:\\\\.|(?!\\1).)*\\1" to colorGreen,
                "\\b\\d+\\b" to colorPurple,
                "\\b(self|cls)\\b" to colorBlue,
                "@[a-zA-Z_][a-zA-Z0-9_]*" to colorYellow
            )
            "java" -> listOf(
                "\\b(public|private|protected|static|final|native|synchronized|abstract|transient|volatile|class|interface|enum|extends|implements|package|import|if|else|switch|case|default|while|do|for|break|continue|return|throw|throws|try|catch|finally|this|super|instanceof|new|true|false|null|void|int|long|short|byte|char|boolean|float|double)\\b" to colorRed,
                "//.*|(?s)/\\*.*?\\*/" to colorGray,
                "\".*?\"" to colorGreen,
                "\\b\\d+\\b" to colorPurple,
                "@[a-zA-Z_][a-zA-Z0-9_]*" to colorYellow
            )
            "javascript" -> listOf(
                "\\b(const|let|var|function|class|if|else|for|while|do|switch|case|break|continue|return|try|catch|finally|throw|new|this|super|import|export|from|as|default|await|async|yield|null|undefined|true|false|typeof|instanceof|delete|in|void|with|debugger)\\b" to colorRed,
                "//.*|(?s)/\\*.*?\\*/" to colorGray,
                "\".*?\"|'.*?'|`.*?`" to colorGreen,
                "\\b\\d+\\b" to colorPurple,
                "\\b(document|window|console|Math|JSON)\\b" to colorBlue
            )
            "bash", "shell" -> listOf(
                "\\b(if|then|else|elif|fi|case|esac|for|select|while|until|do|done|in|function|time|local|readonly|export|return|exit|break|continue)\\b" to colorRed,
                "(?m)#.*$" to colorGray,
                "(['\"])(?:\\\\.|(?!\\1).)*\\1" to colorGreen,
                "\\b\\d+\\b" to colorPurple,
                "\\$[a-zA-Z_][a-zA-Z0-9_]*|\\$\\{.*?\\}" to colorBlue,
                "\\b(echo|printf|cd|pwd|ls|cat|grep|sed|awk|read|source|alias|unalias|set|unset|shift|trap|wait|eval|exec)\\b" to colorAqua
            )
            "sql" -> listOf(
                "(?i)\\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|AND|OR|JOIN|LEFT|RIGHT|INNER|OUTER|ON|GROUP\\s+BY|ORDER\\s+BY|HAVING|LIMIT|CREATE|TABLE|DROP|ALTER|INDEX|PRIMARY\\s+KEY|FOREIGN\\s+KEY|REFERENCES|VALUES|INTO|DISTINCT|AS|UNION|ALL|EXISTS|BETWEEN|LIKE|IN|IS|NULL|NOT|ANY|SOME|CASE|WHEN|THEN|ELSE|END|DATABASE|SCHEMA|VIEW|TRIGGER|PROCEDURE|FUNCTION|RETURNS|DECLARE|SET|GRANT|REVOKE)\\b" to colorRed,
                "(?i)\\b(INT|INTEGER|SMALLINT|BIGINT|VARCHAR|TEXT|DATE|TIME|TIMESTAMP|BOOLEAN|REAL|FLOAT|DOUBLE|DECIMAL|NUMERIC|CHAR|CHARACTER|BLOB|CLOB|UUID|JSON|XML)\\b" to colorYellow,
                "--.*|(?s)/\\*.*?\\*/" to colorGray,
                "'.*?'|\".*?\"" to colorGreen,
                "\\b\\d+\\b" to colorPurple
            )
            "c", "cpp" -> listOf(
                "\\b(if|else|while|for|return|break|continue|switch|case|default|try|catch|throw|class|struct|enum|union|public|private|protected|static|virtual|override|final|inline|const|constexpr|volatile|mutable|explicit|noexcept|template|typename|using|namespace|typedef|extern|auto|decltype|sizeof|alignof|typeid|new|delete|this|friend|operator|dynamic_cast|static_cast|reinterpret_cast|const_cast|bool|char|short|int|long|float|double|void|nullptr|true|false|unsigned|signed)\\b" to colorRed,
                "//.*|(?s)/\\*.*?\\*/" to colorGray,
                "\".*?\"|'.*?'" to colorGreen,
                "\\b\\d+\\b" to colorPurple,
                "#\\s*\\w+" to colorAqua
            )
            else -> listOf( // Kotlin / Default
                "\\b(val|var|fun|class|interface|object|if|else|when|for|while|return|import|package|null|true|false|try|catch|finally|throw|in|as|is|super|this|typealias|constructor|init|companion|field|it|suspend|launch|async|await|delay|yield|inline|reified|sealed|data|internal|expect|actual)\\b" to colorRed,
                "\\b(String|Int|Boolean|Long|Float|Double|Any|Unit|List|Map|Set|Result|Throwable|Exception)\\b" to colorYellow,
                "//.*|(?s)/\\*.*?\\*/" to colorGray,
                "\".*?\"" to colorGreen,
                "\\b\\d+\\b" to colorPurple,
                "@[a-zA-Z_][a-zA-Z0-9_]*" to colorAqua
            )
        }

        patterns.forEach { (regex, color) ->
            val pattern = Pattern.compile(regex)
            applySpan(spannable, pattern, Color.parseColor(color))
        }

        return spannable
    }

    private fun detectLanguage(code: String, category: String?): String {
        val cat = category?.lowercase() ?: ""
        return when {
            cat.contains("python") -> "python"
            cat.contains("java") && !cat.contains("script") -> "java"
            cat.contains("js") || cat.contains("javascript") || cat.contains("node") -> "javascript"
            cat.contains("bash") || cat.contains("shell") || cat.contains("zsh") || cat.contains("sh") -> "bash"
            cat.contains("sql") || cat.contains("database") || cat.contains("sqlite") -> "sql"
            cat.contains("c++") || cat.contains("cpp") -> "cpp"
            cat.contains("c") -> "c"
            cat.contains("kotlin") || cat.contains("android") -> "kotlin"
            // Fallback for generic categories
            code.contains("#include") -> "cpp"
            code.contains("def ") || (code.contains("import ") && code.contains(":")) -> "python"
            code.contains("public class ") || code.contains("System.out.println") -> "java"
            code.contains("const ") || code.contains("let ") || code.contains("function ") -> "javascript"
            code.startsWith("#!") || code.contains("echo ") -> "bash"
            code.contains("SELECT ", ignoreCase = true) && code.contains("FROM ", ignoreCase = true) -> "sql"
            else -> "kotlin"
        }
    }

    private fun applySpan(spannable: Spannable, pattern: Pattern, color: Int) {
        val matcher = pattern.matcher(spannable)
        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(color),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}