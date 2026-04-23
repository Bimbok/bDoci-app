package com.example.bdoci.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bdoci.app.BDociApp
import com.example.bdoci.models.Doc
import com.example.bdoci.repository.DocRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DocViewModel(private val repository: DocRepository) : ViewModel() {

    private val _documents = MutableStateFlow<List<Doc>>(emptyList())
    val documents: StateFlow<List<Doc>> = _documents

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Keep a full copy for filtering
    private var fullList: List<Doc> = emptyList()
    
    // Track current filter state
    private var currentQuery: String = ""
    private var currentCategory: String = "All"

    init {
        fetchDocuments()
    }

    fun fetchDocuments() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Load from local database first (Instant UI update)
                val localDocs = repository.getLocalDocs()
                if (localDocs.isNotEmpty()) {
                    fullList = localDocs
                    _categories.value = localDocs.map { it.category }.distinct().sorted()
                    applyFilters()
                }

                // 2. Refresh from network in background
                repository.refreshDocs()
                
                // 3. Update with fresh data from local database
                val freshDocs = repository.getLocalDocs()
                fullList = freshDocs
                _categories.value = freshDocs.map { it.category }.distinct().sorted()
                applyFilters()
                _isLoading.value = false
            } catch (e: HttpException) {
                _isLoading.value = false
                val message = when (e.code()) {
                    429 -> "Too Many Requests (Rate Limited)"
                    else -> "Server error: ${e.code()}"
                }
                if (_documents.value.isEmpty()) {
                    _errorMessage.value = message
                }
            } catch (e: Exception) {
                _isLoading.value = false
                if (_documents.value.isEmpty()) {
                    _errorMessage.value = "Failed to load data: ${e.message}"
                }
            }
        }
    }

    fun filterDocuments(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun filterByCategory(category: String) {
        currentCategory = category
        applyFilters()
    }

    fun importDocument(doc: Doc) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertDoc(doc)
                // Refresh list
                val freshDocs = repository.getLocalDocs()
                fullList = freshDocs
                applyFilters()
            } catch (e: Exception) {
                _errorMessage.value = "Import failed: ${e.message}"
            }
        }
    }

    private fun applyFilters() {
        var filtered = fullList

        // 1. Apply category filter
        if (currentCategory != "All") {
            filtered = filtered.filter { it.category == currentCategory }
        }

        // 2. Apply search query filter
        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter { 
                it.title.contains(currentQuery, ignoreCase = true) || 
                it.category.contains(currentQuery, ignoreCase = true) 
            }
        }

        _documents.value = filtered
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BDociApp
                return DocViewModel(application.repository) as T
            }
        }
    }
}
