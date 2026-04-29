package com.example.bdoci.repository

import com.example.bdoci.database.DocDao
import com.example.bdoci.database.FavoriteDao
import com.example.bdoci.models.Doc
import com.example.bdoci.models.Favorite
import com.example.bdoci.network.ApiService

class DocRepository(
    private val apiService: ApiService,
    private val docDao: DocDao,
    private val favoriteDao: FavoriteDao
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
        val docs = docDao.getAllDocs()
        val favoriteIds = favoriteDao.getAllFavoriteIds().toSet()
        docs.forEach { it.isFavorite = favoriteIds.contains(it.id) }
        return docs
    }

    suspend fun insertDoc(doc: Doc) {
        docDao.insertDoc(doc)
    }

    suspend fun toggleFavorite(docId: String, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.insert(Favorite(docId))
        } else {
            favoriteDao.delete(Favorite(docId))
        }
    }
}
