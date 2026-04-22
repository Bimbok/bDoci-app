package com.example.bdoci

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.models.Doc
import com.example.bdoci.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

import androidx.activity.viewModels
import com.example.bdoci.viewmodels.DocViewModel
import com.example.bdoci.utils.NetworkUtils

class Dashboard : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var offlineIndicator: View
    private lateinit var docAdapter: DocAdapter

    private lateinit var networkUtils: NetworkUtils
    private val viewModel: DocViewModel by viewModels { DocViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI Elements
        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)
        progressBar = findViewById(R.id.progressBar)
        offlineIndicator = findViewById(R.id.offlineIndicator)

        networkUtils = NetworkUtils(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe ViewModel
        setupObservers()

        // Setup search
        setupSearch()

        // Trigger fetch (it only fetches if data is empty)
        viewModel.fetchDocuments()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.documents.collect { documents ->
                        if (::docAdapter.isInitialized) {
                            docAdapter.updateData(documents)
                        } else {
                            docAdapter = DocAdapter(documents)
                            recyclerView.adapter = docAdapter
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.errorMessage.collect { message ->
                        message?.let {
                            Toast.makeText(this@Dashboard, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                launch {
                    networkUtils.isOnline.collect { isOnline ->
                        offlineIndicator.visibility = if (isOnline) View.GONE else View.VISIBLE
                        if (!isOnline) {
                            Toast.makeText(this@Dashboard, "You are offline", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupSearch() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.filterDocuments(s.toString().trim())
            }
        })
    }
}