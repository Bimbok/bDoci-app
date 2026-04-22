package com.example.bdoci

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Extract the data passed from the Intent
        val title = intent.getStringExtra("EXTRA_TITLE")
        val category = intent.getStringExtra("EXTRA_CATEGORY")
        val description = intent.getStringExtra("EXTRA_DOCUMENT")
        val code = intent.getStringExtra("EXTRA_CODE")

        // Set the data to the views
        titleText.text = title
        categoryText.text = category?.uppercase()
        descText.text = description

        // If code exists, show it. Otherwise, hide the black box.
        if (!code.isNullOrBlank()) {
            codeText.text = code
            codeContainer.visibility = View.VISIBLE
        } else {
            codeContainer.visibility = View.GONE
        }
    }
}