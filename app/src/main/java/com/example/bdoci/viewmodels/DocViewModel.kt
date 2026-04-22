package com.example.bdoci.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdoci.models.Doc
import com.example.bdoci.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DocViewModel : ViewModel() {

    private val _documents = MutableLiveData<List<Doc>>()
    val documents: LiveData<List<Doc>> = _documents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Keep a full copy for filtering
    private var fullList: List<Doc> = emptyList()

    fun fetchDocuments() {
        // Only fetch if we don't have data yet
        if (_documents.value != null) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getAllDocs()
                fullList = response
                _documents.postValue(response)
                _isLoading.postValue(false)
            } catch (e: HttpException) {
                _isLoading.postValue(false)
                val message = when (e.code()) {
                    429 -> "Too Many Requests (Rate Limited)"
                    else -> "Server error: ${e.code()}"
                }
                _errorMessage.postValue(message)
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _errorMessage.postValue("Failed to load data: ${e.message}")
            }
        }
    }

    fun filterDocuments(query: String) {
        val filtered = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.title.contains(query, ignoreCase = true) }
        }
        _documents.value = filtered
    }
}
