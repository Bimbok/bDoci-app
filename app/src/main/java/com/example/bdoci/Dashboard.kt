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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.models.Doc
import com.example.bdoci.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class Dashboard : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var docAdapter: DocAdapter

    // We keep a copy of the full list so we don't lose data when filtering
    private var fullDocumentList: List<Doc> = emptyList()

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

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup the search bar listener
        setupSearch()

        // Fetch the data
        fetchDataFromBackend()
    }

    private fun setupSearch() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            // This runs every time a letter is typed or deleted
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()

                // Filter the master list based on the title
                val filteredList = fullDocumentList.filter { doc ->
                    doc.title.contains(searchText, ignoreCase = true)
                }

                // Send the new filtered list to the adapter
                if (::docAdapter.isInitialized) {
                    docAdapter.updateData(filteredList)
                }
            }
        })
    }

    private fun fetchDataFromBackend() {
        // 1. Show the loading spinner before fetching
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val documents = ApiClient.apiService.getAllDocs()

                withContext(Dispatchers.Main) {
                    // 2. Hide the spinner when successful
                    progressBar.visibility = View.GONE

                    // Save to our master list
                    fullDocumentList = documents

                    // Initialize the adapter and set it
                    docAdapter = DocAdapter(documents)
                    recyclerView.adapter = docAdapter
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    // Hide spinner on error
                    progressBar.visibility = View.GONE
                    val code = e.code()
                    val message = when (code) {
                        429 -> "Too Many Requests (Rate Limited)"
                        else -> "Server error: $code"
                    }
                    Toast.makeText(this@Dashboard, message, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide spinner on error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@Dashboard, "Failed to load data: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }
}