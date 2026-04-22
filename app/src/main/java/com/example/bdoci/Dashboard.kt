package com.example.bdoci

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class Dashboard : AppCompatActivity() {

    // Declare the RecyclerView variable
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Setup Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchDataFromBackend()
    }

    private fun fetchDataFromBackend() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch data from your Express backend
                val documents = ApiClient.apiService.getAllDocs()

                withContext(Dispatchers.Main) {
                    // Success! Connect the data to the RecyclerView using the Adapter
                    val adapter = DocAdapter(documents)
                    recyclerView.adapter = adapter
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
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
                    Toast.makeText(this@Dashboard, "Failed to load data: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }
}