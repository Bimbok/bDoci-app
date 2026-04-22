package com.example.bdoci.app

import android.app.Application
import com.example.bdoci.database.AppDatabase
import com.example.bdoci.network.ApiClient
import com.example.bdoci.repository.DocRepository

class BDociApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DocRepository(ApiClient.apiService, database.docDao()) }
}
