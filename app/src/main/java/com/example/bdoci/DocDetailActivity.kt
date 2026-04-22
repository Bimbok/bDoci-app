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
import com.google.android.material.button.MaterialButton
import com.example.bdoci.models.Doc
import com.example.bdoci.utils.QRUtils
import android.widget.ImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DocDetailActivity : AppCompatActivity() {

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
        val codeContainer = findViewById<LinearLayout>(R.id.codeContainer)
        val codeHeader = findViewById<View>(R.id.codeHeader)
        val codeScroll = findViewById<View>(R.id.codeScroll)
        val btnCopyCode = findViewById<MaterialButton>(R.id.btnCopyCode)
        val btnShareQR = findViewById<MaterialButton>(R.id.btnShareQR)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)

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
        categoryText.text = category?.uppercase()
        descText.text = description

        // If code exists, show it with syntax highlighting.
        if (!code.isNullOrBlank()) {
            codeText.text = highlightCode(code)
            codeHeader.visibility = View.VISIBLE
            codeScroll.visibility = View.VISIBLE

            btnCopyCode.setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = android.content.ClipData.newPlainText("Code", code)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
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

    private fun highlightCode(code: String): Spannable {
        val spannable = SpannableString(code)

        // Basic Highlight Rules (Regex)
        val keywords = Pattern.compile("\\b(val|var|fun|class|interface|object|if|else|when|for|while|return|import|package|null|true|false)\\b")
        val types = Pattern.compile("\\b(String|Int|Boolean|Long|Float|Double|Any|Unit|List|Map|Set)\\b")
        val strings = Pattern.compile("\".*?\"")
        val comments = Pattern.compile("//.*|/\\*.*?\\*/", Pattern.DOTALL)
        val numbers = Pattern.compile("\\b\\d+\\b")

        applySpan(spannable, keywords, Color.parseColor("#FF7B72")) // Reddish
        applySpan(spannable, types, Color.parseColor("#D2A8FF"))    // Purple
        applySpan(spannable, strings, Color.parseColor("#A5D6FF"))  // Blue
        applySpan(spannable, comments, Color.parseColor("#8B949E")) // Grey
        applySpan(spannable, numbers, Color.parseColor("#D2A8FF"))  // Purple

        return spannable
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