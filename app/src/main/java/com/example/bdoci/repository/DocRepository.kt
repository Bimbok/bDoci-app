package com.example.bdoci.repository

import com.example.bdoci.database.DocDao
import com.example.bdoci.models.Doc
import com.example.bdoci.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DocRepository(
    private val apiService: ApiService,
    private val docDao: DocDao
) {
    suspend fun refreshDocs() {
        try {
            val docs = apiService.getAllDocs()
            docDao.deleteAll()
            docDao.insertAll(docs)
        } catch (e: Exception) {
            // Log error or handle it
            throw e
        }
    }

    suspend fun getLocalDocs(): List<Doc> {
        return docDao.getAllDocs()
    }

    suspend fun insertDoc(doc: Doc) {
        docDao.insertDoc(doc)
    }
}
